package himadri.exercise.lp.journeytracker.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class StopFaresDaoImplTest {

    private final StopFaresDao stopFaresDao = new StopFaresDaoImpl();

    @Test
    @DisplayName("Finds the fare from one bus stop to another irrespective of order of ride.")
    void findFareBetweenStopsByCompanyIdAndBusId_shouldFindBetweenStartEndStops() {
        // Given:
        String companyId = "company1";
        String busId = "bus1";
        String startStop = "stop1";
        String endStop = "stop3";

        // When and Then:
        assertEquals(1F, stopFaresDao.findFareBetweenStopsByCompanyIdAndBusId(companyId, busId, startStop, endStop));

        // Given:
        startStop = "stop7";
        endStop = "stop3";

        // When and Then:
        assertEquals(2F, stopFaresDao.findFareBetweenStopsByCompanyIdAndBusId(companyId, busId, startStop, endStop));
    }

    @Test
    @DisplayName("Finds the maximum fare from a bus stop when the rider forgot to tap off while alighting.")
    void findFareBetweenStopsByCompanyIdAndBusId_shouldFindMaximumFareFromStartingStop() {
        // Given:
        String companyId = "company1";
        String busId = "bus1";
        String startStop = "stop1";
        String endStop = null;

        // When and Then:
        assertEquals(3F, stopFaresDao.findFareBetweenStopsByCompanyIdAndBusId(companyId, busId, startStop, endStop));
    }
}