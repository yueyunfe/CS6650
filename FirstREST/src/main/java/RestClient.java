

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class RestClient {


    private final WebTarget baseTarget;
    private final String serverUrl = "webService";
    private Connection connection;

    public RestClient(Client client, String ipAddress, String port, Connection connection){
        String url = "http://" + ipAddress + ":" + port + "/FirstREST_war/web";
        this.baseTarget = client.target(url);
        this.connection = connection;
    }

    public  Response getStatus () throws ClientErrorException{
        WebTarget serverTarget = baseTarget.path(serverUrl);
        Invocation.Builder request = serverTarget.request(MediaType.TEXT_PLAIN);
        Response response = request.get();
        return response;
    }

    public  Response askForPosting(String data){
        WebTarget serverTarget = baseTarget.path(serverUrl);
        Invocation.Builder request = serverTarget.request(MediaType.TEXT_PLAIN);
        Response response = request.post(Entity.entity(data, MediaType.TEXT_PLAIN));

        return response;
    }

    public int getUserRecentStepCount(User user, Report report){
        Response response = this.getStatus();
        int totalStepCount = 0;
        if(connection == null){
            throw new IllegalArgumentException("No connection to the database");
        }
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String queryGetRecentDay = "Select MAX(dayNumber) from users where userId = " + user.getId();
        try {
            preparedStatement = connection.prepareStatement(queryGetRecentDay);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int recentDayNumber = resultSet.getInt(1);

            String query = "Select stepCount from users" +
                    " where userId = " + user.getId() + " and dayNumber = " + recentDayNumber;
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                totalStepCount += resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException("Unable to get stepCount");
        }
        report.addBucket(System.currentTimeMillis());
        response.close();
        return totalStepCount;
    }


    public void doUserPosting(User user, Report report) {
        Response response = this.getStatus();

        String query = "INSERT INTO users (userId, dayNumber, timeInterval, stepCount) VALUES(" + user.getId()
                +"," + user.getDayNumber() + "," + user.getTime() + "," + user.getStepCount() + ")";

        PreparedStatement preparedStatement = null;
        //ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        report.addBucket(System.currentTimeMillis());
        response.close();

    }

    public int getUserStepCountAtDay(User user, Report report){
        Response response = this.getStatus();
        int totalStepCount = 0;
        if(connection == null){
            throw new IllegalArgumentException("No connection to the database");
        }
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;


        try {
            String query = "Select stepCount from users" +
                    " where userId = " + user.getId() + " and dayNumber = " + user.getDayNumber();
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                totalStepCount += resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException("Unable to get stepCount");
        }
        report.addBucket(System.currentTimeMillis());
        response.close();
        return totalStepCount;
    }

    public void deleteAllData() {

        if(connection == null){
            throw new IllegalArgumentException("No connection to the database");
        }
        PreparedStatement preparedStatement = null;


        try {
            String query = "DELETE from users";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalArgumentException("Unable to get stepCount");
        }
    }
}
