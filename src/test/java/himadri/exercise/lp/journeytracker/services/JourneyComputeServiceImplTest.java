package himadri.exercise.lp.journeytracker.services;

import himadri.exercise.lp.journeytracker.commons.JourneyStatus;
import himadri.exercise.lp.journeytracker.commons.TapType;
import himadri.exercise.lp.journeytracker.dao.StopFaresDaoImpl;
import himadri.exercise.lp.journeytracker.records.FullJourneyCost;
import himadri.exercise.lp.journeytracker.records.Journey;
import io.vavr.Tuple2;
import io.vavr.collection.Queue;
import io.vavr.control.Option;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class JourneyComputeServiceImplTest {

    final JourneyComputeService journeyComputeService = new JourneyComputeServiceImpl(new FareComputeServiceImpl(new StopFaresDaoImpl()));

    private static Queue<Journey> JOURNEYS;
    private static Queue<Tuple2<Option<Journey>, Option<Journey>>> EXPECTED_TUPLE_OF_COMPLETED_JOURNEYS;

    private static Queue<FullJourneyCost> EXPECTED_FULL_JOURNEY_COSTS;

    @BeforeAll
    public static void setupJourneys() {
        // Given:
        Journey journey1 = new Journey(1, ZonedDateTime.now(), TapType.ON, "stop1", "company1", "bus1", "5500005555555559");
        Journey journey2 = new Journey(2, ZonedDateTime.now().plusMinutes(7), TapType.OFF, "stop3", "company1", "bus1", "5500005555555559");
        Journey journey3 = new Journey(3, ZonedDateTime.now().plusSeconds(5), TapType.ON, "stop1", "company1", "bus1", "4111111111111111");
        Journey journey4 = new Journey(4, ZonedDateTime.now().plusSeconds(10), TapType.ON, "stop2", "company2", "bus2", "4444333322221111");
        Journey journey5 = new Journey(5, ZonedDateTime.now().plusSeconds(11), TapType.ON, "stop2", "company2", "bus2", "5555555555554444");
        Journey journey6 = new Journey(6, ZonedDateTime.now().plusMinutes(8), TapType.OFF, "stop4", "company2", "bus2", "5555555555554444");
        Journey journey7 = new Journey(7, ZonedDateTime.now().plusSeconds(5), TapType.ON, "stop2", "company2", "bus2", "5105105105105100");
        Journey journey8 = new Journey(8, ZonedDateTime.now().plusMinutes(3), TapType.OFF, "stop4", "company2", "bus2", "5105105105105100");
        Journey journey9 = new Journey(9, ZonedDateTime.now().plusMinutes(7).plusSeconds(6), TapType.OFF, "stop3", "company1", "bus1", "4111111111111111");
        Journey journey10 = new Journey(10, ZonedDateTime.now().plusSeconds(12), TapType.OFF, "stop2", "company2", "bus2", "4444333322221111");
        Journey journey11 = new Journey(11, ZonedDateTime.now().plusSeconds(14), TapType.ON, "stop2", "company2", "bus2", "4001919257537193");
        JOURNEYS = Queue.of(journey1,
                journey2,
                journey3,
                journey4,
                journey5,
                journey6,
                journey7,
                journey8,
                journey9,
                journey10,
                journey11
        );

        /* Expected completed journey pairs Queue */
        EXPECTED_TUPLE_OF_COMPLETED_JOURNEYS = Queue.of(
                new Tuple2<>(Option.of(journey1), Option.of(journey2)),
                new Tuple2<>(Option.of(journey3), Option.of(journey9)),
                new Tuple2<>(Option.of(journey4), Option.of(journey10)),
                new Tuple2<>(Option.of(journey5), Option.of(journey6)),
                new Tuple2<>(Option.of(journey7), Option.of(journey8)),
                new Tuple2<>(Option.of(journey11), Option.none())
        );

        /* Expected completed journey costs */
        EXPECTED_FULL_JOURNEY_COSTS = Queue.of(
                new FullJourneyCost(journey1.journeyTime(), journey2.journeyTime(), journey1.journeyTime().until(journey2.journeyTime(), ChronoUnit.SECONDS), journey1.stopId(), journey2.stopId(), "$1.00", journey1.companyId(), journey1.busId(), journey1.pan(), JourneyStatus.COMPLETED),
                new FullJourneyCost(journey3.journeyTime(), journey9.journeyTime(), journey3.journeyTime().until(journey9.journeyTime(), ChronoUnit.SECONDS), journey3.stopId(), journey9.stopId(), "$1.00", journey3.companyId(), journey3.busId(), journey3.pan(), JourneyStatus.COMPLETED),
                new FullJourneyCost(journey4.journeyTime(), journey10.journeyTime(), journey4.journeyTime().until(journey10.journeyTime(), ChronoUnit.SECONDS), journey4.stopId(), journey10.stopId(), "$0.00", journey4.companyId(), journey4.busId(), journey4.pan(), JourneyStatus.CANCELLED),
                new FullJourneyCost(journey5.journeyTime(), journey6.journeyTime(), journey5.journeyTime().until(journey6.journeyTime(), ChronoUnit.SECONDS), journey5.stopId(), journey6.stopId(), "$1.50", journey5.companyId(), journey5.busId(), journey5.pan(), JourneyStatus.COMPLETED),
                new FullJourneyCost(journey7.journeyTime(), journey8.journeyTime(), journey7.journeyTime().until(journey8.journeyTime(), ChronoUnit.SECONDS), journey7.stopId(), journey8.stopId(), "$1.50", journey7.companyId(), journey7.busId(), journey7.pan(), JourneyStatus.COMPLETED),
                new FullJourneyCost(journey11.journeyTime(), null, 0, journey11.stopId(), null, "$1.50", journey11.companyId(), journey11.busId(), journey11.pan(), JourneyStatus.INCOMPLETE)
        );
    }

    @Test
    void fullJourneyFinder_shouldFindReturnJourneyLeg() {
        // When:
        Queue<Tuple2<Option<Journey>, Option<Journey>>> actualTupleOfCompletedJourneyLegs =
                journeyComputeService.findFullJourney(JOURNEYS);

        // Then:
        assertJourneyLegs(EXPECTED_TUPLE_OF_COMPLETED_JOURNEYS, actualTupleOfCompletedJourneyLegs);
    }

    @Test
    void calculateFullJourneyCost_shouldCalculateFullJourneyCosts() {
        // When:
        Queue<FullJourneyCost> actualFullJourneyCosts = journeyComputeService.calculateFullJourneyCost(EXPECTED_TUPLE_OF_COMPLETED_JOURNEYS);

        // Then:
        assertEquals(EXPECTED_FULL_JOURNEY_COSTS, actualFullJourneyCosts);
    }

    void assertJourneyLegs(Queue<Tuple2<Option<Journey>, Option<Journey>>> expectedTupleOfCompletedJourneyLegs, Queue<Tuple2<Option<Journey>, Option<Journey>>> actualTupleOfCompletedJourneyLegs) {
        Tuple2<Tuple2<Option<Journey>, Option<Journey>>, Queue<Tuple2<Option<Journey>, Option<Journey>>>> dequeuedExpectedTuple = expectedTupleOfCompletedJourneyLegs.dequeue();
        Tuple2<Tuple2<Option<Journey>, Option<Journey>>, Queue<Tuple2<Option<Journey>, Option<Journey>>>> dequeuedActualTuple = actualTupleOfCompletedJourneyLegs.dequeue();
        assertEquals(dequeuedExpectedTuple._1, dequeuedActualTuple._1);
        assertEquals(dequeuedExpectedTuple._2.size(), dequeuedActualTuple._2.size());
        if (dequeuedExpectedTuple._2().isEmpty()) {
            return;
        }
        assertJourneyLegs(dequeuedExpectedTuple._2, dequeuedActualTuple._2);
    }
}