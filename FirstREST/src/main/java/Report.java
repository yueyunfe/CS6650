import java.util.ArrayList;
import java.util.List;

public class Report {
    private int reqNum = 0;
    private int sucNum = 0;
    private List<Long> latencies = new ArrayList<Long>();

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

    public int getSucNum() {
        return sucNum;
    }

    public List<Long> getLatencies() {
        return latencies;
    }
}
