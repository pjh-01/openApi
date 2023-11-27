2023年11月26日 晚
Q1 如何保证API接口的安全性？
    使用API签名认证。就像jwt一样，签名和调用请求都是绑定在一起的，每请求一次就发一次签名，因为请求是无状态的
    然后将一下必要的信息，比如用户信息、签名放在请求头中一起发送即可
Q2 如何签名？
    签名好比加密，所以可以使用对称加密，非对称加密。考虑到安全性，使用非对称加密最好，比如MD5
    可以在User表里为每一个用户分配accessKey、secretKey，后续请求使用他们MD5当作签名即可
    但需要注意的是：不能在请求中携带secretKey，因为请求都是会被拦截的，不能泄露secretKey
Q3 非对称加密无法解密，那服务端怎么判断？
    确实无法解密，所以我们只要使用与发送请求一模一样的参数进行同样的加密，对比加密结果即可
    比如根据用户信息去数据库中查对于的accessKey、secretKey去还原加密过程
Q4 以上流程都完成后，发现调用方式太过复杂，如何简单地给用户调用呢？
    发布为SDK，就像任何一个starter一样，用户只要在yaml中配置参数就可以使用
    1.新建springBoot项目，勾选Configuration-processor依赖，他可以帮助我们发布为maven依赖
    2.编写必要代码
    3.删除启动类，删除test下的类，新建一个Config类，在类上打上注解
        @Configuration
        @ConfigurationProperties("xxx.xxx")//你想要用户在yaml中配置时使用的路径，比如server.port
        @ComponentScan
        @Data
        public class PjhApiClientSDKConfig {
        
            private String accessKey;
        
            private String secretKey;

            @Bean
            public Client getCilent(){
                return new Client(accessKey,secretKey);
            }
        }
    4.在resources下创建META-INF文件夹，在其中创建spring.factories
    配置org.springframework.boot.autoconfigure.EnableAutoConfiguration=com.pjh.pjhapiclientsdk.PjhApiClientSDKConfig
    5.在maven中运行install指令，发布到本地maven仓库中
    6.用户映入我的依赖，maven坐标就在原来的pom.xml中

2023年11月27日 晚
Q1 怎么去发布接口？
    发布接口很简单，只需要修改status=1
    但我们需要先验证该接口确实是可用的，于是选择hutool的Http工具类去访问该接口，根据http状态码是否为200判断可达