package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.ILocation;

import javax.persistence.*;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "reachedLocations",
                query = "SELECT DISTINCT d.locationId FROM Trip t JOIN t.destination d WHERE t.state = 'COMPLETED'"
        ),
        @NamedQuery(
                name = "reachedLocations2",
                query = "SELECT DISTINCT s.locationId FROM Trip t JOIN t.stops s WHERE t.state = 'COMPLETED'"
        )
})
public class Location implements ILocation {

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private Long locationId;

    public Location() {}

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
    public Long getLocationId() {
        return locationId;
    }

    @Override
    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }
}
