
import javax.ws.rs.core.Response;
public class Processor implements Runnable {


    private RestClient restClient;
    private int id = 0;
    private long iterationNum;
    private Report report;
    public Processor(RestClient restClient, long iteration, Report report) {
        this.restClient = restClient;
        this.iterationNum = iteration;
        this.report = report;
    }

    public void run() {


        for(int i = 0; i < iterationNum; i++){
            long getStartTime = System.currentTimeMillis();
            Response getResponse = restClient.getStatus();
            report.addRequest();
            if(getResponse.getStatus() == 200){
                report.addSuccess();
            }
            long getEndingTime = System.currentTimeMillis();
            report.addLatencies(getEndingTime - getStartTime);
            getResponse.close();

            long postStartTime = System.currentTimeMillis();
            Response postResponse = restClient.askForPosting("helloWorld");
            postResponse.readEntity(String.class);
            report.addRequest();
            if(getResponse.getStatus() == 200){
                report.addSuccess();
            }
            long postEndingTime = System.currentTimeMillis();
            report.addLatencies(postEndingTime - postStartTime);
            postResponse.close();
        }

    }


}
