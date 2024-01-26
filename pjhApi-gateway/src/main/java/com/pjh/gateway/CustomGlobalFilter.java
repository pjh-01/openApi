package com.pjh.gateway;

import com.pjh.interfac.InnerInterfaceInfoService;
import com.pjh.interfac.InnerUserInterfaceInfoService;
import com.pjh.interfac.InnerUserService;
import com.pjh.interfac.model.InterfaceInfo;
import com.pjh.interfac.model.User;
import com.pjh.pjhapiclientsdk.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;


    public static final List<String> WHITE_LIST = Arrays.asList("127.0.0.1", "localhost");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        1.用户发送请求到API网关
//        2.请求日志
        ServerHttpRequest request = exchange.getRequest();
        log.info("请求id: " + request.getId());
        log.info("请求方法:" + request.getMethod());
        log.info("请求url:" + request.getURI());
        log.info("请求参数:" + request.getQueryParams());
        log.info("请求体:" + request.getBody());
        log.info("请求子路径:" + request.getPath());
        log.info("请求地址:" + request.getLocalAddress());
        ServerHttpResponse response = exchange.getResponse();
        String host = request.getURI().getHost();

//        3.(黑白名单)
        if (!WHITE_LIST.contains(host)) {
            return handleNoAuth(response);
        }
//        4.用户鉴权（判断ak、sk是否合法）
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        //获取用户信息
        User user = null;
        try {
            user = innerUserService.getInvokerUser(accessKey);
        } catch (Exception e) {
            log.info("获取用户失败！可能数据库根本没有此用户！");
            return handleNoAuth(response);
        }
        //先判断轻量级的条件，比如随机数
        if (nonce.length() != 5) {
            return handleInvokeError(response);
        }
        //再判断时间间隔，防止重放之类的
        long currentTime = System.currentTimeMillis() / 1000;
        if (currentTime - Long.parseLong(timestamp) > 3 * 60L) {
            return handleInvokeError(response);
        }
        //最后是对sign的检查
        String copySign = SignUtils.getSign(accessKey, user.getSecretKey());
        if (!copySign.equals(sign)) {
            return handleInvokeError(response);
        }
//        5.请求的模拟接口是否存在？
        InterfaceInfo interfaceInfo = innerInterfaceInfoService.isExist(request.getPath().toString(), request.getMethod().toString());
        if(interfaceInfo==null){
            return handleInvokeError(response);
        }
//        6。请求转发，调用摸拟接口
//        7.响应日志
//        8.调用成功，接口调用次数+1
//        9.调用失败，返回一个规范的错误码
//        return chain.filter(exchange);
        return handleResponse(exchange,chain, interfaceInfo.getId(), user.getId());
    }

    /**
     * 处理响应
     *
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                // 装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        // 7. 调用成功，接口调用次数 + 1 invokeCount
                        try {
                            innerUserInterfaceInfoService.useOneChance(userId, interfaceInfoId);
                        } catch (Exception e) {
                            log.error("invokeCount error", e);
                        }
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 往返回值里写数据
                            // 拼接字符串
                            return super.writeWith(
                                    fluxBody.map(dataBuffer -> {
                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(content);
                                        DataBufferUtils.release(dataBuffer);//释放掉内存
                                        // 构建日志
                                        StringBuilder sb2 = new StringBuilder(200);
                                        List<Object> rspArgs = new ArrayList<>();
                                        rspArgs.add(originalResponse.getStatusCode());
                                        String data = new String(content, StandardCharsets.UTF_8); //data
                                        sb2.append(data);
                                        // 打印日志
                                        log.info("响应结果：" + data);
                                        return bufferFactory.wrap(content);
                                    }));
                        } else {
                            // 8. 调用失败，返回一个规范的错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 设置 response 对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange); // 降级处理返回数据
        } catch (Exception e) {
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }

    public Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    public Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }

}