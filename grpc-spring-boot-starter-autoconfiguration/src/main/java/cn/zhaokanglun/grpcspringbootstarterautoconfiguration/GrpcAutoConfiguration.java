package cn.zhaokanglun.grpcspringbootstarterautoconfiguration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcAutoConfiguration{

    @Value("${spring.application.name:grpc-service}")
    String name;

    @Value("${server.port:9191}")
    int port;

    @Bean
    ServerRegister server() {
        return new ServerRegister(name, port);
    }
    
}
