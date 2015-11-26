package Rest.Operations.Send;

import Models.request;
import Models.response;
import Utils.Database;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * REST SMS API WebServices
 *
 * @author Luis Duarte
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

    /**
     * Just one get For Test API
     * 
     * @return String SMS API RUNNING!!!!
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        return "SMS API RUNNING!!!!";
    }

    /**
     * Service for enable or disable notifications
     *
     * @param senderAddress request sent from..
     * @return String with status of request, or Notifications Enabled,
     * Notifications Disabled or Number Never Used
     */
    @Path("{senderAddress}/subscriptions")
    @GET
    public String notifications(@PathParam("senderAddress") String senderAddress) {
        String result = null;
        try {
            //check if senderAddress exist, if not return number never used, if exist enable or disable notifications
            Database db = new Database();
            String query = "SELECT * FROM sender WHERE senderAddress=?;";
            ResultSet rs = db.executeQuery(query, senderAddress);
            boolean notifications = false;

            try {
                if (rs.next()) {
                    notifications = rs.getBoolean("notifications");
                    String updateQuery = "UPDATE sender SET notifications = " + !notifications + " WHERE senderAddress=?;";
                    db.executeUpdate(updateQuery, senderAddress);
                    if (notifications) {
                        result = "Notifications disabled";
                    } else {
                        result = "Notifications enabled";
                    }
                } else {
                    result = "Number Never Used";
                }
            } catch (SQLException ex) {
                Logger.getLogger(SendingOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            db.close();

            //send sms with result of request for enable or disable notifications
            String toSendSMS = "http://localhost:15013/cgi-bin/sendsms?username=kannel&password=kannel&to="
                    + senderAddress.substring(4) + "&text=" + result;

            URL url = new URL(toSendSMS.replaceAll("\\s+", "%20"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 202) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }
            conn.disconnect();

        } catch (MalformedURLException ex) {
            Logger.getLogger(SendingOperations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SendingOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /**
     * Service for receive message from Kannel, receive senderAddress and also text Message as parameter name text 
     * 
     * @param senderAddress request set from
     * @return String MessageDelivered for senderAddress
     */
    @Path("{senderAddress}/requests")
    @GET
    public String sendSMS(@PathParam("senderAddress") String senderAddress)  {
        MultivaluedMap<String, String> m = context.getQueryParameters();
        String text = m.get("text").get(0);
        Gson gson = new Gson();
        Database db = new Database();
        StringBuilder fromserver = new StringBuilder();
        System.out.println("New Request --- " + " sender: " + senderAddress + "   text:" + text);

        String regex = null;
        boolean flag = true;
        String last = null;
        try {
            //check if sender exist
            String checkRule = "SELECT * FROM sender WHERE senderAddress = ?";
            ResultSet check = db.executeQuery(checkRule, senderAddress);

            //if not add a new one
            if (!(check.next())) {
                String putUser = "INSERT INTO sender (senderAddress,username,notifications) VALUES(?,?,?)";
                db.executeUpdate(putUser, senderAddress, "unknown", "0");
            }
            // insert request message on database
            String putRequest = "INSERT INTO request (senderAddress, body, stateid) VALUES (?,?,1);";
            int insert = db.executeUpdate(putRequest, senderAddress, text);
            if (insert == 0) {
                Response.ResponseBuilder builder = Response.status(Response.Status.NOT_ACCEPTABLE);
                builder.header("Access-Control-Allow-Origin", "*");
                Response response = builder.build();
                throw new WebApplicationException(response);
            }

            //get last RequestID
            String getRequestID = "SELECT * FROM request ORDER BY requestid DESC";
            ResultSet rsReq = db.executeQuery(getRequestID);
            rsReq.next();
            last = rsReq.getString("requestid");

            //check which service has this regex rule
            String getRules = "SELECT * FROM rule;";
            ResultSet rs1 = db.executeQuery(getRules);
            String tmp = null;
            while (rs1.next() || flag) {
                tmp = rs1.getString("regex");
                if (text.matches(rs1.getString("regex"))) {
                    flag = false;
                    regex = rs1.getString("regex");
                }
            }

            // get service url
            String getURL = "SELECT * FROM rule INNER JOIN service ON service.serviceid=rule.serviceid WHERE regex = ? ;";
            ResultSet rs2 = db.executeQuery(getURL, regex);
            rs2.next();
            String serviceURL = rs2.getString("serviceurl");
            request req = new request(text, senderAddress, Integer.parseInt(last), 1);

            //call a http request POST with ServiceURL, and send a Json with body, senderAddress, requestID and stateid
            System.out.println("Message sent: " + gson.toJson(req) + "\n TO: " + serviceURL);
            URL url = new URL(serviceURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(gson.toJson(req).getBytes());
            os.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                fromserver.append(output);
            }
            String updateQuery = "UPDATE request SET stateid = 2 WHERE requestid='" + Integer.parseInt(last) + "';";
            db.executeUpdate(updateQuery);
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
        return "MessageDelivered";
    }

    /**
     * Service for Composer deliver response for a request from Client, parameter content is a Json composed by response(body field) and status of request, Sender receives notifications if they are enabled
     * 
     * @param requestid  requestid
     * @param content  json composed by response(body field) and status of request
     * @return 
     */
    @Path("{requestid}/response")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public String sendResponse(@PathParam("requestid") int requestid, String content) {
        Gson gson = new Gson();
        Database db = new Database();
        response resp = gson.fromJson(content, response.class);
        System.out.println("NewResonse:  req=" + requestid + "\n content:" + content);
        //check if requestID exist 
        String query = "SELECT * FROM request WHERE requestid='" + requestid + "';";
        ResultSet rs = db.executeQuery(query);
        try {
            if (rs.next()) {
                //get senderAddress
                String number = rs.getString("senderAddress");
                String toSendSMS = null;

                //check if Notifications are disabled or enable for this Address
                String getNotif = "SELECT * FROM sender WHERE senderAddress=?;";
                ResultSet rsNotif = db.executeQuery(getNotif, number);
                boolean notifications = false;
                if (rsNotif.next()) {
                    notifications = rsNotif.getBoolean("notifications");
                }
                int state = 4;
                if (resp.getStatus() == 200) {
                    state = 3;
                }

                //if notifications enable send sms For user with content Message Accepted or Not Accepted, with help of status code
                if (notifications) {
                    if (resp.getStatus() == 200) {

                        toSendSMS = "http://localhost:15013/cgi-bin/sendsms?username=kannel&password=kannel&to="
                                + number.substring(4) + "&text=Message%20Accepted";
                    } else {
                        toSendSMS = "http://localhost:15013/cgi-bin/sendsms?username=kannel&password=kannel&to="
                                + number.substring(4) + "&text=Message%20Not%20Accepted";
                    }
                    URL url = new URL(toSendSMS);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");
                    if (conn.getResponseCode() != 202) {
                        throw new RuntimeException("Failed : HTTP error code : "
                                + conn.getResponseCode());
                    }
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            (conn.getInputStream())));
                    String output;
                    System.out.println("Output from Server .... \n");
                    while ((output = br.readLine()) != null) {
                        System.out.println(output);
                    }
                    conn.disconnect();
                }
                //update status da sms
                String updateQuery = "UPDATE request SET stateid = " + state + " WHERE requestid=" + requestid + ";";
                db.executeUpdate(updateQuery);

                //Send Message for User with response of service
                toSendSMS = "http://localhost:15013/cgi-bin/sendsms?username=kannel&password=kannel&to="
                        + number.substring(4) + "&text=" + resp.getBody();
                URL url = new URL(toSendSMS.replaceAll("\\s+", "%20"));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                if (conn.getResponseCode() != 202) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + conn.getResponseCode());
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));
                String output;
                System.out.println("Output from Server .... \n");
                while ((output = br.readLine()) != null) {
                    System.out.println(output);
                }
                conn.disconnect();

                Response.ResponseBuilder builder = Response.status(Response.Status.OK);
                builder.header("Access-Control-Allow-Origin", "*");
                Response response = builder.build();
                throw new WebApplicationException(response);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SendingOperations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SendingOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        Response.ResponseBuilder builder = Response.status(Response.Status.NOT_ACCEPTABLE);
        builder.header("Access-Control-Allow-Origin", "*");
        Response response = builder.build();
        throw new WebApplicationException(response);
    }

    /**
     * Service where client can check status of their last request
     * 
     * @param senderAddress message sent from
     * @param requestId at this case requestId is a string called last when request comes from kannel, requestId can be a int too
     * @return status of last request
     */
    @Path("{senderAddress}/requests/{requestId}/status")
    @GET
    public String getInfo(@PathParam("senderAddress") String senderAddress, @PathParam("requestId") String requestId) {
        String description = null;
        try {
            Database db = new Database();
            int intReqID;

            //requestID sended can be the "last"  or a number, if its the "last" word we need to get the real requestID
            if (requestId.equals("last")) {
                String getRequestID = "SELECT * FROM request WHERE senderAddress = ? ORDER BY requestid DESC";
                ResultSet rsReq = db.executeQuery(getRequestID, senderAddress);
                rsReq.next();
                intReqID = Integer.parseInt(rsReq.getString("requestid"));
            } else {
                intReqID = Integer.parseInt(requestId);
            }

            //check if requestID exist, if not return status = NOT ACCEPTABLE
            String checkRequest = "SELECT * FROM request WHERE senderAddress=? AND requestid='" + intReqID + "';";
            ResultSet rs = db.executeQuery(checkRequest, senderAddress);
            try {
                if (!(rs.next())) {
                    Response.ResponseBuilder builder = Response.status(Response.Status.NOT_ACCEPTABLE);
                    builder.header("Access-Control-Allow-Origin", "*");
                    Response response = builder.build();
                    throw new WebApplicationException(response);
                }
            } catch (SQLException ex) {
                Logger.getLogger(SendingOperations.class.getName()).log(Level.SEVERE, null, ex);
            }

            //get Description of message state
            int state = rs.getInt("stateid");
            String getDescription = "SELECT * FROM state WHERE stateid = " + state + ";";
            ResultSet rs1 = db.executeQuery(getDescription);

            try {
                if (rs1.next()) {
                    description = rs1.getString("description");
                }
            } catch (SQLException ex) {
                Logger.getLogger(SendingOperations.class.getName()).log(Level.SEVERE, null, ex);
            }

            //send SMS to the requestNumber with state description
            String toSendSMS = "http://localhost:15013/cgi-bin/sendsms?username=kannel&password=kannel&to="
                    + senderAddress.substring(4) + "&text=" + description;
            URL url = new URL(toSendSMS.replaceAll("\\s+", "%20"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            //if request not acceptable throw exception
            if (conn.getResponseCode() != 202) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }
            conn.disconnect();
        } catch (MalformedURLException ex) {
            Logger.getLogger(SendingOperations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | SQLException ex) {
            Logger.getLogger(SendingOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return description;
    }
}
