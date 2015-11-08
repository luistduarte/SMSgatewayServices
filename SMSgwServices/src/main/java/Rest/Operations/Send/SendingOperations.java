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
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    public String sendSMS(@PathParam("senderAddress") int senderAddress, String content) throws MalformedURLException, IOException {
        Gson gson = new Gson();
        Database db = new Database();
        request req = gson.fromJson(content, request.class);
        StringBuilder fromserver = new StringBuilder();

        String regex = null;
        boolean flag = true;
        String last = null;
        try {
            // inserir request na bd
            String putRequest = "INSERT INTO request (senderAddress, body, stateid) VALUES (" + senderAddress + ",?,1);";
            int insert = db.executeUpdate(putRequest, req.getBody());
            if (insert == 0) {
                Response.ResponseBuilder builder = Response.status(Response.Status.NOT_ACCEPTABLE);
                builder.header("Access-Control-Allow-Origin", "*");
                Response response = builder.build();
                throw new WebApplicationException(response);
            }
            //verificar requestid
            String getRequestID = "SELECT * FROM request ORDER BY requestid DESC";
            ResultSet rsReq = db.executeQuery(getRequestID);
            rsReq.next();
            last = rsReq.getString("requestid");

            //verificar se ha servicos que fazem match da regex com o body da mensagem
            String getRules = "SELECT * FROM rule;";
            ResultSet rs1 = db.executeQuery(getRules);
            String test = req.getBody();
            String tmp = null;
            while (rs1.next() || flag) {
                tmp = rs1.getString("regex");
                if (req.getBody().matches(rs1.getString("regex"))) {
                    flag = false;
                    regex = rs1.getString("regex");
                }
            }

            // ir checkar qual o service url
            String getURL = "SELECT * FROM rule INNER JOIN service ON service.serviceid=rule.serviceid WHERE regex = ? ;";
            ResultSet rs2 = db.executeQuery(getURL, regex);
            rs2.next();
            String serviceURL = rs2.getString("serviceurl");
            req.setSenderAddress(senderAddress);
            req.setStateid(1);
            req.setRequestid(Integer.parseInt(last));

            URL url = new URL(serviceURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            OutputStream os = conn.getOutputStream();
            os.write(gson.toJson(req).getBytes());
            os.flush();

            /*
             BufferedReader br = new BufferedReader(new InputStreamReader(
             (conn.getInputStream())));

             String output;
             System.out.println("Output from Server .... \n");
             while ((output = br.readLine()) != null) {
             fromserver.append(output);
             }*/
            conn.disconnect();

        } catch (SQLException ex) {
            Logger.getLogger(SendingOperations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(SendingOperations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ProtocolException ex) {
            Logger.getLogger(SendingOperations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SendingOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        requestStatus reqStatus = new requestStatus(Integer.parseInt(last), senderAddress, "MessageWaiting");
        return gson.toJson(reqStatus);

    }

    @Path("{requestid}/response")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public String sendResponse(@PathParam("requestid") int requestid, String content) {
        Gson gson = new Gson();
        Database db = new Database();
        response resp = gson.fromJson(content, response.class); 
        String query = "SELECT * FROM request WHERE requestid='" + requestid + "';";
        ResultSet rs = db.executeQuery(query);
        try {
            if (rs.next()) {
                
                int number = rs.getInt("senderAddress");      
                //TODO: envia SMS PARA USER
                
                
                //update status da sms
                String updateQuery = "UPDATE request SET stateid = 3 WHERE requestid='" + requestid + "';";
                db.executeUpdate(updateQuery);
                
                Response.ResponseBuilder builder = Response.status(Response.Status.OK);
                builder.header("Access-Control-Allow-Origin", "*");
                Response response = builder.build();
                throw new WebApplicationException(response);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SendingOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        Response.ResponseBuilder builder = Response.status(Response.Status.NOT_ACCEPTABLE);
        builder.header("Access-Control-Allow-Origin", "*");
        Response response = builder.build();
        throw new WebApplicationException(response);
    }

    /*@Path("{senderAddress}/requests/{requestId}/status")
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
     }*/
    @Path("{senderAddress}/requests/{requestId}/status")
    @GET
    @Produces("application/json")
    public String getInfo(@PathParam("senderAddress") int senderAddress, @PathParam("requestId") int requestId) throws SQLException {
        Gson gson = new Gson();
        HttpStatus status = new HttpStatus();
        HttpCodes codes = new HttpCodes();
        Database db = new Database();
        String checkRequest = "SELECT * FROM request WHERE senderAddress='" + senderAddress + "' AND requestid='" + requestId + "';";
        ResultSet rs = db.executeQuery(checkRequest);
        try {
            if (!(rs.next())) {
                status.setCode(400);
                status.setDescription(codes.code_400);
                return gson.toJson(status);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SendingOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        int state = rs.getInt("stateid");
        String getDescription = "SELECT * FROM state WHERE stateid = " + state + ";";

        ResultSet rs1 = db.executeQuery(getDescription);
        String description = null;
        try {
            if (rs1.next()) {
                description = rs1.getString("description");

            }
        } catch (SQLException ex) {
            Logger.getLogger(SendingOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        requestStatus reqStatus = new requestStatus(requestId, senderAddress, description);
        return gson.toJson(reqStatus);
    }
}
