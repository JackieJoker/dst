package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IPreferences;
import dst.ass1.jpa.model.IRider;
import dst.ass1.jpa.model.ITrip;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"accountNo", "bankCode"})
)
@Entity
public class Rider implements IRider {

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @NotNull
    private String tel;

    private Double avgRating;

    @OneToMany(mappedBy = "rider", targetEntity = Trip.class)
    private Collection<ITrip> trips;

    @OneToOne(optional = false, targetEntity = Preferences.class)
    private IPreferences preferences;

    @NotNull
    @Column(unique = true)
    private String email;

    @Column(length = 20)
    private byte[] password;

    private String accountNo;

    private String bankCode;

    public Rider() {
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

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public byte[] getPassword() {
        return password;
    }

    @Override
    public void setPassword(byte[] password) {
        this.password = password;
    }

    @Override
    public String getAccountNo() {
        return accountNo;
    }

    @Override
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    @Override
    public String getBankCode() {
        return bankCode;
    }

    @Override
    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }
}
