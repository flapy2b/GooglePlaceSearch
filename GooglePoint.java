package homeaway;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A class which represent a GooglePoint
 * @author gueniot
 *
 */
public class GooglePoint {
	public String id;
	public double latitude;
	public double longitude;
	public String name;
	public String vicinity ;
	public String types;
	public double rating ;
	
	/**
	 * Default constructor 
	 */
	public GooglePoint(){
		this.id = "";
		this.latitude = 0;
		this.longitude = 0;
		this.name = "";
		this.vicinity = "";
		this.types = "";
		this.rating = 0;
	}
	
	/**
	 * Constructor
	 * @param result The JSON return of an API request 
	 */
	public GooglePoint(JSONObject result){			
		try{
			JSONObject location = result.getJSONObject("geometry").getJSONObject("location");
			this.id = result.get("id").toString();
			this.latitude = location.getDouble("lat");
			this.longitude = location.getDouble("lng");
			this.name = result.get("name").toString();
			this.vicinity = result.get("vicinity").toString();
			this.types = result.getJSONArray("types").toString();
			this.types = this.types.replace("[", "");
			this.types = this.types.replace("]", "");
			
			if(!result.isNull("rating")){
				this.rating = result.getDouble("rating");
			}
		}
		catch(JSONException e){
			System.out.println(e);
		}
	}
	
	/**
	 * Return an object in CSV
	 * @return
	 */
	public String toCSV(){
		return latitude + ";" + longitude + ";" + name + ";" + vicinity + ";"	+ types + ";" + rating;
	}
	
	/**
	 * Check equality between two points
	 * @param point
	 * @return A boolean
	 */
	public boolean equals(GooglePoint point){
		return (point.id.equals(this.id));
	}
	
	public int hashCode(){
		return id.hashCode() * 31;
	}
}
