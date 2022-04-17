package dst.ass2.service.trip.impl.mappers;

import dst.ass2.service.api.trip.InvalidTripException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class MapperInvalidTripException implements ExceptionMapper<InvalidTripException> {
    @Override
    public Response toResponse(InvalidTripException e) {
        return Response
                .status(Response.Status.NOT_ACCEPTABLE)
                .entity("InvalidTripException: " + e.getMessage())
                .build();
    }
}
