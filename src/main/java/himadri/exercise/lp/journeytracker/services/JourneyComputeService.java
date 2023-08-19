package himadri.exercise.lp.journeytracker.services;

import himadri.exercise.lp.journeytracker.records.FullJourneyCost;
import himadri.exercise.lp.journeytracker.records.Journey;
import io.vavr.Tuple2;
import io.vavr.collection.Queue;
import io.vavr.control.Option;

public interface JourneyComputeService {

    /**
     * Generates a {@link Queue} of journey start and end events as a {@link Tuple2} tuple
     * @param journeyLegs journey events which could be a journey start or end event in a {@link Queue}
     * @return {@link Queue} of start and end journey events packed in a tuple.
     */
    Queue<Tuple2<Option<Journey>, Option<Journey>>> findFullJourney(Queue<Journey> journeyLegs);

    /**
     * Calculates the journey cost with all required parameters of a journey from a {@link Queue} of start and end journey events packed in a tuple
     * @param fullJourneys a {@link Queue} of start and end journey events packed in a tuple
     * @return journey costs in a {@link Queue}
     */
    Queue<FullJourneyCost> calculateFullJourneyCost(Queue<Tuple2<Option<Journey>, Option<Journey>>> fullJourneys);

}
