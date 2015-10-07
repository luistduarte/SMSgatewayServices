/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Rest.Operations.Send;

import Models.receiver;
import Models.receivers;
import Models.request;
import Models.requestDeliveryInfo;
import Models.sender;
import Models.state;
import Utils.Database;
import Utils.HttpCodes;
import Utils.HttpStatus;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
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
     * Retrieves representation of an instance of
     * Rest.Operations.Send.SendingOperations
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson() {
        /*
         Gson gson = new Gson();
         List<receiver> recetores = new ArrayList<receiver>();
         sender send = new sender(916056618);
         state sta = new state(1, "pending");
         receiver receiver1 = new receiver(123456788);
         receiver receiver2 = new receiver(123456777);
         String message = "Hello World";
         recetores.add(receiver1);
         recetores.add(receiver2);
         request reque = new request(message, send, recetores);
         return gson.toJson(reque);
         */

        /* try {
         String uri = "http://46.101.14.39/localization/Aveiro";
         URL url = new URL(uri);
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         conn.setRequestMethod("GET");
         conn.setRequestProperty("Accept", "application/json");

         if (conn.getResponseCode() != 200) {
         throw new RuntimeException("Failed : HTTP error code : "
         + conn.getResponseCode());
         }

         BufferedReader br = new BufferedReader(new InputStreamReader(
         (conn.getInputStream())));

         String output;
         StringBuilder test = new StringBuilder();

         System.out.println("Output from Server .... \n");
         while ((output = br.readLine()) != null) {
         test.append(output);
         }
         conn.disconnect();

         return test.toString();
         } catch (MalformedURLException ex) {
         Logger.getLogger(SendingOperations.class.getName()).log(Level.SEVERE, null, ex);
         } catch (IOException ex) {
         Logger.getLogger(SendingOperations.class.getName()).log(Level.SEVERE, null, ex);
         }*/
        return "SMS API RUNNING!!!!";
    }

    @Path("{senderAddress}/subscriptions")
    @PUT
    public void notifications(@PathParam("senderAddress") String senderAddress) {

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
    @GET
    @Produces("application/json")
    public String getInfo(@PathParam("senderAddress") String senderAddress, @PathParam("requestId") String requestId) {

        Gson gson = new Gson();
        HttpStatus status = new HttpStatus();
        HttpCodes codes = new HttpCodes();
        Database db = new Database();
        String checkRequest = "SELECT * FROM request WHERE senderAddress='" + senderAddress + "' AND requestid='" + requestId + "';";
        ResultSet rs = db.executeQuery(checkRequest);

        try {
            if (!rs.next()) {
                status.setCode(400);
                status.setDescription(codes.code_400);
                return gson.toJson(status);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SendingOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        String getstates = "SELECT * FROM receivers INNER JOIN state ON receivers.stateid=state.stateid WHERE requestid='" + requestId + "';";
        ResultSet rs1 = db.executeQuery(getstates);
        List<receivers> receivers = new ArrayList<receivers>();
        try {
 
            while(rs1.next())
            {
                
                state stat = new state(rs1.getInt("stateid"), rs1.getString("description"));
                receiver rec = new receiver(rs1.getInt("receiverAddress"));
                receivers receiver = new receivers(rec,stat);
                receivers.add(receiver);
          
            }
        } catch (SQLException ex) {
            Logger.getLogger(SendingOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        requestDeliveryInfo infos = new requestDeliveryInfo(receivers);
        return gson.toJson(infos);
    }
}
