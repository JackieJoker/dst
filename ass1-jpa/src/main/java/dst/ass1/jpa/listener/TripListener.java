package dst.ass1.jpa.listener;

import dst.ass1.jpa.model.impl.Trip;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.Instant;
import java.util.Date;

public class TripListener {

    @PrePersist
    private void persisted(Trip trip){
        Date now = Date.from(Instant.now());
        trip.setCreated(now);
        trip.setUpdated(now);
    }

    @PreUpdate
    private void updated(Trip trip){
        Date now = Date.from(Instant.now());
        trip.setUpdated(now);
    }

}
