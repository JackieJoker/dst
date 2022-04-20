package dst.ass2.service.facade.impl;

import dst.ass2.service.api.trip.*;
import dst.ass2.service.api.trip.rest.ITripServiceResource;
import dst.ass2.service.facade.Authenticated;
import org.glassfish.jersey.client.proxy.WebResourceFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/trips")
@Authenticated
public class TripServiceResourceFacade implements ITripServiceResource {

    @Inject
    private URI tripServiceURI;

    private ITripServiceResource tripServiceResource;

    @PostConstruct
    public void setup() {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(tripServiceURI).path("trips");
        tripServiceResource = WebResourceFactory.newResource(ITripServiceResource.class, webTarget);
    }

    @Override
    public Response createTrip(Long riderId, Long pickupId, Long destinationId) throws EntityNotFoundException, InvalidTripException {
        return tripServiceResource.createTrip(riderId, pickupId, destinationId);
    }

    @Override
    public Response confirm(Long tripId) throws EntityNotFoundException, InvalidTripException {
        return tripServiceResource.confirm(tripId);
    }

    @Override
    public Response getTrip(Long tripId) throws EntityNotFoundException {
        return tripServiceResource.getTrip(tripId);
    }

    @Override
    public Response deleteTrip(Long tripId) throws EntityNotFoundException {
        return tripServiceResource.deleteTrip(tripId);
    }

    @Override
    public Response addStop(Long tripId, Long locationId) throws InvalidTripException, EntityNotFoundException {
        return tripServiceResource.addStop(tripId, locationId);
    }

    @Override
    public Response removeStop(Long tripId, Long locationId) throws InvalidTripException, EntityNotFoundException {
        return tripServiceResource.removeStop(tripId, locationId);
    }

    @Override
    public Response match(Long tripId, MatchDTO matchDTO) throws EntityNotFoundException, DriverNotAvailableException {
        return tripServiceResource.match(tripId, matchDTO);
    }

    @Override
    public Response complete(Long tripId, TripInfoDTO tripInfoDTO) throws EntityNotFoundException {
        return tripServiceResource.complete(tripId, tripInfoDTO);
    }

    @Override
    public Response cancel(Long tripId) throws EntityNotFoundException {
        return tripServiceResource.cancel(tripId);
    }
}
