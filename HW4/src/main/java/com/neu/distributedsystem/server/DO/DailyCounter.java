package com.neu.distributedsystem.server.DO;

public class DailyCounter {
  private int userId;
  private int currDay;
  private int timeInterval;
  private int stepCount;

  public DailyCounter(int userId, int day, int timeInterval, int stepCount){
    this.userId = userId;
    this.currDay = day;
    this.timeInterval = timeInterval;
    this.stepCount = stepCount;
  }

  public int getUserId() {
    return userId;
  }

  public int getTimeInterval() {
    return timeInterval;
  }

  public int getDay() {
    return currDay;
  }


  public int getStepCount() {
    return stepCount;
  }

  @Override
  public String toString() {
    return "DailyCounter{" +
            "userId='" + userId + '\'' +
            ", currDay='" + currDay + '\'' +
            ", timeInterval='" + timeInterval + '\'' +
            ", stepCount='" + stepCount + '\'' +
            '}';
  }
}
