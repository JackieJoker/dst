package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IPreferences;
import dst.ass1.jpa.model.IRider;
import dst.ass1.jpa.model.ITrip;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

@Entity
public class Rider implements IRider {

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @NotNull
    private String tel;

    private Double avgRating;

    @OneToMany(mappedBy = "rider")
    private Collection<ITrip> trips;

    @OneToOne(optional = false)
    private IPreferences preferences;

    public Rider() {}

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public void setEmail(String email) {

    }

    @Override
    public byte[] getPassword() {
        return new byte[0];
    }

    @Override
    public void setPassword(byte[] password) {

    }

    @Override
    public String getAccountNo() {
        return null;
    }

    @Override
    public void setAccountNo(String accountNo) {

    }

    @Override
    public String getBankCode() {
        return null;
    }

    @Override
    public void setBankCode(String bankCode) {

    }

    @Override
    public IPreferences getPreferences() {
        return preferences;
    }

    @Override
    public void setPreferences(IPreferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public Collection<ITrip> getTrips() {
        return trips;
    }

    @Override
    public void setTrips(Collection<ITrip> trips) {
        this.trips = trips;
    }

    @Override
    public void addTrip(ITrip trip) {
        trips.add(trip);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getTel() {
        return tel;
    }

    @Override
    public void setTel(String tel) {
        this.tel = tel;
    }

    @Override
    public Double getAvgRating() {
        return avgRating;
    }

    @Override
    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating;
    }
}
