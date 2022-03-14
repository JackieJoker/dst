package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IPreferences;
import dst.ass1.jpa.model.IRider;
import dst.ass1.jpa.model.ITrip;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"accountNo", "bankCode"})
)
@Entity
@NamedQueries({
        @NamedQuery(
                name = "riderByEmail",
                query = "SELECT r FROM Rider r WHERE r.email = :email"
        ),
        @NamedQuery(
                name = "sumDistanceOfRiderWithMostRecentTrip",
                query = "SELECT SUM(i.distance) FROM Rider r JOIN r.trips t JOIN t.tripInfo i WHERE t.state = 'COMPLETED' and r.id = (SELECT MIN(r2.id) FROM Rider r2 JOIN r2.trips t2 JOIN t2.tripInfo i2 WHERE i2.completed = (SELECT MAX(i3.completed) FROM Rider r3 JOIN r3.trips t3 JOIN t3.tripInfo i3))"
        ),
})
public class Rider extends PlatformUser implements IRider {

    @OneToMany(mappedBy = "rider", targetEntity = Trip.class)
    private Collection<ITrip> trips;

    @OneToOne(optional = false, targetEntity = Preferences.class, cascade = CascadeType.REMOVE)
    private IPreferences preferences;

    @Column(unique = true, nullable = false)
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
        if (trips == null) {
            trips = new ArrayList<>();
        }
        trips.add(trip);
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
