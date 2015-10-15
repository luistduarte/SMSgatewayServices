/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Rest.Operations.Services;

import Models.*;
import Utils.Database;
import com.google.gson.Gson;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author root
 */
@Path("subscrive")
public class SubscriveOperations {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of SubscriveOperations
     */
    public SubscriveOperations() {
    }

    /**
     * Retrieves representation of an instance of
     * Rest.Operations.Services.SubscriveOperations
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson() {
        Gson gson = new Gson();
        service serv = new service(1, "http://cenas", "DaveComposer");
        return gson.toJson(serv);
    }

    @Path("rules")
    @GET
    @Produces("application/json")
    public String getJson2() {
        /* Gson gson = new Gson();
         service serv = new service(1, "http://cenas", "DaveComposer");
         return gson.toJson(serv);*/

        Gson gson = new Gson();
        List<rule> rules = new ArrayList<>();

        rule rule1 = new rule("JOGOS");
        rule rule2 = new rule("SSDOP");
        String url = "SUPERURL";
        rules.add(rule1);
        rules.add(rule2);
        rulestoservice rul = new rulestoservice(url, rules);
        return gson.toJson(rul);
    }

    @Path("/service")
    @PUT
    @Consumes("application/json")
    public void putservice(String content) {
        int insert = 0;
        Gson gson = new Gson();
        service serv = gson.fromJson(content, service.class);
        Database db = new Database();
        String checkURL = "SELECT * FROM service WHERE serviceurl = ?";
        ResultSet rs = db.executeQuery(checkURL, serv.getServiceurl());
        try {
            if (rs.next()) {
                Response.ResponseBuilder builder = Response.status(Response.Status.CONFLICT);
                Response response = builder.build();
                throw new WebApplicationException(response);
            } else {

                String putService = "INSERT INTO service (name,serviceurl) VALUES(?,?)";
                insert = db.executeUpdate(putService, serv.getName(), serv.getServiceurl());
                if (insert == 0) {
                    Response.ResponseBuilder builder = Response.status(Response.Status.NOT_ACCEPTABLE);
                    Response response = builder.build();
                    throw new WebApplicationException(response);
                } else {
                    Response.ResponseBuilder builder = Response.status(Response.Status.OK);
                    Response response = builder.build();
                    throw new WebApplicationException(response);
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(SubscriveOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Path("/service/rule")
    @PUT
    @Consumes("application/json")
    public void putrules(String content) {

        Gson gson = new Gson();
        rulestoservice newrules = gson.fromJson(content, rulestoservice.class);
        Database db = new Database();
        String checkURL = "SELECT * FROM service WHERE serviceurl = ?";
        ResultSet rs = db.executeQuery(checkURL, newrules.getUrl());
        int i;
        try {
            if (rs.next()) {
                for (i = 0; i < newrules.getRules().size(); i++) {
                    rule now = newrules.getRules().get(i);
                    String checkRule = "SELECT * FROM rule INNER JOIN service ON service.serviceid=rule.serviceid WHERE serviceurl = ? and regex = ?";
                    ResultSet check = db.executeQuery(checkRule, newrules.getUrl(), now.getRegex());

                    if (!(check.next())) {
                        String putRule = "INSERT INTO rule (serviceid,regex) VALUES(?,?)";
                        db.executeUpdate(putRule, rs.getString("serviceid"), now.getRegex());
                    }
                }
                Response.ResponseBuilder builder = Response.status(Response.Status.OK);
                Response response = builder.build();
                throw new WebApplicationException(response);

            } else {
                Response.ResponseBuilder builder = Response.status(Response.Status.NOT_ACCEPTABLE);
                Response response = builder.build();
                throw new WebApplicationException(response);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SubscriveOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Path("/service/check")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public String checkRules(String content) {

        try {
            Gson gson = new Gson();
            url urltoCheck = gson.fromJson(content, url.class);
            Database db = new Database();
            rulestoservice urlRules;
            List<rule> rules = new ArrayList<>();
            String checkURL = "SELECT * FROM rule INNER JOIN service ON service.serviceid=rule.serviceid WHERE serviceurl = ?";
            int i = 0;
            ResultSet check = db.executeQuery(checkURL, urltoCheck.geturl());
            while (check.next()) {
                rule rule1 = new rule(check.getString("regex"));
                rules.add(rule1);
                i++;
            }
            if (i == 0) {
                Response.ResponseBuilder builder = Response.status(Response.Status.NO_CONTENT);
                Response response = builder.build();
                throw new WebApplicationException(response);
            } else {
                urlRules = new rulestoservice(urltoCheck.geturl(), rules);
                return gson.toJson(urlRules);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SubscriveOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        Response.ResponseBuilder builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
        Response response = builder.build();
        throw new WebApplicationException(response);
    }

}
