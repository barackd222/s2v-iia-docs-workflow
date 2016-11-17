/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.oracle.samples.docsrouting.impl;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
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
@Path("download")
public class DownloadResource {

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

    private Response doGetResponse(String url) {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(url);

        Invocation.Builder invocationBuilder = webTarget.request();
        invocationBuilder.header("Authorization", auth);
        return invocationBuilder.get(Response.class);
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
     * Creates a new instance of DownloadResource
     */
    public DownloadResource() {
    }

    /**
     * Retrieves representation of an instance of org.oracle.samples.docsrouting.impl.DownloadResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getDownload() {
        try {
            ClientConfig config = new ClientConfig();
            Client client = ClientBuilder.newClient(config);
            
            String fid = context.getQueryParameters().getFirst("fid");
            String out1 = doGet(host+"/folders/"+fid+"/items");
            System.out.println(out1);
            ReadContext ctx1 = JsonPath.parse(out1);
            String did = ctx1.read("$.items[0].id");
            System.out.println(did);
            
            return doGetResponse(host+"/files/"+did+"/data");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
