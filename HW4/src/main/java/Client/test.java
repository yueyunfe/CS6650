//package Client;
//
//import org.jfree.chart.ChartColor;
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartUtilities;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.plot.CategoryPlot;
//import org.jfree.chart.plot.PlotOrientation;
//import org.jfree.data.category.DefaultCategoryDataset;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.LinkedList;
//import java.util.List;
//
//public class test {
//
//  public static void main(String[] args) throws IOException {
////    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
////    dataset.addValue(1, 1, 2);


////            dataset, PlotOrientation.VERTICAL, true, true, true);
////
////    CategoryPlot cp = chart.getCategoryPlot();

////
////    // ChartFrame frame=new ChartFrame ("水果产量图 ",chart,true);
////    // frame.pack();
////    // frame.setVisible(true);
////
////    try {
////      ChartUtilities.saveChartAsPNG(new File("LineChart.png"),
////              chart, 800, 500);
////    } catch (IOException e) {
////      e.printStackTrace();
////    }
//
//    List<Integer> throughputTime = new LinkedList<>();
//    for(int num : arr){
//      throughputTime.add(num);
//    }
//
//    List<Long> res = new LinkedList<>();
//    int cnt = 0;
//    long curr = 1000;
//    for(int i = 0; i < throughputTime.size(); i++){
//      while(i < throughputTime.size() && throughputTime.get(i) <= curr){
//        cnt++;
//        i++;
//      }
//      res.add((long)cnt);
//      cnt=0;
//      i--;
//      curr += 100;
//    }
//
//////
//////
//////
//    DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();
////    Iterator iterator = throughputTime.iterator();
//    for(int i = 0; i < res.size(); i++){
//      line_chart_dataset.addValue(res.get(i),"throughput", String.valueOf(i*1000));
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
//    File lineChart = new File( "out2.jpeg" );
//
//      ChartUtilities.saveChartAsJPEG(lineChart ,lineChartObject, width ,height);
//
////
////
////    DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();
////    line_chart_dataset.addValue( 15 , "schools" , "1.23" );
////    line_chart_dataset.addValue( 30 , "schools" , "1.434" );
////    line_chart_dataset.addValue( 60 , "schools" , "23.432" );
////    line_chart_dataset.addValue( 120 , "schools" , "2000" );
////    line_chart_dataset.addValue( 240 , "schools" , "2010" );
////    line_chart_dataset.addValue( 300 , "schools" , "2014" );
////
////    JFreeChart lineChartObject = ChartFactory.createLineChart(
////            "Schools Vs Years","Year",
////            "Schools Count",
////            line_chart_dataset,PlotOrientation.VERTICAL,
////            true,true,false);
////
////    int width = 640; /* Width of the image */
////    int height = 480; /* Height of the image */
////    File lineChart = new File( "LineChart2.jpeg" );
////    ChartUtilities.saveChartAsJPEG(lineChart ,lineChartObject, width ,height);
//
//  }
//}
