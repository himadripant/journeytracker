package himadri.exercise.lp.journeytracker.dao;

public interface StopFaresDao {
    float findFareBetweenStopsByCompanyIdAndBusId(String companyId, String busId, String startStop, String endStop);

}
