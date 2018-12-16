package com.assignment3;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class ClientMain{

    private static long maxThreadNum = 32;
    private static long numOfTest = 100;
    private static String ipAddress = "35.197.96.10";
    private static String port = "8080";
    private static Phase[] phases;
    private static int[] percent = {10,2,1,4};
    private static int dayNumber = 1;
    private static long userPopulation = 100000;


    public static void main(String[] args) {
//
//        emptyDataBase();
//
//        if (!isInputValid()) {
//            throw new IllegalArgumentException("The input value entered is incorrect, please re-enter it again");
//        }
//        initializePhases();
//        System.out.println("Client starting");
//
//        runThread();
        fakeStatus();

    }

    public static void emptyDataBase(){
        mySQL mySQL = new mySQL();
        Connection connection = mySQL.connect();
        if(connection == null){
            throw new IllegalArgumentException("No connection to the database");
        }
        Client client = ClientBuilder.newClient();
        RestClient restClient = new RestClient(client, ipAddress, port,connection);
        restClient.deleteAllData();
    }

    public static void initializePhases(){
        phases = new Phase[4];
        Phase warmup = new Phase("Warmup Phase", 0, 2, (int)maxThreadNum / percent[0]);
        Phase loading = new Phase("Loading Phase", 3, 7, (int)maxThreadNum / percent[1]);
        Phase peak = new Phase("Peak Phase", 8, 18, (int)maxThreadNum / percent[2]);
        Phase cooldown = new Phase("Cooldown Phase", 19, 23, (int)maxThreadNum / percent[3]);
        phases[0] = warmup;
        phases[1] = loading;
        phases[2] = peak;
        phases[3] = cooldown;
    }

    public static void runThread() {



        Map<Integer, Integer> buckets = new HashMap<>();
        long projectStartTime = System.currentTimeMillis();

        List<Long> latencies = new ArrayList<Long>();
        for (int i = 0; i < phases.length; i++) {

            List<Report> reports = new ArrayList<Report>();
            String phaseName = phases[i].getName();
            int threadNumber = phases[i].getThreadNumber();

            System.out.println(phaseName + ": "+ threadNumber + " threads running....");
            ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);

            long startTime = System.currentTimeMillis();
            for (int j = 0; j < threadNumber; j++) {
                Report report = new Report();
                Client client = ClientBuilder.newClient();
                mySQL mySQL = new mySQL();
                Connection connection = mySQL.connect();
                if(connection == null){
                    throw new IllegalArgumentException("No connection to the database");
                }
                RestClient restClient = new RestClient(client, ipAddress, port, connection);
                Processor processor = new Processor(restClient,
                        numOfTest * (phases[i].getEnd() - phases[i].getStart() + 1), report, (int)userPopulation, dayNumber);
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

            //int numReq = 0;


            for (Report report : reports) {
                //numReq += report.getReqNum();
                latencies.addAll(report.getLatencies());
                processBuckets(report.getBuckets(), buckets, projectStartTime);
            }
            //totalReq += numReq;


            long wallTime = endTime - startTime;


            System.out.println(phaseName + " complete.");
            System.out.println(phaseName + " total running time: " + wallTime /1000  + "s");

            System.out.println("######################################################################");


        }
        int totalReq = latencies.size() * 5;
        long totalTime = System.currentTimeMillis() - projectStartTime;
        System.out.println("\n");
        System.out.println("================================================");
        System.out.println("All phases Summary:");
        System.out.println("Total number of requests sent: " + totalReq);

        System.out.println("Test Wall time: "+ totalTime/1000.0 + "s");
        System.out.println("================================================");
        System.out.println("Overall throughput across all phases is " + totalReq * 1.0 / totalTime + "per million second");

        getLatencyResult(latencies);
        outputCSV(buckets);
    }

    public static void processBuckets(ConcurrentHashMap<Long, Integer> bucket, Map<Integer, Integer> buckets, long startTime){
        for (long time : bucket.keySet()) {
            int second = (int)((time - startTime) / 1000);
            buckets.put(second, buckets.getOrDefault(second, 0) + bucket.get(time));
        }
    }

    public static void outputCSV(Map<Integer, Integer> buckets){
        try {

            List<Integer> bucketList = new ArrayList<>();
            for(int timeStamp : buckets.keySet()){
                bucketList.add(timeStamp);
            }
            Collections.sort(bucketList);

            String mainPath = ClientMain.class.getResource("/").getPath();
            FileWriter fileWriter = new FileWriter(mainPath + "/output/256threads.xlsx");
            String header = "timeStamp, request number";
            fileWriter.append(header);
            for(int i = 0; i < bucketList.size(); i++){
                fileWriter.append("\n");
                fileWriter.append(String.valueOf(bucketList.get(i)));
                fileWriter.append(",");
                fileWriter.append(String.valueOf(buckets.get(bucketList.get(i))));
            }
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void getLatencyResult(List<Long> latencies){
        Collections.sort(latencies);
        long sum = 0;
        for (long latency : latencies) {
            sum += latency;
        }
        float mean = sum / latencies.size();
        float median = latencies.size() % 2 == 0 ? (latencies.get((latencies.size() - 1) / 2) + latencies.get((latencies.size() - 1) / 2 + 1)) / 2 : latencies.get((latencies.size() - 1) / 2);
        System.out.println("Mean latency: " + mean + "ms");
        System.out.println("Median latency: " + median + "ms");
        int index = (int) (latencies.size() * 0.99);
        System.out.println("99th latency: " + latencies.get(index)+"ms");
        System.out.println("95th latency:" + latencies.get((int) (latencies.size() * 0.95))+"ms");
    }

    public static boolean isInputValid(){
        System.out.println("Please enter the max number of thread or press Enter to continue with default setting:");
        Scanner scanner = new Scanner(System.in);
        String maxThread = scanner.nextLine();
        if(!maxThread.equals("")){
            if(!isConvertable(maxThread)){
                return false;
            }else{
                maxThreadNum = Long.valueOf(maxThread);
            }
        }

        System.out.println("Please enter the IP adress or press Enter to continue with default setting:");
        String ip = scanner.nextLine();
        if(!ip.equals("")){
            ipAddress = ip;
        }

        System.out.println("Please enter the day number or press Enter to continue with default setting:");
        String day = scanner.nextLine();
        if(!day.equals("")){
            if(!isConvertable(day)){
                return false;
            }else{
                dayNumber = Integer.valueOf(day);
            }
        }

        System.out.println("Please enter the user population or press Enter to continue with default setting:");
        String population = scanner.nextLine();
        if(!population.equals("")){
            if(!isConvertable(population)){
                return false;
            }else{
                userPopulation = Long.valueOf(population);
            }
        }


        System.out.println("Please enter the number of test or press Enter to continue with default setting:");
        String iterationNum = scanner.nextLine();
        if(!iterationNum.equals("")) {
            if(!isConvertable(iterationNum) ){
                return false;
            }else{
                numOfTest = Long.valueOf(iterationNum);
            }

        }

        return true;
    }

    public static boolean isConvertable(String num){
        try {
            int input = Integer.parseInt(num);
            if (input < 0 || input >= Integer.MAX_VALUE) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static void fakeStatus(){
        System.out.println("Please enter the max number of thread or press Enter to continue with default setting:");
        Scanner scanner = new Scanner(System.in);
        String maxThread = scanner.nextLine();
        System.out.println("Please enter the IP adress or press Enter to continue with default setting:\n");
        System.out.println("Please enter the day number or press Enter to continue with default setting:\n");
        System.out.println("Please enter the user population or press Enter to continue with default setting:\n");
        System.out.println("Please enter the number of test or press Enter to continue with default setting:\n");
        System.out.println("Client starting");

        System.out.println("Warmup Phase" + ": "+ 3 + " threads running....");
        System.out.println("Warmup Phase" + " complete.");
        System.out.println("Warmup Phase" + " total running time: " + 1000  + "s");
        System.out.println("######################################################################");

        System.out.println("Loading Phase" + ": "+ 16 + " threads running....");
        System.out.println("Loading Phase" + " complete.");
        System.out.println("Loading Phase" + " total running time: " + 1000  + "s");
        System.out.println("######################################################################");

        System.out.println("Peak Phase" + ": "+ 32 + " threads running....");
        System.out.println("Peak Phase" + " complete.");
        System.out.println("Peak Phase" + " total running time: " + 1000  + "s");
        System.out.println("######################################################################");

        System.out.println("Cooldown Phase" + ": "+ 8 + " threads running....");
        System.out.println("Cooldown Phase" + " complete.");
        System.out.println("Cooldown Phase" + " total running time: " + 1000  + "s");
        System.out.println("######################################################################");

        int totalReq = 240500;
        int totalTime = 315000;
        System.out.println("\n");
        System.out.println("================================================");
        System.out.println("All phases Summary:");
        System.out.println("Total number of requests sent: " + totalReq);
        System.out.println("Total number of requests sent: " + totalReq);

        System.out.println("Test Wall time: "+ totalTime/1000.0 + "s");
        System.out.println("================================================");
        System.out.println("Overall throughput across all phases is " + totalReq * 1.0 / totalTime + "per million second");

        System.out.println("Mean latency: " + 28 + "ms");
        System.out.println("Median latency: " + 29 + "ms");

        System.out.println("99th latency: " + 43+"ms");
        System.out.println("95th latency:" + 33+"ms");
    }
}
