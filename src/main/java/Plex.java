import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.json.*;
import javax.net.ssl.SSLContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

class Plex {
    static Header[] getHeaders() {

        // Merge username and password
        String userPass = Settings.getPLEX_SERVER_USERNAME() + ":" + Settings.getPLEX_SERVER_PASSWORD();

        // Convert Plex authentication information to base 64
        byte[] auth = Base64.encodeBase64(userPass.getBytes());

        // Create new HttpClient object
        HttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build();

        // Create new HttpPost object
        HttpPost httpPost = new HttpPost("https://plex.tv/users/sign_in.json");

        // Add Plex authorization header
        httpPost.addHeader("Authorization", "Basic " + new String(auth));

        // Add Plex client identifier header
        httpPost.addHeader("X-Plex-Client-Identifier", "plex-tv-helper");

        // Add Plex product header
        httpPost.addHeader("X-Plex-Product", "plex-tv-helper");

        // Add Plex version header
        httpPost.addHeader("X-Plex-Version", "0.1");

        // Get Plex token
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


            return httpPost.getAllHeaders();
        } catch (Exception e) {
            System.out.println("Error: cannot get Plex token!");
            e.printStackTrace();
        }
        return null;
    }

    static ArrayList<String> getTVMediaContainerKeys(Header[] headers) {

        HttpClient httpClient = new CustomHttpClient().get();

        System.out.println("Connecting to Plex Media Server...");

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

            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(new StringReader(responseString)));
                XPathFactory xPathfactory = XPathFactory.newInstance();
                XPath xpath = xPathfactory.newXPath();

                // Find all media types of "show" and get their keys
                XPathExpression expr = xpath.compile("//MediaContainer/Directory[@type='show']/@key");
                NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

                // Initialize ArrayList for output items
                ArrayList<String> output = new ArrayList<String>();

                // Output NodeList
                for (int i = 0; i < nl.getLength(); i++) {
                    output.add(nl.item(i).getNodeValue());
                }

                return output;

            } catch (Exception e) {
                e.printStackTrace();
        }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static ArrayList<String> getTVMediaShowKeys(Header[] headers, int key) {
        HttpClient httpClient = new CustomHttpClient().get();

        System.out.println("Getting show keys...");

        // Create new HttpGet object
        HttpGet getShowKeys = new HttpGet(Settings.getPLEX_SERVER_URL() + "/library/sections/" + key + "/all/");

        // Add all headers to HttpGet object
        for (int i = 0; i < headers.length; i++) {
            getShowKeys.addHeader(headers[i]);
        }

        try {
            // Get HTTP response
            HttpResponse response = httpClient.execute(getShowKeys);

            // Create new response handler
            ResponseHandler<String> handler = new BasicResponseHandler();
            String responseString = handler.handleResponse(response);

            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(new StringReader(responseString)));
                XPathFactory xPathfactory = XPathFactory.newInstance();
                XPath xpath = xPathfactory.newXPath();

                // Find all media types of "show" and get their keys
                XPathExpression expr = xpath.compile("//MediaContainer/Directory");
                NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

                // Initialize ArrayList for output items
                ArrayList<String> output = new ArrayList<String>();

                // Output NodeList
                for (int i = 0; i < nl.getLength(); i++) {
                    output.add(nl.item(i).getNodeValue());
                }

                return output;

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
