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
import i5.las2peer.services.videoRecommender.database.DatabaseManager;
import i5.las2peer.services.videoRecommender.util.LocationService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
@Path("recommend")
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

	@GET
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
		while(!searches.isEmpty()){
			b=false;
			for(int j=0;j<splittedQuery.length;j++){
				if(searches.get(i).contains(splittedQuery[j]))
						b=true;
			}
			if(!b){
				searches.remove(i);
				searches.remove(i+1);
				
			}
			else{
				recommendations.put(searches.get(i+1));
				i+=2;
			}
				
		}
		
		
		
		return "";
	}
	
	
	@GET
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
	            }
	        });
			
			
			chat.sendMessage("Howdy test1!");
			//System.out.println("Sent");
			
			

	        /*while (true) {
	        	Thread.sleep(50);
	        }*/
        
        
        } catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        



        
	}	 	
	    	
	
	
	
	    
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	@GET
	@Path("getPlaylist")
	public String getPlaylist(@QueryParam(name="Username" , defaultValue = "*") String username, @QueryParam(name = "search", defaultValue = "*" ) String searchString){

		System.out.println("SEARCH: "+searchString);
		dbm = new DatabaseManager();
		dbm.init(driverName, databaseServer, port, database, this.username, password, hostName);
		
		dbm.userExists(username);
		
		String annotations = getAnnotations(searchString, username);
	    
		
		return annotations;
	}
	
	
	private String getAnnotations(String searchString, String username){
		System.out.println("An1");
		CloseableHttpResponse response = null;
		URI request = null;
		JSONArray finalResult = null;
		 int size;
		
		try {
			
			// Get Annotations
			request = new URI("http://eiche:7073/annotations/annotations?q="+searchString.replaceAll(" ", ",")+"&part=duration,objectCollection,location,objectId,text,time,title,keywords&collection=TextTypeAnnotation");
			
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpGet get = new HttpGet(request);
			
			response = httpClient.execute(get);
			
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			
			StringBuilder content = new StringBuilder();
			String line;
			
			while (null != (line = rd.readLine())) {
			    content.append(line);
			}
			
			// Parse Response to JSON
			finalResult = new JSONArray(content.toString());
			String[] objectIds = new String[finalResult.length()];
			float time[] = new float[finalResult.length()];
			float duration[] = new float[finalResult.length()];
			// Remove the non-video annotations
			int i=0;
			while(!finalResult.isNull(i)){
				JSONObject object = finalResult.getJSONObject(i);
				if(!"Videos".equals(object.getString("objectCollection"))){
					finalResult.remove(i);
			    }
				else{
					//System.out.println("An2");
					// Get the object Ids from the response
					objectIds[i] = new String(object.getString("objectId"));
					
					//System.out.println("TIME: "+ Float.valueOf(object.getString("time")));
					
					time[i] = Float.valueOf(object.getString("time"));
					duration[i] = Float.valueOf(object.getString("duration"));
					
					i++;
					System.out.println("An3");
				}
			}
			
			
			// Temporary code, to limit entries to 3
			//int j=3;
			//while(!finalResult.isNull(j)){
				//	finalResult.remove(j);
			//}
			
			System.out.println("An4");
			// The size of the following arrays would be 'size', but for now it is 'j' 
			size=i;
			//int j = size;
			String[] videos = new String[size];
			String[] languages = new String[size];
			
			// Once again, 'j' is temporary, it will be 'size' 
			videos = getVideoURLs(objectIds,size);
			languages = getVideoLang(objectIds,size);
			
			for(int k=0;k<size;k++){
				float endtime = duration[k]+time[k];
				videos[k]+="#t="+time[k]+","+endtime;
			}
			
			JSONObject object;
			
			for(int k=0;k<size;k++){
				object = finalResult.getJSONObject(k);
				object.append("videoURL", videos[k]);
				//object.append("lang", languages[k]);
				object.put("lang", languages[k]);
			}
			//System.out.println("FINAL RESULT: "+finalResult.toString());
			FOSPClass fpc = new FOSPClass();
			finalResult = fpc.applyPreferences(finalResult, username, driverName, databaseServer, port, database, this.username, password, hostName);
			
			RelevanceSorting rsort = new RelevanceSorting();
			LocationSorting lsort = new LocationSorting();
			System.out.println("RELEVANCE");
			finalResult = rsort.sort(finalResult, searchString);
			//double userLat = 50.7743273, userLong = 6.1065564;
			
			dbm = new DatabaseManager();
			dbm.init(driverName, databaseServer, port, database, this.username, password, hostName);
			String[] preferences = dbm.getPreferences(username);
			LocationService ls = new LocationService();
			double[] userltln = ls.getLongitudeLatitude(preferences[4]);
			finalResult = lsort.sort(finalResult, userltln[0], userltln[1]);
			
			System.out.println("check");
			
			
			
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
		
		return finalResult.toString();

	}
*/			
	
	
	
	
	
	
	
	
	
	
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
