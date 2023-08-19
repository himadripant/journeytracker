package himadri.exercise.lp.journeytracker.records;

import himadri.exercise.lp.journeytracker.commons.JourneyStatus;

import java.time.ZonedDateTime;

public record FullJourneyCost(ZonedDateTime journeyStartTime,
                              ZonedDateTime journeyFinishTime,
                              long journeyDurationInSecs,
                              String fromStopId,
                              String toStopId,
                              String chargeAmount,
                              String companyId,
                              String busId,
                              String pan,
                              JourneyStatus journeyStatus) {
}
