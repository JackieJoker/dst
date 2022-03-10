package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.*;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Match implements IMatch {

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(value = TemporalType.DATE)
    private Date date;

    @Embedded
    private IMoney fare;

    @OneToOne(optional = false)
    private ITrip trip;

    @ManyToOne(optional = false)
    private IVehicle vehicle;

    @ManyToOne(optional = false)
    private IDriver driver;

    public Match() {}

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public IMoney getFare() {
        return fare;
    }

    @Override
    public void setFare(IMoney fare) {
        this.fare = fare;
    }

    @Override
    public ITrip getTrip() {
        return trip;
    }

    @Override
    public void setTrip(ITrip trip) {
        this.trip = trip;
    }

    @Override
    public IVehicle getVehicle() {
        return vehicle;
    }

    @Override
    public void setVehicle(IVehicle vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public IDriver getDriver() {
        return driver;
    }

    @Override
    public void setDriver(IDriver driver) {
        this.driver = driver;
    }
}
