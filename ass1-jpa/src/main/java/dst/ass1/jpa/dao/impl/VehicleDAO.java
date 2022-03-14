package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IVehicleDAO;
import dst.ass1.jpa.model.IVehicle;

public class VehicleDAO extends AbstractDAO<IVehicle> implements IVehicleDAO {
    public VehicleDAO() {
        setClassT(IVehicle.class);
    }
}
