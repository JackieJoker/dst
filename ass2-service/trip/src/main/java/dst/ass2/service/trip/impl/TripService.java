package dst.ass2.service.trip.impl;

import dst.ass1.jpa.dao.*;
import dst.ass1.jpa.model.*;
import dst.ass1.jpa.model.impl.Trip;
import dst.ass2.service.api.match.IMatchingService;
import dst.ass2.service.api.trip.*;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.sql.Date;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
@ManagedBean
public class TripService implements ITripService {
    @PersistenceContext
    private EntityManager em;
    @Inject
    private IModelFactory modelFactory;
    @Inject
    private IDAOFactory daoFactory;
    @Inject
    private IMatchingService matchingService;

    private TripDTO newTripDTO(Long tripId, Long riderId, Long pickupId, Long destinationId) {
        TripDTO tripDTO = new TripDTO();
        tripDTO.setId(tripId);
        tripDTO.setRiderId(riderId);
        tripDTO.setPickupId(pickupId);
        tripDTO.setDestinationId(destinationId);
        return tripDTO;
    }

    @Override
    @Transactional
    public TripDTO create(Long riderId, Long pickupId, Long destinationId) throws EntityNotFoundException {
        // Use factory methods to create Trip entity, Rider and location DAOs
        ITrip trip = modelFactory.createTrip();
        IRiderDAO riderDAO = daoFactory.createRiderDAO();
        ILocationDAO locationDAO = daoFactory.createLocationDAO();
        IRider rider;
        ILocation pickup;
        ILocation destination;
        // Get entities from ids

        rider = riderDAO.findById(riderId);
        if (rider == null) throw new EntityNotFoundException("Rider entity not found. Check the riderId.");
        pickup = locationDAO.findById(pickupId);
        if (pickup == null) throw new EntityNotFoundException("Pickup Location entity not found. Check the pickupId.");
        destination = locationDAO.findById(destinationId);
        if (destination == null)
            throw new EntityNotFoundException("Destination Location entity not found. Check the destinationId.");
        // execute only if no exception is thrown
        trip.setRider(rider);
        trip.setPickup(pickup);
        trip.setDestination(destination);
        trip.setState(TripState.CREATED);
//        trip.setCreated(Date.from(Instant.now()));     It is alred done by our listener (Assignment 1)

        em.persist(trip);

        TripDTO tripDTO = newTripDTO(trip.getId(), riderId, pickupId, destinationId);
        // Set tripDTO fare using matchingService to calculate the money amount
        try {
            MoneyDTO moneyDTO = matchingService.calculateFare(tripDTO);
            tripDTO.setFare(moneyDTO);
        } catch (InvalidTripException e) {
            tripDTO.setFare(null);
        }
        return tripDTO;
    }

    @Override
    @Transactional
    public void confirm(Long tripId) throws EntityNotFoundException, IllegalStateException, InvalidTripException {
        ITripDAO tripDAO = daoFactory.createTripDAO();

        ITrip trip = tripDAO.findById(tripId);
        if (trip == null) throw new EntityNotFoundException("Trip entity not found. Check the tripId.");

        if (trip.getState() != TripState.CREATED || trip.getRider() == null) {
            throw new IllegalStateException();
        }
        TripDTO tripDTO = newTripDTO(tripId, trip.getRider().getId(), trip.getPickup().getId(), trip.getDestination().getId());
        matchingService.calculateFare(tripDTO);
        trip = em.find(Trip.class, tripId);
        trip.setState(TripState.QUEUED);
        matchingService.queueTripForMatching(tripId);
    }

    @Override
    @Transactional(rollbackOn = {EntityNotFoundException.class, DriverNotAvailableException.class, IllegalStateException.class})
    public void match(Long tripId, MatchDTO match) throws EntityNotFoundException, DriverNotAvailableException, IllegalStateException {
        // Create the match
        ITrip trip = daoFactory.createTripDAO().findById(tripId);
        if (trip == null) {
            matchingService.queueTripForMatching(tripId);
            throw new EntityNotFoundException("Trip entity not found. Check the tripId.");
        }
        IDriver driver = daoFactory.createDriverDAO().findById(match.getDriverId());
        if (driver == null) {
            matchingService.queueTripForMatching(tripId);
            throw new EntityNotFoundException("Driver entity not found. Check the match.driverId.");
        }
        IVehicle vehicle = daoFactory.createVehicleDAO().findById(match.getVehicleId());
        if (vehicle == null) {
            matchingService.queueTripForMatching(tripId);
            throw new EntityNotFoundException("Vehicle entity not found. Check the match.vehicleId.");
        }

        IMatch matching = modelFactory.createMatch();
        matching.setTrip(trip);
        matching.setDriver(driver);
        matching.setVehicle(vehicle);
        MoneyDTO moneyDTO = match.getFare();
        IMoney money = modelFactory.createMoney();
        money.setCurrency(moneyDTO.getCurrency());
        money.setCurrencyValue(moneyDTO.getValue());
        matching.setFare(money);
        matching.setDate(Date.from(Instant.now()));

        em.persist(matching);

        IMatchDAO matchDAO = daoFactory.createMatchDAO();
        // Extended DAO to obtain the number of matches currently assigned to a driver
        Long driverMatches = matchDAO.countMatchesOfDriver(driver);
        if (driverMatches > 1) {
            matchingService.queueTripForMatching(tripId);
            throw new DriverNotAvailableException("The driver was assigned to another customer.");
        }
        if (trip.getRider() == null || trip.getState() != TripState.QUEUED) {
            matchingService.queueTripForMatching(tripId);
            throw new IllegalStateException();
        }

        // Set the trip state to MATCHED
        trip = em.find(Trip.class, tripId, LockModeType.PESSIMISTIC_WRITE);
        trip.setState(TripState.MATCHED);
    }

    @Override
    @Transactional
    public void complete(Long tripId, TripInfoDTO tripInfoDTO) throws EntityNotFoundException {
        Trip trip = em.find(Trip.class, tripId);
        if (trip == null) throw new EntityNotFoundException("Trip entity not found. Check the tripId.");
        trip.setState(TripState.COMPLETED);
        ITripInfo tripInfo = modelFactory.createTripInfo();
        tripInfo.setCompleted(tripInfoDTO.getCompleted());
        tripInfo.setDistance(tripInfoDTO.getDistance());
        // Set total money
        MoneyDTO moneyDTO = tripInfoDTO.getFare();
        IMoney money = modelFactory.createMoney();
        money.setCurrency(moneyDTO.getCurrency());
        money.setCurrencyValue(moneyDTO.getValue());
        tripInfo.setTotal(money);
        tripInfo.setTrip(trip);
        em.persist(tripInfo);
    }

    @Override
    @Transactional
    public void cancel(Long tripId) throws EntityNotFoundException {
        ITrip trip = em.find(Trip.class, tripId);
        if (trip == null) throw new EntityNotFoundException("Trip entity not found. Check the tripId.");
        trip.setState(TripState.CANCELLED);
    }

    @Override
    @Transactional
    public boolean addStop(TripDTO trip, Long locationId) throws EntityNotFoundException, IllegalStateException {
        ITrip tripEntity = em.find(Trip.class, trip.getId());
        if (tripEntity == null) throw new EntityNotFoundException("Trip entity not found. Check the trip.id.");
        if (tripEntity.getState() != TripState.CREATED) {
            throw new IllegalStateException();
        }
        ILocation location;

        location = daoFactory.createLocationDAO().findById(locationId);
        if (location == null) throw new EntityNotFoundException("Location entity not found. Check the locationId.");

        if (trip.getStops().contains(locationId)) {
            return false;
        } else {
            tripEntity.addStop(location);
            List<Long> stopsId = trip.getStops();
            stopsId.add(locationId);
            trip.setStops(stopsId);
            try {
                trip.setFare(matchingService.calculateFare(trip));
            } catch (InvalidTripException e) {
                trip.setFare(null);
            }
            return true;
        }
    }

    @Override
    @Transactional
    public boolean removeStop(TripDTO trip, Long locationId) throws EntityNotFoundException, IllegalStateException {
        ITrip tripEntity = em.find(Trip.class, trip.getId());
        if (tripEntity == null) throw new EntityNotFoundException("Trip entity not found. Check the trip.id.");
        if (tripEntity.getState() != TripState.CREATED) {
            throw new IllegalStateException();
        }
        ILocation location = daoFactory.createLocationDAO().findById(locationId);
        if (location == null) throw new EntityNotFoundException("Location entity not found. Check the locationId.");

        if (!trip.getStops().contains(locationId)) {
            return false;
        } else {
            Collection<ILocation> stops = tripEntity.getStops();
            stops = stops
                    .stream()
                    .filter(x -> !x.getId().equals(locationId))
                    .collect(Collectors.toList());
            tripEntity.setStops(stops);

            List<Long> stopsId = trip.getStops();
            stopsId.remove(locationId);
            trip.setStops(stopsId);
            try {
                trip.setFare(matchingService.calculateFare(trip));
            } catch (InvalidTripException e) {
                trip.setFare(null);
            }
            return true;
        }
    }

    @Override
    @Transactional
    public void delete(Long tripId) throws EntityNotFoundException {
        ITrip trip = em.find(Trip.class, tripId);
        if (trip == null) throw new EntityNotFoundException("Trip entity not found. Check the tripId.");
        em.remove(trip);
    }

    @Override
    public TripDTO find(Long tripId) {
        ITrip trip = daoFactory.createTripDAO().findById(tripId);
        if (trip == null) return null;

        TripDTO tripDTO = newTripDTO(tripId, trip.getRider().getId(), trip.getPickup().getId(), trip.getDestination().getId());
        if (trip.getStops() != null) {
            tripDTO.setStops(trip.getStops().stream().map(ILocation::getId).collect(Collectors.toList()));
        }
        // Set tripDTO fare using matchingService to calculate the money amount
        try {
            MoneyDTO moneyDTO = matchingService.calculateFare(tripDTO);
            tripDTO.setFare(moneyDTO);
        } catch (InvalidTripException e) {
            tripDTO.setFare(null);
        }
        return tripDTO;
    }
}
