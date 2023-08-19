package himadri.exercise.lp.journeytracker.services;

import himadri.exercise.lp.journeytracker.commons.TapType;
import himadri.exercise.lp.journeytracker.records.FullJourneyCost;
import himadri.exercise.lp.journeytracker.records.Journey;
import io.vavr.collection.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

@Service
public class CsvReadWriteServiceImpl implements CsvReadWriteService {
    final private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Queue<Journey> readCsv() throws IOException {
        final File inputFile = ResourceUtils.getFile("classpath:taps.csv");
        try (Stream<String> stream = Files.lines(inputFile.toPath())) {
            return stream.skip(1)
                    .map(str -> str.split(","))
                    .map(strings -> new Journey(Integer.parseInt(strings[0]), LocalDateTime.parse((strings[1]).trim(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")).atOffset(ZoneOffset.UTC).toZonedDateTime(), TapType.valueOf((strings[2]).trim()), (strings[3]).trim(), (strings[4]).trim(), (strings[5]).trim(), (strings[6]).trim()))
                    .collect(Queue.collector());
        }

    }

    @Override
    public void printOutput(Queue<FullJourneyCost> fullJourneyCosts) throws IOException {
        fullJourneyCosts.forEach(fullJourneyCost -> logger.info(fullJourneyCost.toString()));
    }
}
