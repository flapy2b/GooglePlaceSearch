package homeaway;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GoogleMap {

	/**
	 * The main function
	 * @param args
	 */
	public static void main(String[] args) { 
		
		/** To customize the script, change the 3 following variables *//
		
		/*
		 * The GPS coordinates of the area where you want to make the search. Each point is spaced from each other by 10 kilometers.  
		 * Here, an example of corsican's centroids
		 */
		String[] centroids = { "41.427003373,9.116869581", "41.420962865,9.219398859", "41.613684024,8.825774078",
				"41.607926496,8.928655586", "41.602068426,9.031516932", "41.596109846,9.134357772",
				"41.590050788,9.237177764", "41.583891285,9.339976564", "41.788551949,8.739289836",
				"41.782877616,8.842484219", "41.777102402,8.945658613", "41.771226338,9.04881267",
				"41.765249456,9.151946046", "41.75917179,9.255058393", "41.752993373,9.358149368",
				"41.963385448,8.6522923", "41.957794903,8.755801216", "41.952103136,8.85929032",
				"41.94631018,8.962759261", "41.940416066,9.066207691", "41.934420827,9.16963526",
				"41.928324495,9.273041621", "41.922127105,9.376426425", "42.132676181,8.668602218",
				"42.127068457,8.772407704", "42.121359202,8.876193206", "42.115548448,8.979958372",
				"42.109636228,9.08370285", "42.103622574,9.187426286", "42.097507519,9.291128331",
				"42.091291098,9.394808632", "42.084973343,9.49846684", "42.301996182,8.685006371",
				"42.296371225,8.789110122", "42.290644427,8.893193717", "42.284815821,8.997256798",
				"42.278885438,9.101299012", "42.272853312,9.205320003", "42.266719475,9.309319416",
				"42.260483962,9.413296898", "42.254146808,9.517252094", "42.47134406,8.701505576",
				"42.465701817,8.805909303", "42.459957422,8.910292697", "42.454110907,9.0146554",
				"42.448162304,9.118997054", "42.442111648,9.2233173", "42.435958971,9.327615781",
				"42.429704307,9.43189214", "42.423347692,9.536146021", "42.629296788,8.927491004",
				"42.623432308,9.032155049", "42.61746543,9.13679786", "42.611396185,9.241419078",
				"42.605224608,9.34601834", "42.598950734,9.450595289", "42.774514985,9.36452802",
				"42.76822184,9.469407283", "42.943828695,9.383145754" };
		
		/*
		 * The keywords: which activities or commercials you search
		 */
		String[] keywords = {"restaurant", "pub"};
		
		/*
		 * Your Google Apikey you can generate from your Google Cloud console page: https://console.cloud.google.com 
		 */
		String apiKey = "yourApiKey";
		
		int j = 0;
		ArrayList<GooglePoint> points = new ArrayList<>();
		ArrayList<GooglePoint> nextpoints;
		
		for(String keyword : keywords){
			System.out.println(" ++++ " + keyword + " +++ ");
			j = 0;
			points = new ArrayList<>();
			
			for(String centroid : centroids){
				j++;
				System.out.println(" -- Centroid #" + j + " --");
				int i = 1;
				
				System.out.println("Page 1");
				String response = request(centroid, keyword, null);
				JSONObject json = new JSONObject(response);
				JSONArray results = json.getJSONArray("results");
				nextpoints = parse(results);

				for(GooglePoint nextpoint : nextpoints){
					if(!myContains(points, nextpoint))
						points.add(nextpoint);
				}
				
				while(json.has("next_page_token") && i < 10){
					i++;
					System.out.println("Page " + i);
					try {
						Thread.sleep(4000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					response = request(centroid, keyword, json.getString("next_page_token"));
					json = new JSONObject(response);
					results = json.getJSONArray("results");
					nextpoints = parse(results);
					for(GooglePoint nextpoint : nextpoints){
						if(!myContains(points, nextpoint))
							points.add(nextpoint);
					}
				}
			}
			System.out.println("Nb résults : " + points.size());
			createCSV(points, keyword);
		}
		
	}
	
	/**
	 * Check if a point is include into list of points
	 * @param points
	 * @param point
	 * @return
	 */
	public static boolean myContains(ArrayList<GooglePoint> points, GooglePoint point){
		for(GooglePoint each: points){
			if(each.equals(point)) return true;
		}
		return false;
	}
	
	/**
	 * Check if a point is include into list of points
	 * @param points
	 * @param search
	 * @return
	 */
	public static boolean pointExists(ArrayList<GooglePoint> points, GooglePoint search){
		for(GooglePoint point : points){
			if(point.id == search.id) return true;
		}
		return false;
	}
	
	/**
	 * Parse the JSON result and put it into a GooglePoint object
	 * @param results
	 * @return
	 */
	public static ArrayList<GooglePoint> parse(JSONArray results){
		ArrayList<GooglePoint> points = new ArrayList<GooglePoint>();
		for(int i = 0; i < results.length(); i++){
			JSONObject result = results.getJSONObject(i);
			
			try{
				JSONObject geometry = result.getJSONObject("geometry");
				GooglePoint point = new GooglePoint(result);
				points.add(point);
			}
			catch(JSONException e){
				System.out.println(e);
			}
		}
		return points;
	}
	
	/**
	 * Create a CSV file with the results from a GooglePoint list
	 * @param points
	 * @param search The keyword. The generated file will be names with this word and the extension .CSV.
	 */
	public static void createCSV(ArrayList<GooglePoint> points, String search){
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File("export/" + search + ".csv"), "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
        StringBuilder sb = new StringBuilder();
        //sb.append("id");
        sb.append("latitude;longitude;name;address;types;rating");
        sb.append("\n");
        
        for(GooglePoint point : points){
        	sb.append(point.toCSV());
        	sb.append("\n");
        }
        
        pw.write(sb.toString());
        pw.close();
	}

	/**
	 * Make the request to Google API and all your parameters previously entered 
	 * @param location
	 * @param search
	 * @param pagetoken
	 * @param apiKey
	 * @return
	 */
	public static String request(String location, String search, String pagetoken, String apiKey){
		String out = "";
		try {
			String targetUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
			
			String url; 

			if(pagetoken == null) url = targetUrl + "location=" + location + "&rankby=distance&keyword=" + search + "&key=" + apiKey;
			else url = targetUrl + "pagetoken=" + pagetoken + "&key=" + apiKey;
			System.out.println(url);
			URL serverUrl = new URL(url);
			URLConnection urlConnection = serverUrl.openConnection();
			HttpURLConnection httpConnection = (HttpURLConnection)urlConnection;
			
			InputStreamReader in = new InputStreamReader(httpConnection.getInputStream());

	        int read;
	        StringBuilder jsonResults = new StringBuilder();
	        char[] buff = new char[1024];
	        while ((read = in.read(buff)) != -1) {
	        	jsonResults.append(buff, 0, read);
	        }
	        return jsonResults.toString();
			
		} catch (IOException e){
			System.out.println(e);
		}catch (Exception e) {
	    	System.out.println(e);
	    	return "";
	    }
		
		return out;
	}
}
