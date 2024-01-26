package com.pjh.pjhapiclientsdk;


import com.pjh.pjhapiclientsdk.client.Client;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("pjh.client")
@ComponentScan
@Data
public class PjhApiClientSDKConfig {

    private String accessKey;

    private String secretKey;

    @Bean
    public Client getClient(){
        return new Client(accessKey,secretKey);
    }

}
