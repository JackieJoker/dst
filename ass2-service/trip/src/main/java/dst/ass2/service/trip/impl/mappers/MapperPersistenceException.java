package dst.ass2.service.trip.impl.mappers;

import javax.persistence.PersistenceException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class MapperPersistenceException implements ExceptionMapper<PersistenceException> {
    @Override
    public Response toResponse(PersistenceException e) {
        return Response
                .status(Response.Status.NOT_ACCEPTABLE)
                .entity("PersistenceException: " + e.getMessage())
                .build();
    }
}
