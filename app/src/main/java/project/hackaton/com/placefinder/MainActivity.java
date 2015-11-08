package project.hackaton.com.placefinder;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import project.hackaton.com.placefinder.utils.PrefsH;

public class MainActivity extends AppCompatActivity {

    // Views
    private TextView locationTextView;
    private AutoCompleteTextView locationInputEditText;
    private Button submitButton;


    private LocationManager locationManager;
    private String accessToken;
    private SearchLocationInBackground searchLocationInBackground;
    private ArrayAdapter<String> adapter;
    private List<String> results;
    private String userCountryCode;
    private double latitude;
    private double longitude;




    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.
            //makeUseOfNewLocation(location);
            String[] s_location = getLocationString(MainActivity.this, location);
            //userCountryCode = s_location[0];
            if(location != null)
            {
                locationTextView.setText(s_location[1]);
                locationManager.removeUpdates(locationListener);
            }
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationTextView = (TextView) findViewById(R.id.activity_main_tv_location);
        results = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, results);
        locationInputEditText = (AutoCompleteTextView) findViewById(R.id.activity_main_et_entry);
        locationInputEditText.setAdapter(adapter);
        locationInputEditText.setThreshold(1);
        locationInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Logic goes here
                if (accessToken == null) {
                    return;
                }

                if (searchLocationInBackground != null) {
                    searchLocationInBackground.cancel(true);
                }

                searchLocationInBackground = new SearchLocationInBackground();
                searchLocationInBackground.execute(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        submitButton = (Button) findViewById(R.id.activity_main_bt_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = locationInputEditText.getText().toString();
                String countryCode = text.substring(text.length() - 2);
                text = text.substring(0, text.length() - 4);
                Log.i("RAFA", text);
                Log.i("RAFA", countryCode);
                try {
                    JSONObject json = new JSONObject();
                    json.put(PrefsH.CITY_KEY, text);
                    json.put(PrefsH.LAT_KEY, latitude);
                    json.put(PrefsH.LNG_KEY, longitude);
                    json.put(PrefsH.COUNTRY_KEY, countryCode);
                    PrefsH.setMainJSON(MainActivity.this, json.toString());
                    Intent intent = new Intent(MainActivity.this, SelectionActivity.class);
                    startActivity(intent);
                }
                catch(Exception exc)
                {

                }
            }
        });

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //requestLocationUpdate();
        new GetAccessTokenInBackground().execute("");

    }

    private void requestLocationUpdate()
    {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy()
    {
        //unregisterReceiver(bgSendingReceiver);
        super.onDestroy();
    }

    public static String[] getLocationString(Context ctx, Location location)
    {
        Geocoder geoCoder = new Geocoder(ctx, Locale.getDefault());
        try {
            List<Address> address = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            //return builder.toString(); //This is the complete address.
            String[] result = new String[2];
            result[0] = address.get(0).getCountryCode();
            result[1] = address.get(0).getLocality();
            return result;
        } catch (IOException e) {
            return null;
        }
        catch (NullPointerException e) {
            return null;
        }
    }

    public static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }

    private class SearchLocationInBackground extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... URLs)
        {
            try {
                String arg = URLs[0].replace(" ", "%2520");
                //String query = URLEncoder.encode(arg, "utf-8");
                String uri = "https://api.test.sabre.com/v1/lists/utilities/geoservices/autocomplete?query=" +
                        arg + "&category=CITY";
                URL url = new URL(uri);
                HttpURLConnection connection =
                        (HttpURLConnection) url.openConnection();
                if(connection == null)
                {
                    Log.i("RAFA", "connection == null");
                }
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + accessToken);
                int status = connection.getResponseCode();
                Log.i("RAFA", String.valueOf(status));
                InputStream data = connection.getInputStream();
                String s_data = getStringFromInputStream(data);
                Log.i("RAFA", s_data);

                connection.disconnect();

                return s_data;
            }
            catch(Exception exc)
            {
                Log.i("RAFA", "Exception: " + exc.toString());
                return null;
            }
        }

        protected void onPostExecute(String data) {
            Log.i("RAFA", "onPostExecute");
            adapter.clear();
            if(data != null)
            {
                try
                {
                    JSONObject jsonData = new JSONObject(data);
                    JSONArray jsonArray = jsonData.getJSONObject("Response").getJSONObject("grouped").getJSONObject("category:CITY")
                            .getJSONObject("doclist").getJSONArray("docs");
                    //results.clear();
                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        Log.i("RAFA", obj.getString("name"));
                        userCountryCode = obj.getString("country");
                        latitude = obj.getDouble("latitude");
                        longitude = obj.getDouble("longitude");
                        //results.add(obj.getString("name"));
                        adapter.add(obj.getString("name") + ", " + obj.getString("country"));
                    }
                }
                catch(Exception exc)
                {
                    Log.i("RAFA", exc.toString());
                }

                searchLocationInBackground = null;
            }
            adapter.notifyDataSetChanged();
        }
    }

    private class GetAccessTokenInBackground extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... URLs)
        {
            try {
                String uri = "https://api.test.sabre.com/v2/auth/token";
                URL url = new URL(uri);
                HttpURLConnection connection =
                        (HttpURLConnection) url.openConnection();
                if(connection == null)
                {
                    Log.i("RAFA", "connection == null");
                }
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Basic VmpFNmJURmlZV2RtT0hGcU5IcHJlREpwZGpwRVJWWkRSVTVVUlZJNlJWaFU6ZDNsalMwUTBOMVU9");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                DataOutputStream output = new DataOutputStream(connection.getOutputStream());


                output.writeBytes("grant_type=client_credentials");

                output.close();
                int status = connection.getResponseCode();
                Log.i("RAFA", String.valueOf(status));
                InputStream data = connection.getInputStream();
                String s_data = getStringFromInputStream(data);
                Log.i("RAFA", s_data);

                connection.disconnect();

                return s_data;
            }
            catch(Exception exc)
            {
                Log.i("RAFA", "Exception: " + exc.toString());
                return null;
            }
        }

        protected void onPostExecute(String data) {
            if(data != null)
            {
                try
                {
                    JSONObject jsonData = new JSONObject(data);
                    accessToken = jsonData.getString("access_token");
                    PrefsH.setAccessToken(MainActivity.this, accessToken);
                    Log.i("RAFA", "access token = " + accessToken);
                }
                catch(Exception exc)
                {
                    Log.i("RAFA", exc.toString());
                }
            }
        }

    }
}
