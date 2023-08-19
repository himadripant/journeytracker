package himadri.exercise.lp.journeytracker.records;

import io.vavr.collection.Set;

public record JourneyBusStations(
        String companyId,
        String busId,
        Set<String> startAndEndStops
) {
}
