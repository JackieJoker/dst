package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
public class Trip implements ITrip {

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(value = TemporalType.DATE)
    private Date created;

    @Temporal(value = TemporalType.DATE)
    private Date updated;

    @Enumerated(EnumType.STRING)
    private TripState state;

    @NotNull
    @ManyToOne
    private Location pickup;

    @NotNull
    @ManyToOne
    private Location destination;

    @ManyToMany
    private List<Location> stops;

    @ManyToOne
    private Rider rider;

    public Trip() {}

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public void setId(Long id) {

    }

    @Override
    public Date getCreated() {
        return null;
    }

    @Override
    public void setCreated(Date created) {

    }

    @Override
    public Date getUpdated() {
        return null;
    }

    @Override
    public void setUpdated(Date updated) {

    }

    @Override
    public TripState getState() {
        return null;
    }

    @Override
    public void setState(TripState state) {

    }

    @Override
    public ILocation getPickup() {
        return null;
    }

    @Override
    public void setPickup(ILocation pickup) {

    }

    @Override
    public ILocation getDestination() {
        return null;
    }

    @Override
    public void setDestination(ILocation destination) {

    }

    @Override
    public Collection<ILocation> getStops() {
        return null;
    }

    @Override
    public void setStops(Collection<ILocation> stops) {

    }

    @Override
    public void addStop(ILocation stop) {

    }

    @Override
    public ITripInfo getTripInfo() {
        return null;
    }

    @Override
    public void setTripInfo(ITripInfo tripInfo) {

    }

    @Override
    public IMatch getMatch() {
        return null;
    }

    @Override
    public void setMatch(IMatch match) {

    }

    @Override
    public IRider getRider() {
        return null;
    }

    @Override
    public void setRider(IRider rider) {

    }
}
