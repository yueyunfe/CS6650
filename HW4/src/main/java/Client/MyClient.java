package Client;


import com.neu.distributedsystem.server.Database.DatastoreConn;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;


public class MyClient {

//    private static String address = "http://localhost:8080";
//    HW2LB-1830832122.us-east-2.elb.amazonaws.com
//    private static String address = "http://HW3loadbalancer-2069881694.us-west-2.elb.amazonaws.com:8080";

//    private static String address = "http://34.215.130.74:8080";

//    private static String address = "http://34.216.171.207:8080";
    //  private static String address = "https://wlcv8chfv5.execute-api.us-east-1.amazonaws.com/Prod/myresource";

  //lambda
//    private static String address = "https://jhatbelym7.execute-api.us-west-2.amazonaws.com/Prod";

  //GCP
//  private static String address = "http://35.190.79.27:8080";


//  private static String address = "http://35.247.112.104:8080";

  private static String address = "http://34.220.241.46:8080";
  private static String port = "8080";



    private static int maxThreads = 256;

    private static int numIter = 80;

    private static double[] percent = {0.1,0.5,1,0.25};

    private static int maxDayNum = 1;

    private static int userPopulation = 100000;

    private static int maxSteps = 5000;

    private static String[] phases = {"Warmup phase","Loading phase","Peak phase","Cooldown phase"};

    private static int[][] timeIntervals = {{0,2},{3,7},{8,18},{19,23}};

    private static final String prefix = "HW2/webapi/myresource/";
//    private static final String prefix = "ping/";

//    private static long prevEnd = System.currentTimeMillis();

    public static void main(String[] args) throws IOException {

      if(args.length > 0){
            address = args[0];
            port = args[1];
            maxThreads = Integer.parseInt(args[2]);
            numIter = Integer.parseInt(args[3]);
        }



        System.out.println(address);


        final Client client = ClientBuilder.newClient();
        System.out.println(maxThreads);
//
//        final long prevEnd = System.currentTimeMillis();
        List<Statistic> plot = new ArrayList<>();

        for( int i = 0; i < percent.length; i++) {
            client.target(address).path(prefix+"clear").request().delete();

            int numThreads = (int) (maxThreads * percent[i]);
            List<Statistic> list = new ArrayList<>();
            ExecutorService executorService = Executors.newFixedThreadPool(numThreads);


            System.out.println(phases[i] + " running ");

            final int var = i;
            final long start = System.currentTimeMillis();
//            final long delta = start - prevEnd;
            for (int j = 0; j < numThreads; j++) {

                for(int t = 0; t < numIter; t++){
                    final Statistic statistic = new Statistic();
                    list.add(statistic);
                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {

                            for (int k = 0; k < 3; k++) {
                                int users = ThreadLocalRandom.current().nextInt(1, userPopulation);
                                int intervals = ThreadLocalRandom.current().nextInt(timeIntervals[var][0], timeIntervals[var][1]);
                                int stepCounts = ThreadLocalRandom.current().nextInt(1, maxSteps);

                                long start1 = System.currentTimeMillis();
                                String res = client.target(address).path(prefix + users + "/1/" + intervals + "/" + stepCounts).request(MediaType.TEXT_PLAIN)
                                        .post(Entity.entity("", MediaType.TEXT_PLAIN), String.class);
                                long end1 = System.currentTimeMillis();
//                                System.out.println(res);
//                               statistic.addPostResForPlot(new String[]{res, String.valueOf(prevEnd + end1 - start)});
                                statistic.addLatency(end1-start1);
                                statistic.addPostRes(res);

                                long start2 = System.currentTimeMillis();
                                String res2 = client.target(address).path(prefix + "single/"+users + "/1").request(MediaType.TEXT_PLAIN)
                                        .get(String.class);

                                long end2 = System.currentTimeMillis();
//                              statistic.addGetResForPlot(new String[]{res, String.valueOf(prevEnd + end2 - start)});
                                statistic.addLatency(end2-start2);
                                statistic.addGetRes(res2);

                            }

                        }
                    });
                }

            }
            executorService.shutdown();
            while(!executorService.isTerminated());

            long end = System.currentTimeMillis();
            System.out.println("phase:"+phases[i]);
            System.out.println("total time:" +(end - start)+"ms");



            List<Long> latencies = new ArrayList<>();
            long totalThrough = 0;

            for(Statistic statistic :list ){
                latencies.addAll(statistic.getLatencies());
                long res = statistic.getThroughPut();
                totalThrough += res;

            }

            Collections.sort(latencies);
            long sum = 0;
            for(long latency : latencies){
                sum += latency;
            }
            float mean = sum/(float)(latencies.size());
            float median =
                    latencies.size()%2 == 0? (latencies.get((latencies.size()-1)/2) + latencies.get((latencies.size()-1)/2+1))/2 : latencies.get((latencies.size()-1)/2);

            System.out.println("mean latency:"+mean +"ms");
            System.out.println("median latency:"+median +"ms");
            System.out.println("99th latency:"+latencies.get((int)(latencies.size()*0.99))+"ms");
            System.out.println("95th latency:"+latencies.get((int)(latencies.size()*0.95))+"ms");
            System.out.println("total throughput:" + totalThrough);
            System.out.println("avg throughput(per second):" + totalThrough/((end-start)/1000));

            System.out.println("=======================");

        }

        client.close();



    }

}
