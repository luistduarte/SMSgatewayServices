/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Rest.Operations.Send;

import Utils.Database;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;

/**
 * REST Web Service
 *
 * @author root
 */
@Path("outbound")
public class SendingOperations {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of SendingOperations
     */
    public SendingOperations() {
    }

    /**
     * Retrieves representation of an instance of Rest.Operations.Send.SendingOperations
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson() {
        //TODO return proper representation object
        return "test";
    }
    
    
    @Path("{senderAddress}/subscriptions")
    @PUT
    public void getInfo(@PathParam("senderAddress") String senderAddress) {

        Database db = new Database();
        String query = "SELECT * FROM sender WHERE senderAddress='" + senderAddress + "';";
        ResultSet rs = db.executeQuery(query);
        
        try {
            if (rs.next()) {
                boolean notifications = rs.getBoolean("notifications");
                String updateQuery = "UPDATE sender SET notifications = " + !notifications + " WHERE senderAddress='" + senderAddress + "';";
                db.executeUpdate(updateQuery);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SendingOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        db.close();
    }
    
    
    @Path("{senderAddress}/requests")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public String sendSMS(@PathParam("senderAddress") String senderAddress, String content) {

        //return requestID
        return null;
    }

    
    @Path("{senderAddress}/requests/{requestId}/deliveryInfos")
    @PUT
    @Consumes("application/json")
    public void getInfo(@PathParam("senderAddress") String senderAddress, @PathParam("requestId") String requestId, String content) {

    }
}
