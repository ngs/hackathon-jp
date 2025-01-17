package jp.hackathon.vc.gae.action;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import org.json.JSONArray; // JSON library from http://www.json.org/java/
import org.json.JSONObject;

public class GoogleQuery {

	// Put your website here
	private static final String HTTP_REFERER = "http://voctrl.appspot.com/";
	private static final String KEY = "ABQIAAAAMI2qOraKf1AKK762okOfpxRdIXpbsmqJJwgH19Ff8xKqLPZM9RQ0S3RHR3OKrSkzL8FMvfV5DUfsiQ";
 
//	public GoogleQuery() {
//		makeQuery("questio verum");
//		makeQuery("info:http://frankmccown.blogspot.com/");
//		makeQuery("site:frankmccown.blogspot.com");
//	}
//	public void makeQuery(String query){
//		makeQuery(query, new PrintWriter(System.out));
//	}
	public void makeQuery(String query, PrintWriter writer) {

		//writer.println("\nQuerying for " + query);

		try {
			// Convert spaces to +, etc. to make a valid URL
			query = URLEncoder.encode(query, "UTF-8");

			URL url = new URL(
					"http://ajax.googleapis.com/ajax/services/search/web?start=0&rsz=large&v=1.0&key="+KEY+"&q="+ query);
			URLConnection connection = url.openConnection();
			connection.addRequestProperty("Referer", HTTP_REFERER);

			// Get the JSON response
			String line;
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}

			String response = builder.toString();
			JSONObject json = new JSONObject(response);

			writer.println("Total results = "
					+ json.getJSONObject("responseData")
							.getJSONObject("cursor").getString(
									"estimatedResultCount"));

			JSONArray ja = json.getJSONObject("responseData").getJSONArray("results");

			writer.println("\nResults:");
			for (int i = 0; i < ja.length(); i++) {
				writer.print((i + 1) + ". ");
				JSONObject j = ja.getJSONObject(i);
				writer.println(j.getString("titleNoFormatting"));
				writer.println(j.getString("url"));
			}
		} catch (Exception e) {
			writer.println("Something went wrong...");
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		new GoogleQuery();
	}
}