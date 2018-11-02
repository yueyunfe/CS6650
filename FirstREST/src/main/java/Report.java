import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Report {
    private int reqNum = 0;
    private int sucNum = 0;
    private List<Long> latencies;
    private List<Integer> requestes = new ArrayList<Integer>();
    private ConcurrentHashMap<Long, Integer> buckets;

    public Report() {
        buckets = new ConcurrentHashMap<>();
        latencies = new ArrayList<Long>();
    }

    public void addSuccess(){
        sucNum++;
    }

    public void addLatencies(long time){
        latencies.add(time);
    }

    public void addRequest(){
        reqNum++;
    }


    public int getReqNum() {
        return reqNum;
    }


    public void addBucket(long time){
        if(time < 0){
            throw new IllegalArgumentException("Time is not valid");
        }
        buckets.put(time, buckets.getOrDefault(time, 0) + 1);

    }

    public List<Long> getLatencies() {
        return latencies;
    }

    public ConcurrentHashMap<Long, Integer> getBuckets() {
        return buckets;
    }
}
