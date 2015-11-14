package i5.las2peer.services.videoRecommender;

import i5.las2peer.api.Service;
import i5.las2peer.restMapper.HttpResponse;
import i5.las2peer.restMapper.RESTMapper;
import i5.las2peer.restMapper.annotations.GET;
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
import i5.las2peer.services.videoRecommender.util.OIDC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;

import com.arangodb.entity.GraphEntity;

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
	private String adapterService;
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
	public HttpResponse getInitialRecommendations(@QueryParam(name = "Authorization", defaultValue = "") String token, 
			@QueryParam(name="mobile" , defaultValue = "false") boolean mobile){

		String username = null;
		
		System.out.println("TOKEN: "+token);
		
		if(token!=null){
			   token = token.replace("Bearer ","");
			   username = OIDC.verifyAccessToken(token, userinfo);
		}
		
		if(username.isEmpty() || username.equals("undefined") || 
				username.equals("error")){
			HttpResponse r = new HttpResponse("User is not signed in!");
			r.setStatus(401);
			return r;
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
		int count = 0;
		while(i<searches.size()){
			
			expLevel = getResponse(userPreferenceService+"/expertise"+"?Authorization=Bearer%20"+token+
					"&domain="+searches.get(i+2));
			
			System.out.println("search is not empty! "+expLevel);
			
			/*System.out.println("Explevel: "+expLevel);
			System.out.println("searches i: "+searches.get(i));
			System.out.println("searches i+1: "+searches.get(i+1));
			System.out.println("searches i+2: "+searches.get(i+2));*/
			
			if(mobile){
				System.out.println("Mobile");
				if(!(Float.parseFloat(expLevel.replace("\n", ""))==0) && count<5){
				//if(!expLevel.equals("0") && i<15){
					count++;
					recommendations.put(searches.get(i));
					recommendations.put(searches.get(i+1));
					i+=3;
				}
				else if(Float.parseFloat(expLevel.replace("\n", ""))==0){
					i+=3;
					/*searches.remove(i);
					searches.remove(i+1);
					searches.remove(i+2);*/
				}
				else if(count>=5){
					i+=3;
				}
			}
			else{
				System.out.println("Desktop "+Float.parseFloat(expLevel.replace("\n", "")));
				if(!(Float.parseFloat(expLevel.replace("\n", ""))==0) && count<10){
					count++;
					System.out.println("NOT ZERO: "+expLevel);
					recommendations.put(searches.get(i));
					recommendations.put(searches.get(i+1));
					i+=3;
				}
				else if(Float.parseFloat(expLevel.replace("\n", ""))==0){
					i+=3;
					/*searches.remove(i);
					searches.remove(i+1);
					searches.remove(i+2);*/
				}
				else if(count>=10){
					i+=3;
				}
			}
		}
		
		
		HttpResponse r = new HttpResponse(recommendations.toString());
		r.setStatus(200);
		return r;
		
		
		
	}
	
	@GET
	@Path("relatedSearch")
	public HttpResponse getRelatedSearches(@QueryParam(name = "Authorization", defaultValue = "") String token, 
			@QueryParam(name="mobile" , defaultValue = "false") boolean mobile,
			@QueryParam(name = "search", defaultValue = "*" ) String searchString){

		String username = null;
		
		System.out.println("TOKEN: "+token);
		
		if(token!=null){
			   token = token.replace("Bearer ","");
			   username = OIDC.verifyAccessToken(token, userinfo);
		}
		
		if(username.isEmpty() || username.equals("undefined") || 
				username.equals("error")){
			HttpResponse r = new HttpResponse("User is not signed in!");
			r.setStatus(401);
			return r;
		}
		
		System.out.println("username: "+username);
		dbm = new DatabaseManager();
		dbm.init(driverName, databaseServer, port, database, this.username, password, hostName);
		
		String queries[] = searchString.split(" ");
		
		JSONArray recommendations = new JSONArray();
		
		JSONArray result;
		
		for(int i=0;i<queries.length;i++){
			
			String searchResult = getResponse(adapterService+"/playlist"+"?Authorization=Bearer%20"+token+
					"&sub=123&search="+queries[i]+"&sequence=LRDOW");
			System.out.println(queries[i]+" SEARCHRESULT 1 "+searchResult);
			
			try{
				result = new JSONArray(searchResult);
				if(result.length()>0){
					recommendations.put(queries[i]);
					recommendations.put(searchResult);
				}
			}
			catch (JSONException e){
				
			}
			for(int j=0;j<queries.length;j++){
				
				if(i!=j){
					searchResult = getResponse(adapterService+"/playlist"+"?Authorization=Bearer%20"+token+
							"&sub=123&search="+queries[i]+"%20"+queries[j]+"&sequence=LRDOW");
					System.out.println(queries[i]+" "+queries[j]+" SEARCHRESULT 2 "+searchResult);
					try{
						result = new JSONArray(searchResult);
						if(result.length()>0){
							recommendations.put(queries[i]+" "+queries[j]);
							recommendations.put(searchResult);
						}
					}catch (JSONException e){
						
					}
				}
			
			
			/*if(i==0){
				searchResult = searchResult.substring(0, searchResult.length()-1);
				searchResult+=",";
			}
			
			if(i!=0 && i!=queries.length-1){
				searchResult = searchResult.substring(1);
				searchResult = searchResult.substring(0, searchResult.length()-1);
				searchResult+=",";
			}
			
			if(i==queries.length-1){
				searchResult = searchResult.substring(1);
			}*/
			//recommendations+=searchResult;
			}
		}
		
		
		if(recommendations.length()==0){
			
			HttpResponse r = new HttpResponse("No Annotations found!");
			r.setStatus(204);
			return r;
		}
		
		
		HttpResponse r = new HttpResponse(recommendations.toString());
		r.setStatus(200);
		return r;
	}
	
	
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
