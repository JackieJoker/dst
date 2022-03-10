package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IDriver;
import dst.ass1.jpa.model.IEmploymentKey;
import dst.ass1.jpa.model.IOrganization;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
public class EmploymentKey implements IEmploymentKey, Serializable {

    @ManyToOne(optional = false)
    IDriver driver;

    @ManyToOne(optional = false)
    IOrganization organization;

    @Override
    public IDriver getDriver() {
        return driver;
    }

    @Override
    public void setDriver(IDriver driver) {
        this.driver = driver;
    }

    @Override
    public IOrganization getOrganization() {
        return organization;
    }

    @Override
    public void setOrganization(IOrganization organization) {
        this.organization = organization;
    }
}
