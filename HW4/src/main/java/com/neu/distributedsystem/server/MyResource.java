package com.neu.distributedsystem.server;


import com.neu.distributedsystem.server.DAO.DailyCounterDAO;
import com.neu.distributedsystem.server.DO.DailyCounter;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {



    DailyCounterDAO dailyCounterDAO = new DailyCounterDAO();


    @POST
    @Path("/{userID}/{dayID}/{timeInterval}/{stepCount}")
    @Consumes(MediaType.TEXT_PLAIN)
    public String postData(@PathParam("userID") String userID,
                           @PathParam("dayID") String dayID,
                           @PathParam("timeInterval") String timeInterval,
                           @PathParam("stepCount") String stepCount) throws SQLException {

        DailyCounter dailyCounter = new DailyCounter(Integer.parseInt(userID),
                Integer.parseInt(dayID),
                Integer.parseInt(timeInterval),
                Integer.parseInt(stepCount));
        dailyCounterDAO.insert(dailyCounter);
        return "OK!";
    }




    @GET
    @Path("/single/{userID}/{dayID}")
    @Produces(MediaType.TEXT_PLAIN)
    public int getByDay(@PathParam("userID") String userID,
                        @PathParam("dayID") String dayID) throws SQLException {
        int sum = dailyCounterDAO.getCountForDay(Integer.parseInt(userID), Integer.parseInt(dayID));
        return sum;
    }


    @GET
    @Path("/current/{userID}")
    @Produces(MediaType.TEXT_PLAIN)
    public int getByDay(@PathParam("userID") String userID) throws SQLException {
        int sum = dailyCounterDAO.getCurrentDay(Integer.parseInt(userID));
        return sum;
    }



    @DELETE
    @Path("/clear")
    @Produces(MediaType.TEXT_PLAIN)
    public String deleteTable() throws SQLException {
        dailyCounterDAO.clear();
        return "Clear!";
    }


}
