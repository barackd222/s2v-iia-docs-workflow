/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.oracle.samples;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.minidev.json.JSONArray;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.oracle.samples.docsrouting.pojo.Catalog;

/**
 *
 * @author Jason
 */
public class DoCSRSTest {

    final static private String host = "https://oradocs-corp.documents.us2.oraclecloud.com/documents/api/1.1";
    final static private String auth = "Basic amFzb24ubG93ZUBvcmFjbGUuY29tOlRAdVNwM2Fr";
    
    public DoCSRSTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
    // @Test
    public void testCatalog() {
        try {
            Client client = ClientBuilder.newClient();

            WebTarget webTarget = client.target(host+"/catalog");

            Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
            invocationBuilder.header("Authorization", auth);

            Response response = invocationBuilder.get();
            String out = response.readEntity(String.class);
            System.out.println(response.getStatus());
            System.out.println(out);

            //create ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            //convert json string to object using TreeNode traversal
            JsonNode node = objectMapper.readTree(out);
            node = node.findPath("items");
            node = node.get(0);
            Map.Entry<String,JsonNode>ee = node.fields().next();
            System.out.println(ee.getKey());
            System.out.println(ee.getValue().asText());
        
            Catalog cat = objectMapper.readValue(out,Catalog.class);
            System.out.println(cat.getItems().length);
            
            ReadContext ctx = JsonPath.parse(out);
            List<String> list = ctx.read("$.items[*].name");
            System.out.println(list);
        } catch (Exception ex) {
            ex.printStackTrace();
        }     
    }

    // @Test
    public void testUsers() {
        try {
            Client client = ClientBuilder.newClient();

            WebTarget webTarget = client.target(host+"/users/items");
            webTarget = webTarget.queryParam("query","tim.edwards");

            Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
            invocationBuilder.header("Authorization", auth);

            Response response = invocationBuilder.get();
            String out = response.readEntity(String.class);
            System.out.println(response.getStatus());
            System.out.println(out);

            //create ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            //convert json string to object using TreeNode traversal
            JsonNode node = objectMapper.readTree(out);
            node = node.findPath("items");
            node = node.get(0);
            Map.Entry<String,JsonNode>ee = node.fields().next();
            System.out.println(ee.getKey());
            System.out.println(ee.getValue().asText());
        
            ReadContext ctx = JsonPath.parse(out);
            String list = ctx.read("$.items[0].id");
            System.out.println(list);
        } catch (Exception ex) {
            ex.printStackTrace();
        }     
    }

    // @Test
    public void testUpload() {
        try {
            ClientConfig config = new ClientConfig();
            config.register(MultiPartFeature.class);
            Client client = ClientBuilder.newClient(config);

            WebTarget webTarget = client.target(host+"/files/data");

            Invocation.Builder invocationBuilder = webTarget.request();
            invocationBuilder.header("Authorization", auth);

            MultiPart mp = new MultiPart(MediaType.MULTIPART_FORM_DATA_TYPE);
            FormDataBodyPart bp1 = new FormDataBodyPart("jsonInputParameters", "{ \"parentID\": \"self\" }", MediaType.APPLICATION_JSON_TYPE);
            System.out.println(bp1.getContentDisposition().toString());
            System.out.println(bp1.getHeaders());
            System.out.println(bp1.getEntity());
            mp.bodyPart(bp1);
            FileDataBodyPart bp2 = new FileDataBodyPart("primaryFile",new File("C:/Users/Jason/Downloads/SOA 12c Upgrade - Latest.pptx"));
            System.out.println(bp2.getContentDisposition().toString());
            System.out.println(bp2.getMediaType());
            System.out.println(bp2.getHeaders());
            mp.bodyPart(bp2);
                /*
                POST https://oradocs-corp.documents.us2.oraclecloud.com/documents/api/1.1/files/data HTTP/1.1
Accept-Encoding: gzip,deflate
Content-Type: multipart/form-data; boundary=1234567890
Authorization: Basic XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
Content-Length: 370
Host: oradocs-corp.documents.us2.oraclecloud.com
Connection: Keep-Alive
User-Agent: Apache-HttpClient/4.1.1 (java 1.5)

--1234567890
Content-Disposition: form-data; name="jsonInputParameters"

{
"parentID": "self"
}
--1234567890
Content-Disposition: form-data; name="primaryFile"; filename="helloworld.gif"
Content-Type: image/gif
Content-Transfer-Encoding: base64

R0lGODlhCgAKAIAAAP8AAAAAACH5BAAAAAAALAAAAAAKAAoAAAIIhI+py+0PYysAOw==
--1234567890--
                */
            Entity request = Entity.entity(mp,mp.getMediaType());
            Response response = invocationBuilder.post(request,Response.class);
            String out = response.readEntity(String.class);
            System.out.println(response.getStatus());
            System.out.println(out);
        } catch (Exception ex) {
            ex.printStackTrace();
        }     
    }

    // @Test
    public void testDownload() {
        try {
            ClientConfig config = new ClientConfig();
            Client client = ClientBuilder.newClient(config);

            WebTarget webTarget = client.target(host+"/files/D0606BDE21E67D5A74083B30F6C3FF17C1177A968060/data");

            Invocation.Builder invocationBuilder = webTarget.request();
            invocationBuilder.header("Authorization", auth);

            Response response = invocationBuilder.get(Response.class);
            InputStream is = (InputStream)response.getEntity();
            System.out.println(response.getStatus());
            FileOutputStream fos = new FileOutputStream(new File("C:/Users/Jason/Downloads/SOA 12c Upgrade - Latest - From DoCS.pptx"));
            int bb = 0;
            while ((bb = is.read()) != -1) {
                fos.write(bb);
            }
            fos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }     
    }

    @Test
    public void testShare() {
        String in = "{\"userID\": \"UBEE0A148D6FDDB9A18F3A33F6C3FF17C117\", \"role\": \"downloader\", \"message\": \"granting you shared access to this folder\" }";
        Entity request = Entity.entity(in, MediaType.TEXT_PLAIN_TYPE);
        String out = doPost(host+"/shares/FCDEB7261DFB64AA276C18B9F6C3FF17C1177A968060",request);
        System.out.println(out);
    }

    private String doGet(String url) {
        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);

        WebTarget webTarget = client.target(url);

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
    
    @Test
    public void testFolders() {
        try {
            String out1 = doGet(host+"/folders/items");
            ReadContext ctx1 = JsonPath.parse(out1);
            String fid = (String)((JSONArray)ctx1.read("$.items[?(@.name=='jason.lowe')].id")).get(0);
            System.out.println(fid);
            String out2 = doGet(host+"/folders/"+fid+"/items");
            ReadContext ctx2 = JsonPath.parse(out2);
            for (Iterator ii = ((JSONArray)ctx2.read("$.items[*].name")).iterator(); ii.hasNext(); ) {
                String fname = (String)ii.next();
                System.out.println(fname);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }     
    }

}
