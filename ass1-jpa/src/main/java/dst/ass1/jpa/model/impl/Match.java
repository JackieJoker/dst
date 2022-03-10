package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.*;
import org.hibernate.annotations.Target;

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
    @Target(Money.class)
    private IMoney fare;

    @OneToOne(optional = false, targetEntity = Trip.class)
    private ITrip trip;

    @ManyToOne(optional = false, targetEntity = Vehicle.class)
    private IVehicle vehicle;

    @ManyToOne(optional = false, targetEntity = Driver.class)
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
