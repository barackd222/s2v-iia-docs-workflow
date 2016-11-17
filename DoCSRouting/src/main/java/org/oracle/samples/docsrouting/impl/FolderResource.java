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
import java.io.File;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.minidev.json.JSONArray;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;

/**
 * REST Web Service
 *
 * @author jlowe
 */
@Path("folder")
public class FolderResource {

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
    public FolderResource() {
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
    
    private String doPost(String url, Entity entity) {
        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);

        WebTarget webTarget = client.target(url);

        Invocation.Builder invocationBuilder = webTarget.request();
        invocationBuilder.header("Authorization", auth);

        Response response = invocationBuilder.post(entity,Response.class);
        String out = response.readEntity(String.class);
        System.out.println(response.getStatus());
        return out;
    }
    
    private String doPost(Client client, String url, Entity entity) {
        WebTarget webTarget = client.target(url);

        Invocation.Builder invocationBuilder = webTarget.request();
        invocationBuilder.header("Authorization", auth);

        Response response = invocationBuilder.post(entity,Response.class);
        String out = response.readEntity(String.class);
        System.out.println(response.getStatus());
        return out;
    }
    
    /**
     * Retrieves representation of an instance of org.oracle.samples.docsrouting.pojo.RouteResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getFolders() {
//        String content = "{ \"hello\": \"world\" }";
//        return content;
        try {
            String param = context.getQueryParameters().getFirst("name");
            Client client = ClientBuilder.newClient();

            String out1 = doGet(host+"/folders/items");
            ReadContext ctx1 = JsonPath.parse(out1);
            System.out.println(out1);
            String fid = (String)((JSONArray)ctx1.read("$.items[?(@.name=='"+param+"')].id")).get(0);
            System.out.println(fid);

            String out2 = doGet(host+"/folders/"+fid+"/items");
            return out2;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    /**
     * PUT method for updating or creating an instance of RouteResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
//        String content = "{ \"hello\": \"world\" }";
//        return content;
        try {
            String param = context.getQueryParameters().getFirst("name");
            String message = context.getQueryParameters().getFirst("message");
            Client client = ClientBuilder.newClient();

            String out1 = doGet(host+"/folders/items");
            ReadContext ctx1 = JsonPath.parse(out1);
            String fid = (String)((JSONArray)ctx1.read("$.items[?(@.name=='"+param+"')].id")).get(0);
            System.out.println(fid);

            String in2 = "{ \"name\": \""+System.currentTimeMillis()+"\" }";           
            Entity request2 = Entity.entity(in2, MediaType.TEXT_PLAIN_TYPE);
            String out2 = doPost(host+"/folders/"+fid,request2);
            ReadContext ctx2 = JsonPath.parse(out2);
            String nfid = ctx2.read("$.id");
            
            WebTarget webTarget = client.target(host+"/users/items");
            webTarget = webTarget.queryParam("query",param);
            String out3 = doGet(webTarget);
            ReadContext ctx3 = JsonPath.parse(out3);
            String uid = ctx3.read("$.items[0].id");
            
            String in4 = "{\"userID\": \""+uid+"\", \"role\": \"manager\", \"message\": \""+message+"\" }";
            Entity request4 = Entity.entity(in4, MediaType.TEXT_PLAIN_TYPE);
            String out4 = doPost(host+"/shares/"+nfid,request4);
            System.out.println(out4);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * POST method for updating or creating an instance of RouteResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void postJson(@FormDataParam("to") String to, @FormDataParam("title") String title, @FormDataParam("message") String message, @FormDataParam("primaryFile") FormDataContentDisposition fileInfo, @FormDataParam("primaryFile") InputStream fileInputStream) {
         /* @FormParam("jsonInputParameters") String jsonInputParameters, @FormParam("primaryFile") InputStream fileInputStream, @FormParam("primaryFile") FormDataContentDisposition contentDispositionHeader */
//        String content = "{ \"hello\": \"world\" }";
//        return content;
        try {
            ClientConfig config = new ClientConfig();
            config.register(MultiPartFeature.class);
            Client client = ClientBuilder.newClient(config);

            String out1 = doGet(host+"/folders/items");
            ReadContext ctx1 = JsonPath.parse(out1);
            String fid = (String)((JSONArray)ctx1.read("$.items[?(@.name=='"+to+"')].id")).get(0);
            System.out.println(fid);

            String in2 = "{ \"name\": \""+title+"\" }";           
            Entity request2 = Entity.entity(in2, MediaType.TEXT_PLAIN_TYPE);
            String out2 = doPost(host+"/folders/"+fid,request2);
            ReadContext ctx2 = JsonPath.parse(out2);
            String nfid = ctx2.read("$.id");
            
            WebTarget webTarget = client.target(host+"/users/items");
            webTarget = webTarget.queryParam("query",to);
            String out3 = doGet(webTarget);
            ReadContext ctx3 = JsonPath.parse(out3);
            String uid = ctx3.read("$.items[0].id");
            
            String jsonInputParameters = "{ \"parentID\": \""+nfid+"\" }";
            MultiPart mp = new MultiPart(MediaType.MULTIPART_FORM_DATA_TYPE);
            FormDataBodyPart bp1 = new FormDataBodyPart("jsonInputParameters", jsonInputParameters, MediaType.APPLICATION_JSON_TYPE);
            mp.bodyPart(bp1);
            
            StreamDataBodyPart bp2 = new StreamDataBodyPart("primaryFile", fileInputStream, fileInfo.getFileName());
            System.out.println(bp2.getContentDisposition().toString());
            System.out.println(bp2.getMediaType());
            System.out.println(bp2.getHeaders());
            mp.bodyPart(bp2);
            
            Entity request = Entity.entity(mp,mp.getMediaType());
            String out = doPost(client,host+"/files/data",request);
            System.out.println(out);

            String in4 = "{\"userID\": \""+uid+"\", \"role\": \"manager\", \"message\": \""+message+"\" }";
            Entity request4 = Entity.entity(in4, MediaType.TEXT_PLAIN_TYPE);
            String out4 = doPost(host+"/shares/"+nfid,request4);
            System.out.println(out4);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
