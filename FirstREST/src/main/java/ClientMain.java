import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class ClientMain{

    private static long maxThreadNum = 50;
    private static long numOfInter = 100;
    private static String ipAddress = "ec2-54-184-224-154.us-west-2.compute.amazonaws.com";
    private static String port = "8080";
    private static String[] phases = {"Warmup Phase","Loading Phase","Peak Phase","Cooldown Phase"};
    private static int[] percent = {10,2,1,4};

    public static void main(String[] args) {

        if (!isInputValid()) {
            throw new IllegalArgumentException("The input value entered is incorrect, please re-enter it again");

        }
        runThread();
    }

    public static void runThread() {
        int totalReq = 0;
        int totalSucc = 0;
        long totalTime = 0;
        for (int i = 0; i < phases.length; i++) {
            System.out.println(phases[i] + ": All threads running");
            Client client = ClientBuilder.newClient();
            RestClient restClient = new RestClient(client, ipAddress, port);
            List<Report> reports = new ArrayList<Report>();
            long startTime = System.currentTimeMillis();
            int threadNumber = (int) maxThreadNum / percent[i];
            ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);
            for (int j = 0; j < threadNumber; j++) {
                Report report = new Report();
                Processor processor = new Processor(restClient, numOfInter, report);
                executorService.submit(processor);
                reports.add(report);
            }
            executorService.shutdown();
            while (!executorService.isTerminated());
            client.close();
            long endTime = System.currentTimeMillis();

            int numReq = 0;
            int numSucc = 0;
            List<Long> latencies = new ArrayList<Long>();
            for (Report report : reports) {
                numReq += report.getReqNum();
                numSucc += report.getSucNum();
                latencies.addAll(report.getLatencies());
            }
            totalReq += numReq;
            totalSucc += numSucc;
            Collections.sort(latencies);
            long wallTime = endTime - startTime;
            totalTime += wallTime;
            long sum = 0;
            for (long latency : latencies) {
                sum += latency;
            }
            float throughput = numReq / (float)(wallTime / 1000);
            float mean = sum / latencies.size();
            float median = latencies.size() % 2 == 0 ? (latencies.get((latencies.size() - 1) / 2) + latencies.get((latencies.size() - 1) / 2 + 1)) / 2 : latencies.get((latencies.size() - 1) / 2);

            System.out.println(phases[i] + " complete.");
            System.out.println(phases[i] + "run time: " + wallTime);
            System.out.println(phases[i] + "number of requests: " + numReq);
            System.out.println(phases[i] + " number of success: " + numSucc);
            System.out.println(phases[i] + "throughput: " + throughput);
            System.out.println(phases[i] +  "mean latency: " + mean);
            System.out.println(phases[i] +  "median latency: " + median);
            System.out.println(phases[i] + "99th latency: " + latencies.get((int) (latencies.size() * 0.99)));
            System.out.println(phases[i] + "95th latency:" + latencies.get((int) (latencies.size() * 0.95)));
        }

        System.out.println("All phases Summary:");
        System.out.println("Total number of requests: "+totalReq);
        System.out.println("Total success requests: "+totalSucc);
        System.out.println("Total time: "+ totalTime);
    }



    public static boolean isInputValid(){
        System.out.println("Please enter the max number of thread or press Enter to continue with default setting:");
        Scanner scanner = new Scanner(System.in);
        String maxThread = scanner.nextLine();
        if(!maxThread.equals("") && !isConvertable(maxThread)){
            return false;
        }
        System.out.println("Please enter the number of iterations or press Enter to continue with default setting:");
        String iterationNum = scanner.nextLine();
        if(!iterationNum.equals("") && !isConvertable(iterationNum) ) {
            return false;
        }

        System.out.println("Please enter the IP adress or press Enter to continue with default setting:");
        String ip = scanner.nextLine();
        if(!ip.equals("")){
            ipAddress = ip;
        }

        System.out.println("Please enter the port or press Enter to continue with default setting:");
        String portNum = scanner.nextLine();
        if(!portNum.equals("")){
            port = portNum;
        }
        return true;
    }

    public static boolean isConvertable(String num){
        try {
            numOfInter = Integer.parseInt(num);
            if (numOfInter < 0 || numOfInter >= Integer.MAX_VALUE) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
