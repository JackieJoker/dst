package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IDriver;
import dst.ass1.jpa.model.IEmployment;
import dst.ass1.jpa.model.IVehicle;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class Driver extends PlatformUser implements IDriver {

    @ManyToOne(targetEntity = Vehicle.class, optional = false)
    private IVehicle vehicle;

    @OneToMany(mappedBy = "id.driver", targetEntity = Employment.class)
    private Collection<IEmployment> employments;

    public Driver() {}

    @Override
    public IVehicle getVehicle() {
        return vehicle;
    }

    @Override
    public void setVehicle(IVehicle vehicle) {
        this.vehicle = vehicle;
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
