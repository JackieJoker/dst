package dst.ass2.service.facade.impl.mappers;

import dst.ass2.service.api.auth.NoSuchUserException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class MapperNoSuchUserException implements ExceptionMapper<NoSuchUserException> {
    @Override
    public Response toResponse(NoSuchUserException e) {
        return Response
                .status(Response.Status.UNAUTHORIZED)
                .entity("NoSuchUserException: " + e.getMessage())
                .build();
    }
}
