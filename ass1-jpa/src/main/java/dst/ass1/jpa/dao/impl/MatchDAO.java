package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IMatchDAO;
import dst.ass1.jpa.model.IDriver;
import dst.ass1.jpa.model.IMatch;

public class MatchDAO extends AbstractDAO<IMatch> implements IMatchDAO {
    public MatchDAO() {
        setClassT(IMatch.class);
    }

    public Long countMatchesOfDriver(IDriver driver) {
        return (Long) entityManager.createQuery("select count(t) from Match m join m.driver d join m.trip t where d.id = " + driver.getId() + " and t.state <> 'CANCELLED' and t.state <> 'COMPLETED'").getSingleResult();
    }
}
