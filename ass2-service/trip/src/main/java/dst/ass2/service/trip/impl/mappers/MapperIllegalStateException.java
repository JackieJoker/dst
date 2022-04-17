package dst.ass2.service.trip.impl.mappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class MapperIllegalStateException implements ExceptionMapper<IllegalStateException> {
    @Override
    public Response toResponse(IllegalStateException e) {
        return Response
                .status(Response.Status.CONFLICT)
                .entity("IllegalStateException: " + e.getMessage())
                .build();
    }
}
