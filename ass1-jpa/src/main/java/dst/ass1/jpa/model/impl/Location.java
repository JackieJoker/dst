package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.ILocation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Location implements ILocation {

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private Long locationId;

    public Location() {}

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
    public Long getLocationId() {
        return null;
    }

    @Override
    public void setLocationId(Long locationId) {

    }
}
