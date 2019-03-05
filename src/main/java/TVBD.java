import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import java.util.ArrayList;

public class TVBD {
    public static Header[] getHeaders() {
        // Create new HttpClient object
        HttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build();

        // Create new HttpPost object
        HttpPost httpPost = new HttpPost("https://api.thetvdb.com/login");

        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("apikey", Settings.getTVDB_API_KEY()));
        postParameters.add(new BasicNameValuePair("userkey", Settings.getTVDB_USERKEY()));
        postParameters.add(new BasicNameValuePair("username", Settings.getTVDB_USERNAME()));

        // Create new StringEntity objecy for JSON request
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

            // Return token
            return httpPost.getAllHeaders();

        } catch (Exception e) {
            System.out.println("Error: cannot get TVDB token!");
            e.printStackTrace();
        }
        return null;
    }
}
