package cn.zhaokanglun.grpcspringbootstarterautoconfiguration;

import cn.zhaokanglun.grpcspringbootstarterautoconfiguration.annotations.GrpcService;
import io.grpc.stub.StreamObserver;

@GrpcService
class GreeterImpl extends GreeterGrpc.GreeterImplBase {

    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
      HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }
}