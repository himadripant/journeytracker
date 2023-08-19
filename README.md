# Please note, here are some assumptions and work arounds I've done to expedite
* Journeys can take infinite time to complete.
* Please consider the class StopFaresDaoImpl.java as DAO where `fareTable` mirrors a cached database table data.
* Output is generated in the console in logs
* Spring Boot 3.+, Java 17

# Run
* Add data to file `resources/taps.csv`, please refer to class StopFaresDaoImpl.java, you might need to add records based on bus fares (per company, bus ID, stops)
* Launch via command line ```./gradlew bootRun``` alternatively via IDE with default spring boot run configuration