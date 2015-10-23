package i5.las2peer.services.videoRecommender;

import i5.las2peer.api.Service;
import i5.las2peer.restMapper.HttpResponse;
//import i5.las2peer.restMapper.MediaType;
import i5.las2peer.restMapper.RESTMapper;
import i5.las2peer.restMapper.annotations.GET;
//import i5.las2peer.restMapper.annotations.POST;
import i5.las2peer.restMapper.annotations.Consumes;
import i5.las2peer.restMapper.annotations.Path;
import i5.las2peer.restMapper.annotations.PathParam;
import i5.las2peer.restMapper.annotations.Produces;
import i5.las2peer.restMapper.annotations.QueryParam;
import i5.las2peer.restMapper.annotations.Version;
import i5.las2peer.restMapper.annotations.swagger.ApiInfo;
import i5.las2peer.restMapper.annotations.swagger.ApiResponse;
import i5.las2peer.restMapper.annotations.swagger.ApiResponses;
import i5.las2peer.restMapper.annotations.swagger.Summary;
import i5.las2peer.restMapper.tools.ValidationResult;
import i5.las2peer.restMapper.tools.XMLCheck;
import i5.las2peer.services.videoRecommender.util.OIDC;
import i5.las2peer.services.videoRecommender.database.DatabaseManager;
import i5.las2peer.services.videoRecommender.util.LocationService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.HttpMethod;
//import org.apache.commons.httpclient.HttpStatus;
//import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import com.arangodb.entity.GraphEntity;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.MessageListener;
















//import i5.las2peer.services.videoCompiler.idGenerateClient.IdGenerateClientClass;
//import org.junit.experimental.theories.ParametersSuppliedBy;
//import com.sun.jersey.multipart.FormDataParam;
//import com.sun.jersey.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * LAS2peer Service
 * 
 * 
 * 
 * 
 */
@Path("recommendation")
@Version("0.1")
@ApiInfo(title = "Adapter Service", 
	description = "<p>A RESTful service for Adaptation for Vaptor.</p>", 
	termsOfServiceUrl = "", 
	contact = "siddiqui@dbis.rwth-aachen.de", 
	license = "MIT", 
	licenseUrl = "") 
	
public class RecommenderClass extends Service {

	private String port;
	//private String host;
	private String username;
	private String password;
	private String database;
	private String databaseServer;
	private String driverName;
	private String hostName;
	private String useUniCode;
	private String charEncoding;
	private String charSet;
	private String collation;
	private String userPreferenceService;
	private String userinfo;
	
	

	private DatabaseManager dbm;
	private String epUrl;
	
	GraphEntity graphNew;
	

	public RecommenderClass() {
		// read and set properties values
		setFieldValues();

		if (!epUrl.endsWith("/")) {
			epUrl += "/";
		}
		// instantiate a database manager to handle database connection pooling
		// and credentials
		//dbm = new DatabaseManager(username, password, host, port, database);
	}

	/*@GET
	@Path("")
	//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String getRecommendations(@QueryParam(name="username" , defaultValue = "*") 
	String username, @QueryParam(name = "query", defaultValue = "*" ) String query){

		System.out.println("username: "+username);
		dbm = new DatabaseManager();
		dbm.init(driverName, databaseServer, port, database, this.username, password, hostName);
		
		List<String> searches = dbm.getSearches();
		JSONArray recommendations = new JSONArray();
		String[] splittedQuery = query.split(" ");
		//for(int i=0; i<searches.size(); i+=2)
		int i=0;
		boolean b=false;
		//while(!searches.isEmpty() && !searches.get(i).isEmpty()){
		while(i<searches.size()){
			
			b=false;
			for(int j=0;j<splittedQuery.length;j++){
				if(searches.get(i).contains(splittedQuery[j]))
						b=true;
			}
			if(!b){
				searches.remove(i);
				searches.remove(i+1);
				searches.remove(i+2);
				
			}
			else{
				recommendations.put(searches.get(i+1));
				i+=3;
			}
				
		}
		
		
		
		return "";
	}*/
	
	@GET
	@Path("")
	//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String getInitialRecommendations(@QueryParam(name = "Authorization", defaultValue = "") String token, 
			@QueryParam(name="mobile" , defaultValue = "false") boolean mobile){

String username = null;
		
		System.out.println("TOKEN: "+token);
		
		if(token!=null){
			   token = token.replace("Bearer ","");
			   username = OIDC.verifyAccessToken(token, userinfo);
		}
		
		System.out.println("username: "+username);
		dbm = new DatabaseManager();
		dbm.init(driverName, databaseServer, port, database, this.username, password, hostName);
		
		
		//String preferenceString = getResponse(userPreferenceService+"?username="+username);
		//JSONObject preferencesJSON = new JSONObject(preferenceString);
		
		
		
		List<String> searches = dbm.getSearches();
		JSONArray recommendations = new JSONArray();
		//String[] splittedQuery = query.split(" ");
		//for(int i=0; i<searches.size(); i+=2)
		String expLevel;
		int i=0;
		//boolean b=false;
		//while(!searches.isEmpty()&& !searches.get(i).isEmpty()){
		while(i<searches.size()){
			System.out.println("search is not empty!");
			expLevel = getResponse(userPreferenceService+"/expertise"+"?username="+username+
					"&domain="+searches.get(i+2));
			
			/*System.out.println("Explevel: "+expLevel);
			System.out.println("searches i: "+searches.get(i));
			System.out.println("searches i+1: "+searches.get(i+1));
			System.out.println("searches i+2: "+searches.get(i+2));*/
			
			if(mobile){
				System.out.println("Mobile");
				if(!expLevel.equals("0") && i<15){
					recommendations.put(searches.get(i));
					recommendations.put(searches.get(i+1));
					i+=3;
				}
				else if(expLevel.equals("0")){
					searches.remove(i);
					searches.remove(i+1);
					searches.remove(i+2);
				}
				else if(i>=15){
					i+=3;
				}
			}
			else{
				System.out.println("Desktop");
				if(!expLevel.equals("0")){
					recommendations.put(searches.get(i));
					recommendations.put(searches.get(i+1));
					i+=3;
				}
				else{
					searches.remove(i);
					searches.remove(i+1);
					searches.remove(i+2);
				}
			}
		}
		
		
		
		
		
		return recommendations.toString();
	}
	
	
	/*@GET
	@Path("pubtest")
	public void pubsub(@QueryParam(name="username" , defaultValue = "*") 
	String username) {
		System.out.println("Username: "+username);
		XMPP xmpp = new XMPP();
        XMPPConnection connection = xmpp.getConnection();
        try {
        	System.out.println("USER:"+ username+"@role-sandbox.eu");
			Chat chat = connection.getChatManager().createChat(username+"@role-sandbox.eu", new MessageListener() {

	            public void processMessage(Chat chat, Message message) {
	                // Print out any messages we get back to standard out.
	                //System.out.println("Received message: " + message.getBody());
	                /*try {
	                	Thread.sleep(500);
	                	System.out.println("sending");
						chat.sendMessage("Howdy test2!");
					} catch (XMPPException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("XMPP EXCEPTION");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
	            /*}
	        });
			
			
			chat.sendMessage("Howdy test1!");
			//System.out.println("Sent");
			
			

	        /*while (true) {
	        	Thread.sleep(50);
	        }*/
        
        
        /*} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        



        
	}*/	 	
	    	
	
	
	// Get response from the given uri
		private String getResponse(String uri){
			
			CloseableHttpResponse response = null;
			URI httpRequest;
			String preferenceString = null;
			
			try {
				httpRequest = new URI(uri);
			
				CloseableHttpClient httpPreferenceService = HttpClients.createDefault();
				HttpGet getPreferences = new HttpGet(httpRequest);
				response = httpPreferenceService.execute(getPreferences);
				
		        HttpEntity entity = response.getEntity();
		        
		        if (entity != null) {
		            InputStream instream = entity.getContent();
		            preferenceString = convertStreamToString(instream);
		            //System.out.println("RESPONSE: " + preferenceString);
		            instream.close();
		        }
	        
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        return preferenceString;
			
		}
		
		private static String convertStreamToString(InputStream is) {
			
		    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		    StringBuilder sb = new StringBuilder();
		
		    String line = null;
		    try {
		        while ((line = reader.readLine()) != null) {
		            sb.append(line + "\n");
		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		    } finally {
		        try {
		            is.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		    return sb.toString();
		}
	
	
	// ================= Swagger Resource Listing & API Declarations
	// =====================

	@GET
	@Path("api-docs")
	@Summary("retrieve Swagger 1.2 resource listing.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Swagger 1.2 compliant resource listing"),
			@ApiResponse(code = 404, message = "Swagger resource listing not available due to missing annotations."), })
	@Produces(MediaType.APPLICATION_JSON)
	public HttpResponse getSwaggerResourceListing() {
		return RESTMapper.getSwaggerResourceListing(this.getClass());
	}

	@GET
	@Path("api-docs/{tlr}")
	@Produces(MediaType.APPLICATION_JSON)
	@Summary("retrieve Swagger 1.2 API declaration for given top-level resource.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Swagger 1.2 compliant API declaration"),
			@ApiResponse(code = 404, message = "Swagger API declaration not available due to missing annotations."), })
	public HttpResponse getSwaggerApiDeclaration(@PathParam("tlr") String tlr) {
		return RESTMapper.getSwaggerApiDeclaration(this.getClass(), tlr, epUrl);
	}

	/**
	 * Method for debugging purposes. Here the concept of restMapping validation
	 * is shown. It is important to check, if all annotations are correct and
	 * consistent. Otherwise the service will not be accessible by the
	 * WebConnector. Best to do it in the unit tests. To avoid being
	 * overlooked/ignored the method is implemented here and not in the test
	 * section.
	 * 
	 * @return true, if mapping correct
	 */
	public boolean debugMapping() {
		String XML_LOCATION = "./restMapping.xml";
		String xml = getRESTMapping();

		try {
			RESTMapper.writeFile(XML_LOCATION, xml);
		} catch (IOException e) {
			e.printStackTrace();
		}

		XMLCheck validator = new XMLCheck();
		ValidationResult result = validator.validate(xml);

		if (result.isValid())
			return true;
		return false;
	}

	/**
	 * This method is needed for every RESTful application in LAS2peer. There is
	 * no need to change!
	 * 
	 * @return the mapping
	 */
	public String getRESTMapping() {
		String result = "";
		try {
			result = RESTMapper.getMethodsAsXML(this.getClass());
		} catch (Exception e) {

			e.printStackTrace();
		}
		return result;
	}

}
