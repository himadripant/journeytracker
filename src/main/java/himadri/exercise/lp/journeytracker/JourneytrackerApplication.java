package himadri.exercise.lp.journeytracker;

import himadri.exercise.lp.journeytracker.records.FullJourneyCost;
import himadri.exercise.lp.journeytracker.records.Journey;
import himadri.exercise.lp.journeytracker.services.CsvReadWriteService;
import himadri.exercise.lp.journeytracker.services.JourneyComputeService;
import io.vavr.collection.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static java.lang.System.exit;

@SpringBootApplication
public class JourneytrackerApplication implements CommandLineRunner {

	@Autowired
	private JourneyComputeService journeyComputeService;

	@Autowired
	private CsvReadWriteService csvReadWriteService;

	public static void main(String[] args) {
		SpringApplication.run(JourneytrackerApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		final Queue<Journey> journeys = csvReadWriteService.readCsv();
		final Queue<FullJourneyCost> fullJourneyCosts = journeyComputeService.calculateFullJourneyCost(journeyComputeService.findFullJourney(journeys));
		csvReadWriteService.printOutput(fullJourneyCosts);
	}
}
