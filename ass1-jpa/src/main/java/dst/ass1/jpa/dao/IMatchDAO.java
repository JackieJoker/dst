package dst.ass1.jpa.dao;

import dst.ass1.jpa.model.IDriver;
import dst.ass1.jpa.model.IMatch;

public interface IMatchDAO extends GenericDAO<IMatch> {
    Long countMatchesOfDriver(IDriver driver);
}
