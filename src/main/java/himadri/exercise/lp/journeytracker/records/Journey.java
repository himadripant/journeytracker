package himadri.exercise.lp.journeytracker.records;

import himadri.exercise.lp.journeytracker.commons.TapType;

import java.time.ZonedDateTime;

public record Journey(int id,
                      ZonedDateTime journeyTime,
                      TapType tapType,
                      String stopId,
                      String companyId,
                      String busId,
                      String pan) {
}
