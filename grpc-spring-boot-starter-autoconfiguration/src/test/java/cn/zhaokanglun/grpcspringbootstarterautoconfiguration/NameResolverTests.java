package cn.zhaokanglun.grpcspringbootstarterautoconfiguration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import cn.zhaokanglun.grpcspringbootstarterautoconfiguration.GreeterGrpc.GreeterBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.NameResolverRegistry;

@SpringBootTest
public class NameResolverTests {

    @Autowired
    MyNameResolverProvider myNameResolverProvider;

    @Test
    void test() {
        NameResolverRegistry.getDefaultRegistry().register(myNameResolverProvider);

        ManagedChannel channel = ManagedChannelBuilder
                .forTarget("grpc-service")
                .build();

        GreeterBlockingStub blockingStub = GreeterGrpc.newBlockingStub(channel);

        blockingStub.sayHello(HelloRequest.newBuilder().build());

    }
}
