package himadri.exercise.lp.journeytracker.services;

import himadri.exercise.lp.journeytracker.dao.StopFaresDao;
import himadri.exercise.lp.journeytracker.records.Journey;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class FareComputeServiceImpl implements FareComputeService {

    private final StopFaresDao stopFaresDao;

    public FareComputeServiceImpl(StopFaresDao stopFaresDao) {
        this.stopFaresDao = stopFaresDao;
    }

    @Override
    public float calculateFare(final Journey startJourney, final Journey endJourney) {
        final String companyId = startJourney.companyId();
        final String busId = startJourney.busId();
        final String startStop = startJourney.stopId();
        final String endStop = (endJourney != null) ? endJourney.stopId() : null;
        if (StringUtils.equals(startStop, endStop) && (startJourney.tapType() != endJourney.tapType())) {
            return 0F;
        }
        return stopFaresDao.findFareBetweenStopsByCompanyIdAndBusId(companyId, busId, startStop, endStop);
    }
}
