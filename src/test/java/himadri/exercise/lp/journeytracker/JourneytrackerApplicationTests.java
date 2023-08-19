package himadri.exercise.lp.journeytracker;

import himadri.exercise.lp.journeytracker.commons.JourneyStatus;
import himadri.exercise.lp.journeytracker.records.FullJourneyCost;
import himadri.exercise.lp.journeytracker.records.Journey;
import himadri.exercise.lp.journeytracker.services.CsvReadWriteService;
import himadri.exercise.lp.journeytracker.services.JourneyComputeService;
import io.vavr.collection.Queue;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test is based on the input values in `resources/taps.csv`
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class JourneytrackerApplicationTests {

    @Autowired
    private CsvReadWriteService csvReadWriteService;

    @Autowired
    private JourneyComputeService journeyComputeService;

    @Test
    public void readCsvFileAndAssertJourneyFare_shouldVerifyJourney1() {
        try {
            final Queue<Journey> journeys = csvReadWriteService.readCsv();
            final Queue<FullJourneyCost> fullJourneyCosts = journeyComputeService.calculateFullJourneyCost(journeyComputeService.findFullJourney(journeys));

            /* assert jounrey of pan # 5500005555555559 from stop1 to stop3 by bus1 of company1 */
            // Given:
            String companyId = "company1";
            String busId = "bus1";
            String pan = "5500005555555559";
            String from = "stop1";
            String to = "stop3";
			// When:
            FullJourneyCost fullJourneyCost = fullJourneyCosts.find(journeyCost ->
                    StringUtils.equals(journeyCost.busId(), busId)
                            && StringUtils.equals(journeyCost.companyId(), companyId)
                            && StringUtils.equals(journeyCost.pan(), pan)
                            && StringUtils.equals(journeyCost.fromStopId(), from)
                            && StringUtils.equals(journeyCost.toStopId(), to)
            ).get();
			// Then:
			assertAll(() -> assertEquals("$1.00", fullJourneyCost.chargeAmount()),
					() -> assertEquals(300, fullJourneyCost.journeyDurationInSecs()),
					() -> assertEquals(JourneyStatus.COMPLETED, fullJourneyCost.journeyStatus()));
        } catch (IOException e) {
            fail("Exception encountered " + e.getMessage());
        }
    }

	@Test
	public void readCsvFileAndAssertJourneyFare_shouldVerifyJourney2() {
		try {
			final Queue<Journey> journeys = csvReadWriteService.readCsv();
			final Queue<FullJourneyCost> fullJourneyCosts = journeyComputeService.calculateFullJourneyCost(journeyComputeService.findFullJourney(journeys));

			/* assert journey of pan # 4111111111111111 from stop3 to stop7 by bus1 of company1 */
			// Given:
			String companyId = "company1";
			String busId = "bus1";
			String pan = "4111111111111111";
			String from = "stop3";
			String to = "stop7";
			// When:
			FullJourneyCost fullJourneyCost = fullJourneyCosts.find(journeyCost ->
					StringUtils.equals(journeyCost.busId(), busId)
							&& StringUtils.equals(journeyCost.companyId(), companyId)
							&& StringUtils.equals(journeyCost.pan(), pan)
							&& StringUtils.equals(journeyCost.fromStopId(), from)
							&& StringUtils.equals(journeyCost.toStopId(), to)
			).get();
			// Then:
			assertAll(() -> assertEquals("$2.00", fullJourneyCost.chargeAmount()),
					() -> assertEquals(81720, fullJourneyCost.journeyDurationInSecs()),
					() -> assertEquals(JourneyStatus.COMPLETED, fullJourneyCost.journeyStatus()));
		} catch (IOException e) {
			fail("Exception encountered " + e.getMessage());
		}
	}

	@Test
	public void readCsvFileAndAssertJourneyFare_shouldVerifyIncompleteJourney() {
		try {
			final Queue<Journey> journeys = csvReadWriteService.readCsv();
			final Queue<FullJourneyCost> fullJourneyCosts = journeyComputeService.calculateFullJourneyCost(journeyComputeService.findFullJourney(journeys));

			/* assert incomplete journey of pan # 4111111111111111 from stop5 by bus1 of company1 */
			// Given:
			String companyId = "company1";
			String busId = "bus1";
			String pan = "4111111111111111";
			String from = "stop5";
			String to = null;
			// When:
			FullJourneyCost fullJourneyCost = fullJourneyCosts.find(journeyCost ->
					StringUtils.equals(journeyCost.busId(), busId)
							&& StringUtils.equals(journeyCost.companyId(), companyId)
							&& StringUtils.equals(journeyCost.pan(), pan)
							&& StringUtils.equals(journeyCost.fromStopId(), from)
							&& StringUtils.equals(journeyCost.toStopId(), to)
			).get();
			// Then:
			assertAll(() -> assertEquals("$2.00", fullJourneyCost.chargeAmount()),
					() -> assertEquals(0, fullJourneyCost.journeyDurationInSecs()),
					() -> assertEquals(JourneyStatus.INCOMPLETE, fullJourneyCost.journeyStatus()));
		} catch (IOException e) {
			fail("Exception encountered " + e.getMessage());
		}
	}

	@Test
	public void readCsvFileAndAssertJourneyFare_shouldVerifyCancelledJourney() {
		try {
			final Queue<Journey> journeys = csvReadWriteService.readCsv();
			final Queue<FullJourneyCost> fullJourneyCosts = journeyComputeService.calculateFullJourneyCost(journeyComputeService.findFullJourney(journeys));

			/* assert cancelled journey of pan # 5500005555555559 at stop2 by bus2 of company2 */
			// Given:
			String companyId = "company2";
			String busId = "bus2";
			String pan = "5500005555555559";
			String from = "stop2";
			String to = "stop2";
			// When:
			FullJourneyCost fullJourneyCost = fullJourneyCosts.find(journeyCost ->
					StringUtils.equals(journeyCost.busId(), busId)
							&& StringUtils.equals(journeyCost.companyId(), companyId)
							&& StringUtils.equals(journeyCost.pan(), pan)
							&& StringUtils.equals(journeyCost.fromStopId(), from)
							&& StringUtils.equals(journeyCost.toStopId(), to)
			).get();
			// Then:
			assertAll(() -> assertEquals("$0.00", fullJourneyCost.chargeAmount()),
					() -> assertEquals(9, fullJourneyCost.journeyDurationInSecs()),
					() -> assertEquals(JourneyStatus.CANCELLED, fullJourneyCost.journeyStatus()));
		} catch (IOException e) {
			fail("Exception encountered " + e.getMessage());
		}
	}

}
