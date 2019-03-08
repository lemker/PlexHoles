import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URI;
import java.time.LocalDate;
import java.util.Calendar;

class TVDB {
    static Header[] getHeaders() {

        Log.print("Getting TVDB headers...");

        // Create new HttpClient object
        HttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build();

        // Create new HttpPost object
        HttpPost httpPost = new HttpPost("https://api.thetvdb.com/login");

        // Create new StringEntity object for JSON request
        StringEntity requestEntity = new StringEntity(
          "{\n" +
                "\"apikey\": \"" + Settings.getTVDB_API_KEY() + "\",\n" +
                "\"userkey\": \"" + Settings.getTVDB_USERKEY() + "\",\n" +
                "\"username\": \"" + Settings.getTVDB_USERNAME() + "\"\n" +
                "}",
                ContentType.APPLICATION_JSON);
        try {

            // Set POST headers
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // Set POST entity
            httpPost.setEntity(requestEntity);

            // Execute POST and get response
            HttpResponse response = httpClient.execute(httpPost);

            // Create new response handler
            ResponseHandler<String> handler = new BasicResponseHandler();

            // Create new JSON object from response
            JSONObject obj = new JSONObject(handler.handleResponse(response));

            // Add authorization header to POST object
            httpPost.addHeader("Authorization", "Bearer " + obj.getString("token"));

            Log.print("Success!");

            // Return token
            return httpPost.getAllHeaders();

        } catch (Exception e) {
            Log.print("Error: cannot get TVDB token!");
            e.printStackTrace();
        }
        return null;
    }

    static PlexSeries getSeries(Header[] headers, PlexSeries series) {

        Log.print("Getting TVDB series data for: \"" + series.getTitle() + "\"");

        // Create new custom HttpClient
        HttpClient httpClient = new CustomHttpClient().get();

        try {

            // Initialize page counter
            int page = 1;

            // Create new HttpGet object
            HttpGet getEpisodes = new HttpGet("https://api.thetvdb.com/series/" + series.getGUID() + "/episodes?page=" + page);

            // Add all headers to HttpGet object
            for (int i = 0; i < headers.length; i++) {
                getEpisodes.addHeader(headers[i]);
            }

            // Get HTTP response
            HttpResponse response = httpClient.execute(getEpisodes);

            // Create new response handler
            ResponseHandler<String> handler = new BasicResponseHandler();

            // Create JSON response object
            JSONObject JSONResponseObject = new JSONObject(handler.handleResponse(response));

            // Create new ArrayList
            JSONArray JSONEpisodeArray = new JSONArray();

            // Get the last page
            int lastPage = JSONResponseObject.getJSONObject("links").getInt("last");

            Log.print("Found " + lastPage + " page(s) for series");
            Log.print("Parsing page: " + page);

            // Loop through JSON array and add objects
            for (int i = 0; i < JSONResponseObject.getJSONArray("data").length(); i++) {

                // Check if episode is in season "0"
                if (JSONResponseObject.getJSONArray("data").getJSONObject(i).getInt("airedSeason") == 0) {
                    Log.print("Skipping episode \"" + JSONResponseObject.getJSONArray("data").getJSONObject(i).get("episodeName") + "\" since it is in season \"0\"!");
                    continue;
                }

                // Check if episode has aired date
                if (!JSONResponseObject.getJSONArray("data").getJSONObject(i).has("firstAired")) {
                    Log.print("Skipping episode \"" + JSONResponseObject.getJSONArray("data").getJSONObject(i).get("episodeName") + "\" since there is no air date!");
                    continue;
                }

                // Check if episode has aired
                System.out.println("Episode aired date: " +JSONResponseObject.getJSONArray("data").getJSONObject(i).getString("firstAired"));
                LocalDate dateToday = LocalDate.now();
                String[] aired = JSONResponseObject.getJSONArray("data").getJSONObject(i).getString("firstAired").split("-");
                LocalDate dateAired = LocalDate.of(Integer.parseInt(aired[0]), Integer.parseInt(aired[1]), Integer.parseInt(aired[2]));


                if (dateToday.isBefore(dateAired)) {
                    Log.print("Skipping episode \"" + JSONResponseObject.getJSONArray("data").getJSONObject(i).get("episodeName") + "\" since it is airing after the current date!");
                    continue;
                }

                // Add episode to array
                JSONEpisodeArray.put(JSONResponseObject.getJSONArray("data").getJSONObject(i));
            }

            // Loop through all pages
            while (page < lastPage) {

                // Increment page counter
                page++;

                System.out.println("Parsing page: " + page);

                // Create new URI for GET
                URI uri = new URI("https://api.thetvdb.com/series/" + series.getGUID() + "/episodes?page=" + page);

                // Set URI for GET
                getEpisodes.setURI(uri);

                // Get HTTP response
                HttpResponse responseLoop = httpClient.execute(getEpisodes);

                // Create new response handler
                ResponseHandler<String> handlerLoop = new BasicResponseHandler();
                JSONObject JSONResponseObjectLoop = new JSONObject(handlerLoop.handleResponse(responseLoop));

                // Loop through JSON array and add objects
                for (int i = 0; i < JSONResponseObjectLoop.getJSONArray("data").length(); i++) {
                    // Check if episode is in season "0"
                    if (JSONResponseObjectLoop.getJSONArray("data").getJSONObject(i).getInt("airedSeason") == 0) {
                        Log.print("Skipping episode \"" + JSONResponseObjectLoop.getJSONArray("data").getJSONObject(i).get("episodeName") + "\" since it is in season \"0\"!");
                        continue;
                    }

                    // Check if episode has aired date
                    if (!JSONResponseObjectLoop.getJSONArray("data").getJSONObject(i).has("firstAired")) {
                        Log.print("Skipping episode \"" + JSONResponseObjectLoop.getJSONArray("data").getJSONObject(i).get("episodeName") + "\" since there is no air date!");
                        continue;
                    }

                    // Check if episode has aired
                    LocalDate dateToday = LocalDate.now();
                    String[] aired = JSONResponseObject.getJSONArray("data").getJSONObject(i).getString("firstAired").split("-");
                    LocalDate dateAired = LocalDate.of(Integer.parseInt(aired[0]), Integer.parseInt(aired[1]), Integer.parseInt(aired[2]));
                    if (dateToday.isBefore(dateAired)) {
                        Log.print("Skipping episode \"" + JSONResponseObjectLoop.getJSONArray("data").getJSONObject(i).get("episodeName") + "\" since it is airing after the current date!");
                        continue;
                    }

                    // Add JSON object to array
                    JSONEpisodeArray.put(JSONResponseObjectLoop.getJSONArray("data").getJSONObject(i));
                }
            }

            System.out.println("Found " + JSONEpisodeArray.length() + " episodes in series: \"" + series.getTitle() + "\"");

            // Set series TVDB episode data
            series.setTVDBSeriesData(JSONEpisodeArray);

            return series;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
