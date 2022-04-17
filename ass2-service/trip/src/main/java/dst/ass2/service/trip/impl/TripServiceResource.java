package dst.ass2.service.trip.impl;

import dst.ass2.service.api.trip.*;
import dst.ass2.service.api.trip.rest.ITripServiceResource;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/trips")
public class TripServiceResource implements ITripServiceResource {
    @Inject
    ITripService tripService;

    @Override
    public Response createTrip(Long riderId, Long pickupId, Long destinationId) throws EntityNotFoundException, InvalidTripException {
        if (riderId == null || pickupId == null || destinationId == null) {
            throw new InvalidTripException("One of the parameters is null.");
        }
        TripDTO trip = tripService.create(riderId, pickupId, destinationId);
        return Response.status(Response.Status.OK).entity(trip.getId()).build();
    }

    @Override
    public Response confirm(Long tripId) throws EntityNotFoundException, InvalidTripException {
        tripService.confirm(tripId);
        return Response.status(Response.Status.OK).build();
    }

    @Override
    public Response getTrip(Long tripId) throws EntityNotFoundException {
        if (tripId == null)
            throw new EntityNotFoundException("Long tripId is null.");
        TripDTO trip = tripService.find(tripId);
        if (trip == null)
            throw new EntityNotFoundException("No entity found with ID " + tripId + ".");
        return Response.status(Response.Status.OK).entity(trip).build();
    }

    @Override
    public Response deleteTrip(Long tripId) throws EntityNotFoundException {
        tripService.delete(tripId);
        return Response.status(Response.Status.OK).build();
    }

    @Override
    public Response addStop(Long tripId, Long locationId) throws InvalidTripException, EntityNotFoundException {
        TripDTO trip = tripService.find(tripId);
        if (trip == null)
            throw new EntityNotFoundException("No entity found with ID " + tripId + ".");
        boolean stopAdded = tripService.addStop(trip, locationId);
        if(!stopAdded) throw new InvalidTripException("The stop can't be added to the trip.");
        return Response.status(Response.Status.OK).entity(trip.getFare()).build();
    }

    @Override
    public Response removeStop(Long tripId, Long locationId) throws InvalidTripException, EntityNotFoundException {
        TripDTO trip = tripService.find(tripId);
        if (trip == null)
            throw new EntityNotFoundException("No entity found with ID " + tripId + ".");
        boolean stopRemoved = tripService.removeStop(trip, locationId);
        if(!stopRemoved) throw new InvalidTripException("The stop can't be removed from the trip. (The reason may be that the stop was not present in the route).");
        return Response.status(Response.Status.OK).entity(trip.getFare()).build();
    }

    @Override
    public Response match(Long tripId, MatchDTO matchDTO) throws EntityNotFoundException, DriverNotAvailableException {
        tripService.match(tripId, matchDTO);
        return Response.status(Response.Status.OK).build();
    }

    @Override
    public Response complete(Long tripId, TripInfoDTO tripInfoDTO) throws EntityNotFoundException {
        tripService.complete(tripId, tripInfoDTO);
        return Response.status(Response.Status.OK).build();
    }

    @Override
    public Response cancel(Long tripId) throws EntityNotFoundException {
        tripService.cancel(tripId);
        return Response.status(Response.Status.OK).build();
    }
}
