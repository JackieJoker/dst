package dst.ass1.jpa.model;

public interface IModelFactory {

    IModelFactory createModelFactory();

    IDriver createDriver();

    IEmployment createEmployment();

    IEmploymentKey createEmploymentKey();

    ILocation createLocation();

    IMatch createMatch();

    IMoney createMoney();

    IOrganization createOrganization();

    IRider createRider();

    IPreferences createPreferences();

    ITrip createTrip();

    ITripInfo createTripInfo();

    IVehicle createVehicle();
}
