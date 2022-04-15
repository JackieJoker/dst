package dst.ass2.service.auth.grpc.impl;

import com.google.rpc.Code;
import com.google.rpc.Status;
import dst.ass2.service.api.auth.AuthenticationException;
import dst.ass2.service.api.auth.IAuthenticationService;
import dst.ass2.service.api.auth.NoSuchUserException;
import dst.ass2.service.api.auth.proto.*;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;

public class AuthService extends AuthServiceGrpc.AuthServiceImplBase {
    private final IAuthenticationService authenticationService;

    public AuthService(IAuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public void authenticate(AuthenticationRequest request, StreamObserver<AuthenticationResponse> responseObserver) {
        try {
            String token = authenticationService.authenticate(request.getEmail(), request.getPassword());
            AuthenticationResponse response = AuthenticationResponse
                    .newBuilder()
                    .setToken(token)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (NoSuchUserException e) {
            Status status = Status
                    .newBuilder()
                    .setCode(Code.NOT_FOUND_VALUE)
                    .build();
            responseObserver.onError(StatusProto.toStatusRuntimeException(status));
        } catch (AuthenticationException e) {
            Status status = Status
                    .newBuilder()
                    .setCode(Code.UNAUTHENTICATED_VALUE)
                    .build();
            responseObserver.onError(StatusProto.toStatusRuntimeException(status));
        }
    }

    @Override
    public void validateToken(TokenValidationRequest request, StreamObserver<TokenValidationResponse> responseObserver) {
        boolean valid = authenticationService.isValid(request.getToken());
        TokenValidationResponse response = TokenValidationResponse
                .newBuilder()
                .setValid(valid)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
