public class Settings {
    private static String PLEX_SERVER_ADDRESS = "";
    private static int PLEX_SERVER_PORT = 32400;
    private static String PLEX_SERVER_USERNAME = "";
    private static String PLEX_SERVER_PASSWORD = "";
    private static boolean SSL = true;
    private static String TVDB_API_KEY = "";
    private static String TVDB_USERKEY = "";
    private static String TVDB_USERNAME = "";

    /**
     * Get Plex Server address
     * @return  address
     */
    public static String getPLEX_SERVER_ADDRESS() {
        return PLEX_SERVER_ADDRESS;
    }

    /**
     * Get Plex Server port
     * @return  port
     */
    public static int getPLEX_SERVER_PORT() {
        return PLEX_SERVER_PORT;
    }

    /**
     * Get Plex Server username
     * @return  username
     */
    public static String getPLEX_SERVER_USERNAME() {
        return PLEX_SERVER_USERNAME;
    }

    /**
     * Get Plex Server password
     * @return  password
     */
    public static String getPLEX_SERVER_PASSWORD() {
        return PLEX_SERVER_PASSWORD;
    }

    /**
     * Return SSL status
     * @return  SSL status
     */
    public static boolean ifSSL() {
        return SSL;
    }

    /**
     * Get TVDB API key
     * @return  API key
     */
    public static String getTVDB_API_KEY() {
        return TVDB_API_KEY;
    }

    /**
     * Get TVDB userkey
     * @return  userkey
     */
    public static String getTVDB_USERKEY() {
        return TVDB_USERKEY;
    }

    /**
     * Get TVDB username
     * @return  username
     */
    public static String getTVDB_USERNAME() {
        return TVDB_USERNAME;
    }

    /**
     * Get plex server URL
     * @return  URL
     */
    public static String getPLEX_SERVER_URL() {
        if (SSL) {
            return "https://" + PLEX_SERVER_ADDRESS + ":" + PLEX_SERVER_PORT;
        }

        return "http://" + PLEX_SERVER_ADDRESS + ":" + PLEX_SERVER_PORT;
    }
}
