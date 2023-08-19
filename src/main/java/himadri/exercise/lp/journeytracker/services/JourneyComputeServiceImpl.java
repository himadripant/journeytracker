package himadri.exercise.lp.journeytracker.services;

import himadri.exercise.lp.journeytracker.commons.JourneyStatus;
import himadri.exercise.lp.journeytracker.records.FullJourneyCost;
import himadri.exercise.lp.journeytracker.records.Journey;
import io.vavr.Tuple2;
import io.vavr.collection.Queue;
import io.vavr.control.Option;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;

@Service
public class JourneyComputeServiceImpl implements JourneyComputeService {

    private final FareComputeService fareComputeService;

    public JourneyComputeServiceImpl(FareComputeService fareComputeService) {
        this.fareComputeService = fareComputeService;
    }

    @Override
    public Queue<Tuple2<Option<Journey>, Option<Journey>>> findFullJourney(Queue<Journey> journeyLegs) {
        return createCompleteJourneyTuples(journeyLegs, Queue.empty());
    }

    @Override
    public Queue<FullJourneyCost> calculateFullJourneyCost(Queue<Tuple2<Option<Journey>, Option<Journey>>> fullJourneys) {
        return fullJourneys.map(fullJourney -> {
            final Journey journeyStart = fullJourney._1.get();
            final Journey journeyEnd = fullJourney._2.getOrElse(() -> null);
            final Float fare = fareComputeService.calculateFare(journeyStart, journeyEnd);
            final JourneyStatus journeyStatus = findJourneyStatus(journeyStart, journeyEnd);
            return new FullJourneyCost(journeyStart.journeyTime(),
                    (journeyEnd != null) ? journeyEnd.journeyTime() : null,
                    (journeyEnd != null) ? journeyStart.journeyTime().until(journeyEnd.journeyTime(), ChronoUnit.SECONDS) : 0,
                    journeyStart.stopId(),
                    (journeyEnd != null) ? journeyEnd.stopId() : null,
                    String.format("$%.2f", fare),
                    journeyStart.companyId(),
                    journeyStart.busId(),
                    journeyStart.pan(),
                    journeyStatus);
        });
    }

    private Queue<Tuple2<Option<Journey>, Option<Journey>>> createCompleteJourneyTuples(
            final Queue<Journey> journeys, final Queue<Tuple2<Option<Journey>, Option<Journey>>> fullJourneyTuples) {
        final Tuple2<Journey, Queue<Journey>> dequeuedJourneyQueueTuple = journeys.dequeue();
        final Journey journeyStart = dequeuedJourneyQueueTuple._1;
        final Option<Journey> journeyEndOption = dequeuedJourneyQueueTuple._2
                .find(journey -> isJourneyEnded(journeyStart, journey));
        final Queue<Tuple2<Option<Journey>, Option<Journey>>> updatedFullJourneyTuples = fullJourneyTuples.enqueue(new Tuple2<>(Option.of(journeyStart), journeyEndOption));
        final Queue<Journey> updatedJourneys = (journeyEndOption.isEmpty())
                ? dequeuedJourneyQueueTuple._2 : dequeuedJourneyQueueTuple._2.remove(journeyEndOption.get());
        return updatedJourneys.isEmpty() ? updatedFullJourneyTuples : createCompleteJourneyTuples(updatedJourneys, updatedFullJourneyTuples);
    }

    private boolean isJourneyEnded(final Journey journeyStart, final Journey expectedJourneyEnd) {
        return StringUtils.equals(journeyStart.busId(), expectedJourneyEnd.busId())
                && StringUtils.equals(journeyStart.companyId(), expectedJourneyEnd.companyId())
                && StringUtils.equals(journeyStart.pan(), expectedJourneyEnd.pan())
                && expectedJourneyEnd.journeyTime().isAfter(journeyStart.journeyTime())
                && journeyStart.tapType() != expectedJourneyEnd.tapType();
    }

    private JourneyStatus findJourneyStatus(final Journey journeyStart, final Journey journeyEnd) {
        if (journeyEnd == null) {
            return JourneyStatus.INCOMPLETE;
        } else if (StringUtils.equals(journeyStart.stopId(), journeyEnd.stopId())) {
            return JourneyStatus.CANCELLED;
        } else {
            return JourneyStatus.COMPLETED;
        }
    }

}
