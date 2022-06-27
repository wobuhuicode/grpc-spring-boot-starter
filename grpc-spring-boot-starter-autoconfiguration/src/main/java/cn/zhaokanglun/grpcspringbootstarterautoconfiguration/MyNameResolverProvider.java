package cn.zhaokanglun.grpcspringbootstarterautoconfiguration;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import io.grpc.NameResolver.Args;
import io.grpc.NameResolverProvider;
import io.grpc.Status;
import io.grpc.SynchronizationContext;

@Component
public class MyNameResolverProvider extends NameResolverProvider {

    @Autowired
    DiscoveryClient client;

    @Override
    protected boolean isAvailable() {
        return true;
    }

    @Override
    protected int priority() {
        return 10;
    }

    @Override
    public NameResolver newNameResolver(URI targetUri, Args args) {
        System.out.println("到达");
        return new MyNameResolver(targetUri, args);
    }

    @Override
    public String getDefaultScheme() {
        return "grpc";
    }

    class MyNameResolver extends NameResolver {
        private Args args;
        private URI targetUri;

        MyNameResolver(URI targetUri, Args args) {
            this.targetUri = targetUri;
            this.args = args;
        }

        @Override
        public void start(Listener2 listener) {
            SynchronizationContext context = args.getSynchronizationContext();
            List<SocketAddress> listAddr = new LinkedList<>();
            String serviceId = targetUri.getPath().substring(1);
            List<ServiceInstance> instances = client.getInstances(serviceId);
            List<EquivalentAddressGroup> listAddrGroup = new LinkedList<>();

            for (ServiceInstance instance : instances) {
                listAddr.add(new InetSocketAddress(instance.getHost(), instance.getPort()));
            }

            listAddrGroup.add(new EquivalentAddressGroup(listAddr));

            if (instances.size() > 0)
                context.execute(
                        () -> listener.onResult(ResolutionResult.newBuilder().setAddresses(listAddrGroup).build()));
            else
                context.execute(() -> listener.onError(Status.NOT_FOUND));
        }

        @Override
        public String getServiceAuthority() {
            return "zhaokanglun";
        }

        @Override
        public void shutdown() {
        }

    }
}
