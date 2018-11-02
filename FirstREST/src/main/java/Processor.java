
import javax.ws.rs.core.Response;
import java.util.Random;
import java.util.Set;


public class Processor implements Runnable {


    private RestClient restClient;

    private long iterationNum;
    private Report report;
    private int userPopulation;
    private int dayNumber;

    public Processor(RestClient restClient, long iteration, Report report, int userPopulation, int dayNumber) {
        this.restClient = restClient;
        this.iterationNum = iteration;
        this.report = report;

        this.userPopulation = userPopulation;

        this.dayNumber = dayNumber;
    }

    public void run() {
        for(int i = 0; i < iterationNum; i++){

            //generate id, time interval, and setpCound

            User[] users = new User[Constants.NUMBER_OF_USERS];
            for(int j = 0; j < Constants.NUMBER_OF_USERS; j++){
                Random random = new Random();
                int id = random.nextInt(userPopulation);

                int time = random.nextInt(Constants.TIME_INTERVAL);
                int stepCount = random.nextInt(Constants.STEP_COUNT);
                users[j] = new User(id, time, stepCount, dayNumber);
            }

            long getStartTime = System.currentTimeMillis();
            //POST /userID1/day/timeInterval1/stepCount1
            restClient.doUserPosting(users[0], report);
//            POST /userID2/day/timeInterval2l/stepCount2
            restClient.doUserPosting(users[1], report);
//            GET /current/userID1
            restClient.getUserRecentStepCount(users[0], report);
//            GET/single/userID2/day
            restClient.getUserStepCountAtDay(users[1], report);
//            POST /userID3/day/timeInterval3/stepCount3
            restClient.doUserPosting(users[2], report);

            long getEndingTime = System.currentTimeMillis();
            report.addLatencies(getEndingTime - getStartTime);


        }

    }


}
