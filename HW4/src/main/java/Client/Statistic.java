package Client;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Statistic {
  private int numReq;
  private int numSucc;
  private List<Long> latencies;
  private List<String> postRes;
  private List<String> getRes;

  private List<String[]> postResForPlot;
  private List<String[]> getResForPlot;


  public Statistic(){
    numReq = 0;
    numSucc = 0;
    latencies = new ArrayList<>();
    postRes = new ArrayList<>();
    getRes = new ArrayList<>();

    postResForPlot = new ArrayList<>();
    getResForPlot = new ArrayList<>();
  }

  public void addReq(){
    numReq++;
  }

  public void addSucc(){
    numSucc++;
  }

  public void addLatency(long time){
    latencies.add(time);
//    System.out.println(time);
//    System.out.println(latencies == null);
  }

  public void addPostRes(String res){ postRes.add(res);}

  public void addGetRes(String res){ getRes.add(res);}

  public void addPostResForPlot(String[] res){ postResForPlot.add(res);}

  public void addGetResForPlot(String[] res){ getResForPlot.add(res);}


  public int getNumReq() {
    return numReq;
  }

  public void setNumReq(int numReq) {
    this.numReq = numReq;
  }

  public int getNumSucc() {
    return numSucc;
  }

  public void setNumSucc(int numSucc) {
    this.numSucc = numSucc;
  }

  public List<Long> getLatencies() {
    return latencies;
  }

  public void setLatencies(List<Long> latencies) {
    this.latencies = latencies;
  }

  public long getThroughPut(){
    long cnt = 0;
    for(String res : postRes){
//      System.out.println(res);
//      System.out.println(res.equals("OK!"));
      if(res != null && res.equals("OK!")){
        cnt++;
      }
    }
    for(String res : getRes){
//      System.out.println(res);
      try{
        int num = Integer.parseInt(res);
        cnt++;
      }
      catch(Exception e){}
    }
//    System.out.println(cnt);
    return cnt;
  }

  public List<Long> getThroughPutForPlot(){
    List<Long> temp = new ArrayList<>();


    for(String[] res : postResForPlot){
//      System.out.println(res[0]);
//      System.out.println(res[0].equals("OK!"));
      if(res[0] != null && res[0].equals("OK!")){
        temp.add(Long.parseLong(res[1]));
      }
    }

    for(String[] res : getResForPlot){
//      System.out.println(res);
      try{
        int num = Integer.parseInt(res[0]);
        temp.add(Long.parseLong(res[1]));
      }
      catch(Exception e){}
    }
//    System.out.println(cnt);
    return temp;

//    DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();
//    for(int i = 0; i < temp.size(); i++){
//      line_chart_dataset.addValue(i+1,"throughput",String.valueOf(temp.get(i)));
//    }
//
//
//    JFreeChart lineChartObject = ChartFactory.createLineChart(
//            "througput Vs times","time",
//            "throughput",
//            line_chart_dataset, PlotOrientation.VERTICAL,
//            true,true,false);
//
//    int width = 640; /* Width of the image */
//    int height = 480; /* Height of the image */
//    File lineChart = new File( "out.jpeg" );
//    try {
//      ChartUtilities.saveChartAsJPEG(lineChart ,lineChartObject, width ,height);
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//    return cnt;
  }


}
