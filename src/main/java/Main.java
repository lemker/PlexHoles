import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;

import static java.lang.System.exit;

class Main {
    public static void main(String[] args) {
        Log.print("Starting Main...");

        // Get Plex headers
        Header[] plexHeaders = Plex.getHeaders();

        // Check for Plex headers
        if (plexHeaders == null) {
            System.out.println("Error: cannot get Plex header information!");
            exit(0);
        } else {

            // Get TVDB token
            Header[] TVDBHeaders = TVDB.getHeaders();

            // Check for TVDB token
            if (TVDBHeaders == null) {
                Log.print("Error: cannot get TVDB header information!");
                exit(0);
            } else {
                // Find all media container keys that are of type "show"
                ArrayList<String> TVMediaContainerKeys = Plex.getTVMediaContainerKeys(plexHeaders);

                // Check if there are no TV keys in library
                if (TVMediaContainerKeys == null || TVMediaContainerKeys.isEmpty()) {
                    Log.print("Could not find any TV media containers!");
                    exit(0);
                } else {
                    for (int i = 0; i < TVMediaContainerKeys.size(); i++) {
                        Log.print("Found the following media container keys: " + TVMediaContainerKeys.get(i));

                        // Find all TV show keys
                        ArrayList<String> ratingKeys = Plex.getRatingKeys(plexHeaders, Integer.parseInt(TVMediaContainerKeys.get(i)));

                        if (ratingKeys == null || ratingKeys.isEmpty()) {
                            Log.print("Could not find rating keys!");
                            exit(0);
                        } else {
                            System.out.println("Found " + ratingKeys.size() + " unique keys!");

                            // Initialize ArrayList for series
                            ArrayList<PlexSeries> plexSeries = new ArrayList<PlexSeries>();

                            // Loop through all rating keys
                            for (int j = 0; j < ratingKeys.size(); j++) {
                                plexSeries.add(Plex.getSeriesData(plexHeaders, Integer.parseInt(ratingKeys.get(j))));
                                System.out.println("Found show data: " + plexSeries.get(j).getTitle() + " | Rating key: " + plexSeries.get(j).getRatingKey() + " | GUID: " + plexSeries.get(j).getGUID());
                            }
                            if (plexSeries.isEmpty()) {
                                Log.print("No show data was found!");
                                exit(0);
                            } else {
                                for (int k = 0; k < plexSeries.size(); k++) {
                                    // Get episode data
                                    Plex.getEpisodeData(plexHeaders, plexSeries.get(k));

                                    // Get series data from TVDB
                                    TVDB.getSeries(TVDBHeaders, plexSeries.get(k));

                                    // Get missing episodes
                                    plexSeries.get(k).getMissingEpisodes();
                                }

                                for (int g = 0; g < plexSeries.size(); g++) {

                                    JSONArray missingEpisodes = plexSeries.get(g).getMissingEpisodes();
                                    for (int h = 0; h < missingEpisodes.length(); h++) {
                                        System.out.println(plexSeries.get(g).getTitle() + " - " + missingEpisodes.getJSONObject(h).getString("episodeName"));
                                    }
                                }



                            }



                            }
                        }
                    }
                }
            }
        }
    }

