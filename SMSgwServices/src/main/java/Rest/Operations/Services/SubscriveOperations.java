package Rest.Operations.Services;

import Models.rule;
import Models.rulestoservice;
import Models.service;
import Models.url;
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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * REST SMS API WebServices
 *
 * @author Luis Duarte
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
     * One Composer can register their service, for that need to give their service Url, and their name
     * 
     * @param content json composed by serviceurl and name
     */
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
                builder.header("Access-Control-Allow-Origin", "*");
                Response response = builder.build();
                throw new WebApplicationException(response);
            } else {

                String putService = "INSERT INTO service (name,serviceurl) VALUES(?,?)";
                insert = db.executeUpdate(putService, serv.getName(), serv.getServiceurl());
                if (insert == 0) {
                    Response.ResponseBuilder builder = Response.status(Response.Status.NOT_ACCEPTABLE);
                    builder.header("Access-Control-Allow-Origin", "*");
                    Response response = builder.build();
                    throw new WebApplicationException(response);
                } else {
                    Response.ResponseBuilder builder = Response.status(Response.Status.OK);
                    builder.header("Access-Control-Allow-Origin", "*");
                    Response response = builder.build();
                    throw new WebApplicationException(response);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(SubscriveOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * One Composer can add regex rules for our service forward messages for their service, for that need to give url of their service and list of rules
     * 
     * @param content json composed by url and list of rules
     */
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
                builder.header("Access-Control-Allow-Origin", "*");
                Response response = builder.build();
                throw new WebApplicationException(response);

            } else {
                Response.ResponseBuilder builder = Response.status(Response.Status.NOT_ACCEPTABLE);
                builder.header("Access-Control-Allow-Origin", "*");
                Response response = builder.build();
                throw new WebApplicationException(response);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SubscriveOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Service just for check rules for one composer url
     * 
     * @param content json with just one field (url)
     * @return List of rules for selected service
     */
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
                builder.header("Access-Control-Allow-Origin", "*");
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
        builder.header("Access-Control-Allow-Origin", "*");
        Response response = builder.build();
        throw new WebApplicationException(response);
    }
}
