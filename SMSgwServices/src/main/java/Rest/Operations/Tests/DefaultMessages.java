/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Rest.Operations.Tests;

import External.Models.info;
import External.Models.menu;
import External.Models.updatemenu;
import Models.receiver;
import Models.request;
import Models.sender;
import Models.state;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
        List<receiver> recetores = new ArrayList<>();
        sender send = new sender(916056618, "luisduarte");
        state sta = new state(1, "pending");
        receiver receiver1 = new receiver(123456788);
        receiver receiver2 = new receiver(123456777);
        String message = "Hello World";
        recetores.add(receiver1);
        recetores.add(receiver2);
        request reque = new request(message, send, recetores);
        return gson.toJson(reque);
    }

    @Path("update")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getDefaultUpdate() {
        Gson gson = new Gson();

        info inf = new info("username");

        List<menu> menu = new ArrayList<>();

        menu m1 = new menu("peixe", 12.0, 20);
        menu m2 = new menu("frango", 13.5, 25);
        menu.add(m1);
        menu.add(m2);
        updatemenu managemenu = new updatemenu(inf, menu);

        return gson.toJson(managemenu);
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
