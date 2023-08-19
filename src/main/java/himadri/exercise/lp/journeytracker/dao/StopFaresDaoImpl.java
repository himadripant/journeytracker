package himadri.exercise.lp.journeytracker.dao;

import himadri.exercise.lp.journeytracker.records.JourneyBusStations;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.collection.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

@Repository
public class StopFaresDaoImpl implements StopFaresDao {

    final Map<JourneyBusStations, Float> fareTable = HashMap.of(
            new JourneyBusStations("company1", "bus1", HashSet.of(
                "stop1", "stop3")), 1F,
            new JourneyBusStations("company1", "bus1", HashSet.of(
                    "stop1", "stop5")), 2F,
            new JourneyBusStations("company1", "bus1", HashSet.of(
                    "stop3", "stop5")), 1F,
            new JourneyBusStations("company1", "bus1", HashSet.of(
                    "stop5", "stop7")), 1F,
            new JourneyBusStations("company1", "bus1", HashSet.of(
                    "stop3", "stop7")), 2F,
            new JourneyBusStations("company1", "bus1", HashSet.of(
                    "stop1", "stop7")), 3F,
            new JourneyBusStations("company2", "bus2", HashSet.of("stop2", "stop4")), 1.5F
    );

    Map<Tuple3<String, String, String>, Float> maxFareTable = HashMap.empty();

    @Override
    public float findFareBetweenStopsByCompanyIdAndBusId(final String companyId, final String busId, final String startStop, final String endStop) {
        if (StringUtils.isNotBlank(endStop)) {
            final JourneyBusStations busStation = new JourneyBusStations(companyId, busId, HashSet.of(startStop, endStop));
            return fareTable.getOrElse(busStation, 0F);
        }
        return findMaxFareFromStopByCompanyIdAndBusId(companyId, busId, startStop);
    }

    private float findMaxFareFromStopByCompanyIdAndBusId(final String companyId, final String busId, final String startStop) {
        final Tuple3<String, String, String> maxFareCompanyBusStopToSearch = new Tuple3<>(companyId, busId, startStop);
        if (!maxFareTable.containsKey(maxFareCompanyBusStopToSearch)) {
            for (Tuple2<JourneyBusStations, Float> journeyBusStationsFloatTuple2 : fareTable) {
                final String company = journeyBusStationsFloatTuple2._1.companyId();
                final String bus = journeyBusStationsFloatTuple2._1.busId();
                final Set<String> stops = journeyBusStationsFloatTuple2._1.startAndEndStops();
                final String stop1 = stops.head();
                final String stop2 = stops.last();
                final Float journeyFare = journeyBusStationsFloatTuple2._2;
                final Tuple3<String, String, String> companyBusStop1 = new Tuple3<>(company, bus, stop1);
                final Tuple3<String, String, String> companyBusStop2 = new Tuple3<>(company, bus, stop2);
                final Float maxFareCompanyBusStop1 = maxFareTable.getOrElse(companyBusStop1, 0F);
                if (maxFareCompanyBusStop1.compareTo(journeyFare) < 0) {
                    maxFareTable = maxFareTable.put(companyBusStop1, journeyFare);
                }
                final Float maxFareCompanyBusStop2 = maxFareTable.getOrElse(companyBusStop2, 0F);
                if (maxFareCompanyBusStop2.compareTo(journeyFare) < 0) {
                    maxFareTable = maxFareTable.put(companyBusStop2, journeyFare);
                }
            }
        }
        return maxFareTable.getOrElse(maxFareCompanyBusStopToSearch, 0F);
    }
}
