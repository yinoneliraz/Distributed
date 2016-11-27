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
import org.json.JSONObject;

public class Worker {

	public static void main(String[] args) throws ClientProtocolException, IOException {
		// TODO Auto-generated method stub
		String url = "https://api.nasa.gov/neo/rest/v1/feed?start_date=2015-09-07&end_date=2015-09-08&api_key=9hZ8Yyim2EC1ZrEs2XlySosrCTLXaVpqtQwo1SHJ";

		//Dates
		LocalDate date=LocalDate.parse("2014-02-28");
		date=date.plusDays(1);
		System.out.println(date);
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);

		// add request header
		request.addHeader("User-Agent", "Dist1");
		HttpResponse response = client.execute(request);

		InputStream isr=response.getEntity().getContent();
		System.out.println("Response Code : "
		                + response.getEntity().getContent());

		BufferedReader rd = new BufferedReader(
			new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
			//System.out.println(line);
		}
		System.out.println(result);
		JSONObject json=new JSONObject(result);
		
		
	}

}
