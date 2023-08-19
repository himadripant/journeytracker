package himadri.exercise.lp.journeytracker.services;

import himadri.exercise.lp.journeytracker.records.Journey;

public interface FareComputeService {
    float calculateFare(Journey startJourney, Journey returnJourney);
}
