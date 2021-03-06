package i5.las2peer.services.videoRecommender.database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


//import de.dbis.util.GetProperty;

/**
 * 
 * Establishes connection to the MySql database.
 * Uses 'dbconnection.properties' file for configuration.
 *
 */
public class DatabaseManager
{
	private final static String INPUT_FILE = "etc/i5.las2peer.services.videoAdapter.dbconnection.properties";
	private static String url;
	private static String host;
	private static String port;
	private static String dbName;
	private static String driver;
	private static String userName;
	private static String password;
	private static String databaseServer;
	
	public void init(String driver, String databaseServer, String port, String dbName, String userName, String password, String host) {

		System.out.println("DB CHECK: ");
		
		this.driver = driver;
		this.databaseServer = databaseServer;
		this.port = port;
		this.dbName = dbName;
		this.userName = userName;
		this.password = password;
		this.host = host;
		
		url = "jdbc:" + this.databaseServer + "://" + this.host +":"+ this.port + "/";
		
		System.out.println("DB URL: "+url);
	}
	
	
	public List<String> getSearches(){
		
		ResultSet res = null;
		List<String> searches = new ArrayList<String>();
		
		try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);
			
			String selectQuery = "select max(id) id, result, user, query, domain FROM "
					+ "searches group by query";
			//String selectQuery = "SELECT * FROM searches";
			
			PreparedStatement pstmt = conn.prepareStatement(selectQuery);
			res = pstmt.executeQuery();

			while(res.next()){
				
				searches.add(res.getString("query"));
				searches.add(res.getString("result"));
				searches.add(res.getString("domain"));
			}

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return searches;
	}
	
}	