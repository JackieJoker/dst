package dst.ass1.jpa.dao;

import dst.ass1.jpa.model.IMoney;
import dst.ass1.jpa.model.IRider;

import java.util.Map;

public interface IRiderDAO extends GenericDAO<IRider> {


    /**
     * Returns the rider associated with the given email. Returns null if the email does not exist.
     *
     * @param email the email address
     * @return the rider or null
     */
    IRider findByEmail(String email);

    /**
     * Gets the total travel distance of the rider with the most recently completed trip
     *
     * @return the total distance the rider has travelled
     */
    Double getTotalDistanceOfMostRecentRider();

    /**
     * Finds for each Rider, who has completed at least one trip in the past 30 days, the sum of their total
     * spendings of the past 30 days.
     *
     * @return a map containing each rider, and the money grouped by their currency.
     */
    Map<IRider, Map<String, IMoney>> getRecentSpending();
}
