package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Entity
public class Trip implements ITrip {

    private Long id;

    private Date created;

    private Date updated;

    private TripState state;

    private ILocation pickup;

    private ILocation destination;

    private Collection<ILocation> stops;

    private IRider rider;

    private ITripInfo tripInfo;

    private IMatch match;

    public Trip() {}

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public Date getUpdated() {
        return updated;
    }

    @Override
    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Override
    public TripState getState() {
        return state;
    }

    @Override
    public void setState(TripState state) {
        this.state = state;
    }

    @Override
    public ILocation getPickup() {
        return pickup;
    }

    @Override
    public void setPickup(ILocation pickup) {
        this.pickup = pickup;
    }

    @Override
    public ILocation getDestination() {
        return destination;
    }

    @Override
    public void setDestination(ILocation destination) {
        this.destination = destination;
    }

    @Override
    public Collection<ILocation> getStops() {
        return stops;
    }

    @Override
    public void setStops(Collection<ILocation> stops) {
        this.stops = stops;
    }

    @Override
    public void addStop(ILocation stop) {
        if(stops == null) {
            stops = new ArrayList<>();
        }
        stops.add(stop);
    }

    @Override
    public IRider getRider() {
        return rider;
    }

    @Override
    public void setRider(IRider rider) {
        this.rider = rider;
    }

    @Override
    public ITripInfo getTripInfo() {
        return tripInfo;
    }

    @Override
    public void setTripInfo(ITripInfo tripInfo) {
        this.tripInfo = tripInfo;
    }

    @Override
    public IMatch getMatch() {
        return match;
    }

    @Override
    public void setMatch(IMatch match) {
        this.match = match;
    }
}
