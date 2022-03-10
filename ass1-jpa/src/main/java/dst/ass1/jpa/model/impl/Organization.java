package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IEmployment;
import dst.ass1.jpa.model.IOrganization;
import dst.ass1.jpa.model.IVehicle;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity
public class Organization implements IOrganization {

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToMany(targetEntity = Vehicle.class)
    private Collection<IVehicle> vehicles;

    @ManyToMany(targetEntity = Organization.class)
    @JoinTable(
            name = "organization_parts",
            joinColumns = @JoinColumn(name = "partsOrganization_id"),
            inverseJoinColumns = @JoinColumn(name = "partOfOrganization_id"))
    private Collection<IOrganization> parts;

    @ManyToMany(mappedBy = "parts", targetEntity = Organization.class)
    private Collection<IOrganization> partOf;

    @OneToMany(mappedBy = "id.organization", targetEntity = Employment.class)
    private Collection<IEmployment> employments;

    public Organization() {}

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
    public Collection<IVehicle> getVehicles() {
        return vehicles;
    }

    @Override
    public void setVehicles(Collection<IVehicle> vehicles) {
        this.vehicles = vehicles;
    }

    @Override
    public void addVehicle(IVehicle vehicle) {
        vehicles.add(vehicle);
    }

    @Override
    public Collection<IOrganization> getParts() {
        return parts;
    }

    @Override
    public void setParts(Collection<IOrganization> parts) {
        this.parts = parts;
    }

    @Override
    public void addPart(IOrganization part) {
        parts.add(part);
    }

    @Override
    public Collection<IOrganization> getPartOf() {
        return partOf;
    }

    @Override
    public void setPartOf(Collection<IOrganization> partOf) {
        this.partOf = partOf;
    }

    @Override
    public void addPartOf(IOrganization partOf) {
        this.partOf.add(partOf);
    }

    @Override
    public Collection<IEmployment> getEmployments() {
        return employments;
    }

    @Override
    public void setEmployments(Collection<IEmployment> employments) {
        this.employments = employments;
    }

    @Override
    public void addEmployment(IEmployment employment) {
        employments.add(employment);
    }
}
