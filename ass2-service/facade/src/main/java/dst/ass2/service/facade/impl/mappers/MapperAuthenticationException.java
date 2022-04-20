package dst.ass2.service.facade.impl.mappers;

import dst.ass2.service.api.auth.AuthenticationException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class MapperAuthenticationException implements ExceptionMapper<AuthenticationException> {
    @Override
    public Response toResponse(AuthenticationException e) {
        return Response
                .status(Response.Status.UNAUTHORIZED)
                .entity("AuthenticationException: " + e.getMessage())
                .build();
    }
}
