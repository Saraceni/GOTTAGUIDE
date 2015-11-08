package project.hackaton.com.placefinder.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by rafaelgontijo on 11/8/15.
 */
public class PrefsH {

    private static final String PREFS_KEY = "appPrefs";
    private static final String ACCESS_TOKEN_KEY = "ACCESS_TOKEN_KEY";
    private static final String MAIN_JSON_KEY = "MAIN_JSON_KEY";

    public static final String NAME_KEY = "NAME_KEY";
    public static final String CITY_KEY = "CITY_KEY";
    public static final String LAT_KEY = "LAT_KEY";
    public static final String LNG_KEY = "LNG_KEY";
    public static final String COUNTRY_KEY = "COUNTRY_KEY";

    private static SharedPreferences cache;

    public static SharedPreferences getPrefs(Context ctx) {
        if (PrefsH.cache == null) {
            Context mainContext = ctx.getApplicationContext();
            SharedPreferences sharedPref = mainContext.getSharedPreferences(
                    PREFS_KEY, Context.MODE_PRIVATE);
            PrefsH.cache = sharedPref;
        }
        return PrefsH.cache;
    }

    public static boolean setMainJSON(Context ctx, String json)
    {
        SharedPreferences prefs = getPrefs(ctx);
        return prefs.edit().putString(MAIN_JSON_KEY, json).commit();
    }

    public static String getMainJSON(Context ctx)
    {
        SharedPreferences prefs = getPrefs(ctx);
        return prefs.getString(MAIN_JSON_KEY, null);
    }

    public static boolean setAccessToken(Context ctx, String accesssToken)
    {
        SharedPreferences prefs = getPrefs(ctx);
        return prefs.edit().putString(ACCESS_TOKEN_KEY, accesssToken).commit();
    }

    public static String getAccessToken(Context ctx)
    {
        SharedPreferences prefs = getPrefs(ctx);
        return prefs.getString(ACCESS_TOKEN_KEY, null);
    }

}
