package cn.zhaokanglun.grpcspringbootstarterautoconfiguration;

import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.zookeeper.serviceregistry.ServiceInstanceRegistration;
import org.springframework.cloud.zookeeper.serviceregistry.ZookeeperRegistration;
import org.springframework.cloud.zookeeper.serviceregistry.ZookeeperServiceRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import cn.zhaokanglun.grpcspringbootstarterautoconfiguration.annotations.GrpcService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;

public class ServerRegister implements ApplicationRunner, ApplicationContextAware {

    private ApplicationContext context;

    @Autowired
    private ZookeeperServiceRegistry registry;

    @Autowired
    private GreeterImpl greeterImpl;

    private static final Logger logger = Logger.getLogger(ServerRegister.class.getName());

    private String name;
    private int port;
    private Server server;

    public ServerRegister(String name, int port) {
        this.name = name;
        this.port = port;
    }

    public Server getServer() {
        return server;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Map<String, Object> servicesMap = context.getBeansWithAnnotation(GrpcService.class);
        LinkedList<ServerServiceDefinition> services = new LinkedList<>();
        ZookeeperRegistration registration;

        Thread grpcThread;

        for (Map.Entry<String, Object> entry : servicesMap.entrySet()) {
            if (entry.getValue() instanceof ServerServiceDefinition) {
                services.add((ServerServiceDefinition) entry.getValue());
            }
        }

        System.out.println(greeterImpl);

        registration = ServiceInstanceRegistration.builder()
                .defaultUriSpec()
                .address(InetAddress.getLocalHost().getHostAddress())
                .port(port)
                .name(name)
                .build();
        registry.register(registration);

        server = ServerBuilder.forPort(port).addServices(services).build();

        grpcThread = new Thread(() -> {
            try {
                server.start();
                logger.info("Server started, listening on " + port);

                // blocking untill shutdown
                server.awaitTermination();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        grpcThread.setName("grpc-server");
        grpcThread.start();
    }

}
