import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.json.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

class Plex {
    /**
     * Get Plex header information
     * @return                 Array of Plex headers
     */
    static Header[] getHeaders() {
        Log.print("Getting Plex header information...");

        // Merge username and password
        String userPass = Settings.getPLEX_SERVER_USERNAME() + ":" + Settings.getPLEX_SERVER_PASSWORD();

        // Convert Plex authentication information to base 64
        byte[] auth = Base64.encodeBase64(userPass.getBytes());

        // Create new HttpClient object
        HttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build();

        // Create new HttpPost object
        HttpPost httpPost = new HttpPost("https://plex.tv/users/sign_in.json");

        // Add Plex headers
        httpPost.addHeader("Authorization", "Basic " + new String(auth));
        httpPost.addHeader("X-Plex-Client-Identifier", "plex-tv-helper");
        httpPost.addHeader("X-Plex-Product", "plex-tv-helper");
        httpPost.addHeader("X-Plex-Version", "0.1");

        try {
            // Get HTTP response
            HttpResponse response = httpClient.execute(httpPost);

            // Create new response handler
            ResponseHandler<String> handler = new BasicResponseHandler();

            // Create new JSON object from response
            JSONObject obj = new JSONObject(handler.handleResponse(response));

            // Add Plex token header
            httpPost.addHeader("X-Plex-Token", obj.getJSONObject("user").getString("authToken"));

            // Remove authorization header
            httpPost.removeHeaders("Authorization");

            Log.print("Success!");

            // Return headers
            return httpPost.getAllHeaders();
        } catch (Exception e) {
            System.out.println("Error: cannot get Plex token!");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all media container keys of type "show"
     * @param headers          Plex header information
     * @return                 ArrayList of media container keys
     */
    static ArrayList<String> getTVMediaContainerKeys(Header[] headers) {

        // Create new custom HttpClient
        HttpClient httpClient = new CustomHttpClient().get();

        // Create new HttpGet object
        HttpGet getTVKeys = new HttpGet(Settings.getPLEX_SERVER_URL() + "/library/sections");

        // Add all headers to HttpGet object
        for (int i = 0; i < headers.length; i++) {
            getTVKeys.addHeader(headers[i]);
        }

        try {
            // Get HTTP response
            HttpResponse response = httpClient.execute(getTVKeys);

            // Create new response handler
            ResponseHandler<String> handler = new BasicResponseHandler();
            String responseString = handler.handleResponse(response);

            // Parse string node values
            return XML.parseStringAsNodesValue(responseString, "//MediaContainer/Directory[@type='show']/@key");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all rating keys
     * @param headers          Plex header information
     * @param key              Key that is to be found
     * @return                 ArrayList of of series keys
     */
    static ArrayList<String> getRatingKeys(Header[] headers, int key) {

        // Create new custom HttpClient
        HttpClient httpClient = new CustomHttpClient().get();

        // Create new HttpGet object
        HttpGet getSeriesKeys = new HttpGet(Settings.getPLEX_SERVER_URL() + "/library/sections/" + key + "/all/");

        // Add all headers to HttpGet object
        for (int i = 0; i < headers.length; i++) {
            getSeriesKeys.addHeader(headers[i]);
        }

        try {
            // Get HTTP response
            HttpResponse response = httpClient.execute(getSeriesKeys);

            // Create new response handler
            ResponseHandler<String> handler = new BasicResponseHandler();
            String responseString = handler.handleResponse(response);

            // Create output ArrayList
            ArrayList<String> output = XML.parseStringAsNodesValue(responseString, "//MediaContainer/Directory/@ratingKey");

            // Remove possibly duplicates in ArrayList
            assert(output != null);
            Set<String> setter = new HashSet<String>(output);
            output.clear();
            output.addAll(setter);

            // Return output ArrayList
            return output;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static PlexSeries getSeriesData(Header[] headers, int key) {
        // Create new custom HttpClient
        HttpClient httpClient = new CustomHttpClient().get();

        // Create new HttpGet object
        HttpGet getSeriesKeys = new HttpGet(Settings.getPLEX_SERVER_URL() + "/library/metadata/" + key + "/");

        // Add all headers to HttpGet object
        for (int i = 0; i < headers.length; i++) {
            getSeriesKeys.addHeader(headers[i]);
        }

        try {
            // Get HTTP response
            HttpResponse response = httpClient.execute(getSeriesKeys);

            // Create new response handler
            ResponseHandler<String> handler = new BasicResponseHandler();
            String responseString = handler.handleResponse(response);

            // Create new PlexSeries object
            PlexSeries series = new PlexSeries();

            // Try to set title variable
            ArrayList<String> title = XML.parseStringAsNodesValue(responseString, "//MediaContainer/Directory/@title");
            if (title == null || title.get(0) == null) {
                System.out.println("Error: series title was not found!");
                return null;
            }
            if (title.get(0) != null) {
                series.setTitle(title.get(0));
            }

            // Set rating key
            series.setRatingKey(key);

            // Try to set GUID variable
            ArrayList<String> GUID = XML.parseStringAsNodesValue(responseString, "//MediaContainer/Directory/@guid");
            if (GUID == null || GUID.get(0) == null) {
                System.out.println("Error: series GUID was not found!");
                return null;
            }
            if (GUID.get(0) != null) {
                series.setGUID(Integer.parseInt(GUID.get(0).replaceAll("[^0-9]+", "")));
            }

            // Return output ArrayList
            return series;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static PlexSeries getEpisodeData(Header[] headers, PlexSeries series) {

        Log.print("Getting episode data for series: " + series.getTitle());

        // Create new custom HttpClient
        HttpClient httpClient = new CustomHttpClient().get();

        // Create new HttpGet object
        HttpGet getEpisodes = new HttpGet(Settings.getPLEX_SERVER_URL() + "/library/metadata/" + series.getRatingKey() + "/allLeaves");

        // Add all headers to HttpGet object
        for (int i = 0; i < headers.length; i++) {
            getEpisodes.addHeader(headers[i]);
        }

        try {
            // Get HTTP response
            HttpResponse response = httpClient.execute(getEpisodes);

            // Create new response handler
            ResponseHandler<String> handler = new BasicResponseHandler();
            String responseString = handler.handleResponse(response);

            // Get all episodes
            ArrayList<String> episodes = XML.parseStringAsNodesValue(responseString, "//MediaContainer/Video/@ratingKey");

            // Assert lists are not null
            assert (episodes != null);

            ArrayList<PlexEpisode> plexEpisodes = new ArrayList<PlexEpisode>();
            for (int j = 0; j < episodes.size(); j++) {
                PlexEpisode episode = new PlexEpisode();

                // Parse episode data
                ArrayList<String> title = XML.parseStringAsNodesValue(responseString, "//MediaContainer/Video[@ratingKey='" + episodes.get(j) + "']/@title");
                ArrayList<String> parentIndex = XML.parseStringAsNodesValue(responseString, "//MediaContainer/Video[@ratingKey='" + episodes.get(j) + "']/@parentIndex");
                ArrayList<String> index = XML.parseStringAsNodesValue(responseString, "//MediaContainer/Video[@ratingKey='" + episodes.get(j) + "']/@index");

                // Try to set episode title
                if (title != null && title.get(0) != null) {
                    episode.setTitle(title.get(0));
                }
                // TODO Make failure case

                    // Try to set episode parentIndex
                    if (parentIndex != null && parentIndex.get(0) != null) {
                        episode.setParentIndex(Integer.parseInt(parentIndex.get(0)));
                    }
                    // TODO Make failure case

                    // Try to set episode index
                    if (index != null && index.get(0) != null) {
                        episode.setIndex(Integer.parseInt(index.get(0)));
                    }

                    // Add episode to series
                    plexEpisodes.add(episode);
                }
                // Add all episodes to series
                series.setEpisodes(plexEpisodes);

            return series;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
