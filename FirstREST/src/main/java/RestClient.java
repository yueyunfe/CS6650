

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;



public class RestClient {


    private final WebTarget baseTarget;
    private final String serverUrl = "webService";

    public RestClient(Client client, String ipAddress, String port){
        String url = "http://" + ipAddress + ":" + port + "/FirstREST_war/web";
        this.baseTarget = client.target(url);
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


}
