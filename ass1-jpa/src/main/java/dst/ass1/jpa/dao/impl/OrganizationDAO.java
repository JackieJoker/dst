package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IOrganizationDAO;
import dst.ass1.jpa.model.IOrganization;

public class OrganizationDAO extends AbstractDAO<IOrganization> implements IOrganizationDAO {
    public OrganizationDAO() {
        setClassT(IOrganization.class);
    }
}
