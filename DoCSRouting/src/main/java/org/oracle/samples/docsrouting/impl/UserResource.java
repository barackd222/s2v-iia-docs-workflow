package org.oracle.samples.docsrouting.impl;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.minidev.json.JSONArray;
import org.glassfish.jersey.client.ClientConfig;

/**
 * REST Web Service
 *
 * @author jlowe
 */
@Path("user")
public class UserResource {

    static private String host = "https://hostname/documents/api/1.1";
    static private String auth = "Basic XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
    
    static {
        Properties p = new Properties();
        try {
            // Glassfish has this in the same classes directory as FolderResource (under the source directory)
            // InputStream is = FolderResource.class.getResourceAsStream("config.properties");
            // Tomcat has this in the same classes directory as FolderResource
            // Tomcat requires it to be an absolute path to the resource
            InputStream is = FolderResource.class.getResourceAsStream("/org/oracle/samples/docsrouting/resources/config.properties");
            System.out.println(is);
            p.load(is);
            host = p.getProperty("host");
            auth = p.getProperty("auth");
            System.out.println(host);
            System.out.println(auth);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of RouteResource
     */
    public UserResource() {
    }

    private String doGet(String url) {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(url);
        return doGet(webTarget);
    }
    
    private String doGet(WebTarget webTarget) {
        Invocation.Builder invocationBuilder = webTarget.request();
        invocationBuilder.header("Authorization", auth);

        Response response = invocationBuilder.get(Response.class);
        String out = response.readEntity(String.class);
        System.out.println(response.getStatus());
        return out;
    }
    
    /**
     * Retrieves representation of an instance of org.oracle.samples.docsrouting.pojo.UserResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getUsers() {
//        String content = "{ \"hello\": \"world\" }";
//        return content;
        try {
            String param = context.getQueryParameters().getFirst("query");
            Client client = ClientBuilder.newClient();

            WebTarget webTarget = client.target(host+"/users/items");
            webTarget = webTarget.queryParam("query",param);

            return doGet(webTarget);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

}
