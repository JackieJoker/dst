package dst.ass2.service.auth.grpc.impl;

import dst.ass2.service.api.auth.IAuthenticationService;
import dst.ass2.service.auth.grpc.GrpcServerProperties;
import dst.ass2.service.auth.grpc.IGrpcServerRunner;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

@Singleton
@ManagedBean
public class GrpcServerRunner implements IGrpcServerRunner {
    @Inject
    private GrpcServerProperties properties;
    @Inject
    private IAuthenticationService authenticationService;

    @Override
    public void run() throws IOException {
        int port = properties.getPort();
        Server server = ServerBuilder.forPort(port).addService(new AuthService(authenticationService)).build();
        server.start();
        System.out.println("Server started, listening on port:" + port);
    }
}
