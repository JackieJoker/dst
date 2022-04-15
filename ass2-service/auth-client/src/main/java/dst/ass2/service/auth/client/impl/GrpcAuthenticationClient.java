package dst.ass2.service.auth.client.impl;

import dst.ass2.service.api.auth.AuthenticationException;
import dst.ass2.service.api.auth.NoSuchUserException;
import dst.ass2.service.api.auth.proto.*;
import dst.ass2.service.api.auth.proto.AuthServiceGrpc.AuthServiceBlockingStub;
import dst.ass2.service.auth.client.AuthenticationClientProperties;
import dst.ass2.service.auth.client.IAuthenticationClient;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GrpcAuthenticationClient implements IAuthenticationClient {

    private static final Logger logger = Logger.getLogger(GrpcAuthenticationClient.class.getName());
    private ManagedChannel channel;
    private final AuthServiceBlockingStub blockingStub;

    public GrpcAuthenticationClient(AuthenticationClientProperties properties) {
        channel = ManagedChannelBuilder.forAddress(properties.getHost(), properties.getPort()).usePlaintext().build();
        blockingStub = AuthServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public String authenticate(String email, String password) throws NoSuchUserException, AuthenticationException {
        AuthenticationRequest request = AuthenticationRequest
                .newBuilder()
                .setEmail(email)
                .setPassword(password)
                .build();
        AuthenticationResponse response;
        try {
            response = blockingStub.authenticate(request);
            return response.getToken();
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new NoSuchUserException();
            } else if (e.getStatus().getCode() == Status.Code.UNAUTHENTICATED) {
                throw new AuthenticationException();
            } else {
                logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            }
            return null;
        }
    }

    @Override
    public boolean isTokenValid(String token) {
        TokenValidationRequest request = TokenValidationRequest
                .newBuilder()
                .setToken(token)
                .build();
        TokenValidationResponse response;
        try {
            response = blockingStub.validateToken(request);
            return response.getValid();
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return false;
        }
    }

    @Override
    public void close() {
        try {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
