/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Rest.Operations.Tests;

import Models.request;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author root
 */
@Path("default")
public class DefaultMessages {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of DefaultMessages
     */
    public DefaultMessages() {
    }

    @Path("message")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getDefaultMessage() {
        Gson gson = new Gson();
        String message = "Hello World";
        request reque = new request(message, 916056618);
        return gson.toJson(reque);
    }

    @Path("dave")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getDaveWS() {

        StringBuilder test = new StringBuilder();
        try {
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
            while ((output = br.readLine()) != null) {
                test.append(output);
            }
            conn.disconnect();
            return test.toString();
        } catch (MalformedURLException ex) {
            Logger.getLogger(DefaultMessages.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ProtocolException ex) {
            Logger.getLogger(DefaultMessages.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DefaultMessages.class.getName()).log(Level.SEVERE, null, ex);
        }
        return test.toString();
    }

    @Path("getok")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public void get200Ok() {
        Response.ResponseBuilder builder = Response.status(Response.Status.OK);
        Response response = builder.build();
        throw new WebApplicationException(response);
    }

    @Path("getunauthorized")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public void getUNAUTHORIZED() {
        Response.ResponseBuilder builder = Response.status(Response.Status.UNAUTHORIZED);
        Response response = builder.build();
        throw new WebApplicationException(response);
    }
}
