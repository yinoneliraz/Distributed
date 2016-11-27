import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Worker {

	final static String NEAR_EARTH_OBJECTS="near_earth_objects";
	final static String NAME="name";
	final static String CLOSE_APPROACH="close_approach_data";
	final static String RELATIVE_VELOCITY="relative_velocity";
	final static String KPS="kilometers_per_second";
	final static String HAZARDOUS="is_potentially_hazardous_asteroid";
	final static String DIAMETER="estimated_diameter";
	final static String METERS="meters";
	final static String MAX_DIAMETER_METERS="estimated_diameter_max";
	final static String MISS_DISTANCE="miss_distance";
	final static String ASTRONOMICAL="astronomical";
	
	public static void main(String[] args) throws ClientProtocolException, IOException, JSONException {

		String url = "https://api.nasa.gov/neo/rest/v1/feed?start_date=2015-09-07&end_date=2015-09-08&api_key=9hZ8Yyim2EC1ZrEs2XlySosrCTLXaVpqtQwo1SHJ";

		LocalDate startDate=LocalDate.parse("2015-09-07");
		LocalDate endDate=LocalDate.parse("2015-09-08");
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);

		request.addHeader("User-Agent", "Dist1");
		HttpResponse response = client.execute(request);
		int counter=0;
		while(response.getStatusLine().getStatusCode()!=200 && counter<10){
			response = client.execute(request);
			counter++;
		}

		BufferedReader rd = new BufferedReader(
			new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		
		System.out.println(result);
		final JSONObject json=new JSONObject(result.toString());
		JSONObject near= json.getJSONObject(NEAR_EARTH_OBJECTS);
		while(!startDate.equals(endDate)){
			JSONArray arr;
			if(near.has(startDate.toString())){
				arr = near.getJSONArray(startDate.toString());
				for(int i=0;i<arr.length();i++){
					JSONObject temp=arr.getJSONObject(i);
					System.out.println("Astroide " + temp.getString(NAME));
					String isHazard=determineAstroide(temp);
					System.out.println(" " + isHazard);
				}
			}
			startDate=startDate.plusDays(1);
		}		
	}
	
	private static String determineAstroide(JSONObject astroide) throws JSONException{
		if(astroide.getJSONArray(CLOSE_APPROACH).getJSONObject(0).getJSONObject(RELATIVE_VELOCITY).getDouble(KPS)<10 &&
				astroide.getBoolean(HAZARDOUS))
			return "GREEN";
		if(astroide.getJSONArray(CLOSE_APPROACH).getJSONObject(0).getJSONObject(RELATIVE_VELOCITY).getDouble(KPS)>10 &&
				astroide.getBoolean(HAZARDOUS) && 
				astroide.getJSONObject(DIAMETER).getJSONObject(METERS).getDouble(MAX_DIAMETER_METERS)<200)
			return "YELLOW";
		if(astroide.getJSONArray(CLOSE_APPROACH).getJSONObject(0).getJSONObject(RELATIVE_VELOCITY).getDouble(KPS)>10 &&
				astroide.getBoolean(HAZARDOUS) && 
				astroide.getJSONArray(CLOSE_APPROACH).getJSONObject(0).getJSONObject(MISS_DISTANCE).getDouble(ASTRONOMICAL)>0.3 && 
				astroide.getJSONObject(DIAMETER).getJSONObject(METERS).getDouble(MAX_DIAMETER_METERS)>200)
			return "RED";
		return "GREEN";
	}
}
