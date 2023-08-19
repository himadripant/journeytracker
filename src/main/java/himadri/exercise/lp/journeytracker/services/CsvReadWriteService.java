package himadri.exercise.lp.journeytracker.services;

import himadri.exercise.lp.journeytracker.records.FullJourneyCost;
import himadri.exercise.lp.journeytracker.records.Journey;
import io.vavr.collection.Queue;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface CsvReadWriteService {

    Queue<Journey> readCsv() throws IOException;

    void printOutput(Queue<FullJourneyCost> fullJourneyCosts) throws FileNotFoundException, IOException;
}
