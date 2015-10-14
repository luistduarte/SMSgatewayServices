/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Rest.Operations.Send;


import Models.*;
import Utils.Database;
import Utils.HttpCodes;
import Utils.HttpStatus;
import com.google.gson.Gson;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;


/**
 * REST Web Service
 *
 * @author root
 */
@Path("outbound")
public class SendingOperations {

    @Context
    private UriInfo context;

    @Context
    private HttpServletResponse response;

    /**
     * Creates a new instance of SendingOperations
     */
    public SendingOperations() {
    }



    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        //GET#Aveiro
        //PUT:luisduarte#menu#Dish:peixe#Price:10#Doses:20#Dish:carne#Price:10#Doses:20
        //PUT:luisduarte#reservation#Provider:1#Prato:peixe#Lugares:5#Hora:12
        return "SMS API RUNNING!!!!";
    }

    @Path("{senderAddress}/subscriptions")
    @PUT
    public void notifications(@PathParam("senderAddress") int senderAddress) {
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
    public String sendSMS(@PathParam("senderAddress") int senderAddress, String content) {
        Gson gson = new Gson();
        Database db = new Database();
        request req = gson.fromJson(content, request.class);
        String message = req.getBody();
        
        
        
        return gson.toJson(req);
    }

    @Path("{senderAddress}/requests/{requestId}/deliveryInfos")
    @GET
    @Produces("application/json")
    public String getInfo(@PathParam("senderAddress") int senderAddress, @PathParam("requestId") int requestId) {
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
        List<receivers> receivers = new ArrayList<>();
        try {
            while (rs1.next()) {
                state stat = new state(rs1.getInt("stateid"), rs1.getString("description"));
                receiver rec = new receiver(rs1.getInt("receiverAddress"));
                receivers receiver = new receivers(rec, stat);
                receivers.add(receiver);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SendingOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        requestDeliveryInfo infos = new requestDeliveryInfo(requestId, receivers);
        return gson.toJson(infos);
    }
}
