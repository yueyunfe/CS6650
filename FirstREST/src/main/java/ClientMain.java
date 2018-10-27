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

    private static long maxThreadNum = 20;
    private static long numOfInter = 100;
    private static String ipAddress = "ec2-54-213-240-203.us-west-2.compute.amazonaws.com";
    private static String port = "8080";
    private static String[] phases = {"Warmup Phase","Loading Phase","Peak Phase","Cooldown Phase"};
    private static int[] percent = {10,2,1,4};



    public static void main(String[] args) {

        if (!isInputValid()) {
            throw new IllegalArgumentException("The input value entered is incorrect, please re-enter it again");

        }
        System.out.println("Client starting");
        runThread();

    }

    public static void runThread() {
        int totalReq = 0;
        int totalSucc = 0;
        long totalTime = 0;
        List<Long> latencies = new ArrayList<Long>();
        for (int i = 0; i < phases.length; i++) {

            List<Report> reports = new ArrayList<Report>();

            int threadNumber = (int) maxThreadNum / percent[i];
            System.out.println(phases[i] + ": "+ threadNumber + " threads running....");
            ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);

            long startTime = System.currentTimeMillis();
            for (int j = 0; j < threadNumber; j++) {
                Report report = new Report();
                Client client = ClientBuilder.newClient();
                RestClient restClient = new RestClient(client, ipAddress, port);
                Processor processor = new Processor(restClient, numOfInter, report);
                executorService.submit(processor);
                reports.add(report);
            }
            try {
                executorService.shutdown();
                executorService.awaitTermination(1, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            long endTime = System.currentTimeMillis();

            int numReq = 0;
            int numSucc = 0;

            for (Report report : reports) {
                numReq += report.getReqNum();
                numSucc += report.getSucNum();
                latencies.addAll(report.getLatencies());
            }
            totalReq += numReq;
            totalSucc += numSucc;

            long wallTime = endTime - startTime;
            totalTime += wallTime;

            System.out.println(phases[i] + " complete.");
            System.out.println(phases[i] + " total running time: " + wallTime /1000  + "s");
            //System.out.println(phases[i] + " number of requests: " + numReq);
            //System.out.println(phases[i] + " number of success: " + numSucc);

            //System.out.println(phases[i] +  " mean latency: " + mean);
            //System.out.println(phases[i] +  " median latency: " + median);
           // System.out.println(phases[i] + " 99th latency: " + latencies.get((int) (latencies.size() * 0.99)));
            //System.out.println(phases[i] + " 95th latency:" + latencies.get((int) (latencies.size() * 0.95)));
            System.out.println("######################################################################");
            //System.out.println("######################################################################");

        }
        System.out.println("\n");
        System.out.println("================================================");
        System.out.println("All phases Summary:");
        System.out.println("Total number of requests sent: "+totalReq);
        System.out.println("Total number of Successful response: "+totalSucc);
        System.out.println("Test Wall time: "+ totalTime/1000.0 + "s");
        System.out.println("================================================");
        System.out.println("Overall throughput across all phases is " + totalReq * 1.0 / totalTime);
        Collections.sort(latencies);
        getLatencyResult(latencies);
    }


    public static void getLatencyResult(List<Long> latencies){
        long sum = 0;
        for (long latency : latencies) {
            sum += latency;
        }
        float mean = sum / latencies.size();
        float median = latencies.size() % 2 == 0 ? (latencies.get((latencies.size() - 1) / 2) + latencies.get((latencies.size() - 1) / 2 + 1)) / 2 : latencies.get((latencies.size() - 1) / 2);
        System.out.println("Mean latency: " + mean);
        System.out.println("Median latency: " + median);
        System.out.println("99th latency: " + latencies.get((int) (latencies.size() * 0.99)));
        System.out.println("95th latency:" + latencies.get((int) (latencies.size() * 0.95)));
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
