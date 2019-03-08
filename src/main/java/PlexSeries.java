import org.json.JSONArray;

import java.util.ArrayList;

class PlexSeries {
    private String title;
    private int ratingKey;
    private int GUID;
    private ArrayList<PlexEpisode> episodes = new ArrayList<PlexEpisode>();
    private JSONArray TVDBSeriesData = new JSONArray();

    String getTitle() {
        return title;
    }

    void setTitle(String input) {
        title = input;
    }

    int getRatingKey() {
        return ratingKey;
    }

    void setRatingKey(int input) {
        ratingKey = input;
    }

    ArrayList<PlexEpisode> getEpisodes() {
        return episodes;
    }

    void setEpisodes(ArrayList<PlexEpisode> input) {
        episodes = input;
    }

    JSONArray getTVDBSeriesData() {
        return TVDBSeriesData;
    }

    void setTVDBSeriesData(JSONArray input) {
        TVDBSeriesData = input;
    }

    int getGUID() {
        return GUID;
    }

    void setGUID(int input) {
        GUID = input;
    }

    JSONArray getMissingEpisodes() {
        Log.print("Finding missing episodes for series: " + this.getTitle());

        // Initialize missing episodes JSON array
        JSONArray missingEpisodes = this.getTVDBSeriesData();
        int missingCount = this.getTVDBSeriesData().length() - this.getEpisodes().size();

        Log.print("Plex episodes for this series: " + this.getEpisodes().size());
        Log.print("TVDB episodes for this series: " + missingEpisodes.length());
        Log.print("Missing episodes for this series: " + missingCount);


        // Check if series has episode data
        if (this.getEpisodes() == null || this.getEpisodes().isEmpty() || missingEpisodes.isEmpty()) {
            Log.print("Series has no data!");
            return new JSONArray();
        }

        // Check if there are more episodes in Plex than TVDB
        if (this.getEpisodes().size() > missingEpisodes.length()) {
            Log.print("Series has more Plex episodes than episodes on TVDB!");
            return new JSONArray();
        }

        if (this.getEpisodes().size() == missingEpisodes.length()) {
            Log.print("Series has no missing episodes!");
            return new JSONArray();
        }

        // Loop through JSON array

            for (int i = 0; i < this.getEpisodes().size(); i++) {
                // Loop through Plex episodes
                for (int j = 0; j < missingEpisodes.length(); j++) {
                    if (this.getEpisodes().get(i).getTitle().equalsIgnoreCase(missingEpisodes.getJSONObject(j).get("episodeName").toString())) {
                        Log.print("Matched episode: \"" + missingEpisodes.getJSONObject(j).getString("episodeName") + "\"");

                        // Remove JSON object from missing episodes
                        missingEpisodes.remove(i);
                    }
                }
            }

            System.out.println("Found " + missingEpisodes.length() + " missing episodes in series: \n" + this.getTitle() + "\n" );

        return missingEpisodes;
    }
}
