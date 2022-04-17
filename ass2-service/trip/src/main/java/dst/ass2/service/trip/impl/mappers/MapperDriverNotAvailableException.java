package dst.ass2.service.trip.impl.mappers;

import dst.ass2.service.api.trip.DriverNotAvailableException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class MapperDriverNotAvailableException implements ExceptionMapper<DriverNotAvailableException> {
    @Override
    public Response toResponse(DriverNotAvailableException e) {
        return Response
                .status(Response.Status.CONFLICT)
                .entity("DriverNotAvailableException: " + e.getMessage())
                .build();
    }
}
