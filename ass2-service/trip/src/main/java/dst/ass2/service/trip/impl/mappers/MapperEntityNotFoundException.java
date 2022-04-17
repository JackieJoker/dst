package dst.ass2.service.trip.impl.mappers;

import dst.ass2.service.api.trip.EntityNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class MapperEntityNotFoundException implements ExceptionMapper<EntityNotFoundException> {
    @Override
    public Response toResponse(EntityNotFoundException e) {
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity("EntityNotFoundException: " + e.getMessage())
                .build();
    }
}
