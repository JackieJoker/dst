package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IMatchDAO;
import dst.ass1.jpa.model.IMatch;

public class MatchDAO extends AbstractDAO<IMatch> implements IMatchDAO {
    public MatchDAO() {
        setClassT(IMatch.class);
    }
}
