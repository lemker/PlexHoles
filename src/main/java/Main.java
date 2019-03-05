import org.apache.http.Header;
import java.util.ArrayList;

class Main {
    public static void main(String[] args) {
        System.out.println("Starting Main...");

        // Get Plex headers
        Header[] plexHeaders = Plex.getHeaders();

        // Check for Plex headers
        if (plexHeaders == null) {
            System.out.println("Error: cannot get Plex header information!");
        } else {

            System.out.println("Got Plex token!");

            // Get TVDB token
            Header[] TVDBHeaders = TVBD.getHeaders();

            // Check for TVDB token
            if (TVDBHeaders == null) {
                System.out.println("Error: cannot get TVDB token!");
            } else {
                System.out.println("Got TVDB token!");
                // Find all media container keys that are of type "show"
                ArrayList<String> TVMediaContainerKeys = Plex.getTVMediaContainerKeys(plexHeaders);

                // Check if there are no TV keys in library
                if (TVMediaContainerKeys == null || TVMediaContainerKeys.isEmpty()) {
                    System.out.println("Could not find any TV media containers!");
                } else {

                    for (int i = 0; i < TVMediaContainerKeys.size(); i++) {
                        System.out.println("Found the following media container keys: " + TVMediaContainerKeys.get(i));

                        // Find all TV show keys
                        ArrayList<String> TVMediaShowKeys = Plex.getTVMediaShowKeys(plexHeaders, i);
                        for (int j = 0; i < TVMediaShowKeys.size(); j++) {
                            System.out.println("Found show with key: " + TVMediaShowKeys.get(j));
                        }
                    }


                }
            }
        }
    }
}