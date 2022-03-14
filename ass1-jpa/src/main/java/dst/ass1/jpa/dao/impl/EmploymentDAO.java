package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IEmploymentDAO;
import dst.ass1.jpa.model.IEmployment;

public class EmploymentDAO extends AbstractDAO<IEmployment> implements IEmploymentDAO {
    public EmploymentDAO() {
        setClassT(IEmployment.class);
    }
}
