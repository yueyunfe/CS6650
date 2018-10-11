package services;


import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("webService")
public class WebService {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Received it from Service1!";

    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response postIt(String data) {
        if(data.length() == 0){
            return Response.status(400).entity("Please provide data").build();
        }
        return Response.status(300).entity("Post it to Service!").build();
    }

}
