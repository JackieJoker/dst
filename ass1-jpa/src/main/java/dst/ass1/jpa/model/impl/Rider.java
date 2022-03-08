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
    private List<Trip> trips;

    @NotNull
    @OneToOne
    private Preferences preferences;

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
        return null;
    }

    @Override
    public void setPreferences(IPreferences preferences) {

    }

    @Override
    public Collection<ITrip> getTrips() {
        return null;
    }

    @Override
    public void setTrips(Collection<ITrip> trips) {

    }

    @Override
    public void addTrip(ITrip trip) {

    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public void setId(Long id) {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public String getTel() {
        return null;
    }

    @Override
    public void setTel(String tel) {

    }

    @Override
    public Double getAvgRating() {
        return null;
    }

    @Override
    public void setAvgRating(Double avgRating) {

    }
}
