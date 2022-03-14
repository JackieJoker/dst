package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.ITripInfoDAO;
import dst.ass1.jpa.model.ITripInfo;

public class TripInfoDAO extends AbstractDAO<ITripInfo> implements ITripInfoDAO {

    public TripInfoDAO() {
        setClassT(ITripInfo.class);
    }

}
