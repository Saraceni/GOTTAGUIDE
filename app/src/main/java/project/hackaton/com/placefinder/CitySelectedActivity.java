package project.hackaton.com.placefinder;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import project.hackaton.com.placefinder.object.Destination;
import project.hackaton.com.placefinder.object.DestinationAdapter;
import project.hackaton.com.placefinder.utils.PrefsH;

/**
 * Created by rafaelgontijo on 11/8/15.
 */
public class CitySelectedActivity extends Activity {

    // Views
    private TextView textViewLabel;
    private ListView listView;

    private String countryCode;
    private String cityName;
    private double latitude;
    private double longitude;
    private DestinationAdapter adapter;
    private List<Destination> destinationList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_selected);

        Intent intent = getIntent();
        //countryCode = intent.getStringExtra("country");
        //cityName = intent.getStringExtra("text");
        //latitude = intent.getDoubleExtra("latitude", 0f);
        //longitude = intent.getDoubleExtra("longitude", 0f);

        try {
            String jsonString = PrefsH.getMainJSON(this);
            JSONObject json = new JSONObject(jsonString);
            countryCode = json.getString(PrefsH.COUNTRY_KEY);
            cityName = json.getString(PrefsH.CITY_KEY);
            latitude = json.getDouble(PrefsH.LAT_KEY);
            longitude = json.getDouble(PrefsH.LNG_KEY);
        }
        catch(Exception exc)
        {

        }

        textViewLabel = (TextView) findViewById(R.id.activity_city_selected_label);
        textViewLabel.setText(cityName);

        listView = (ListView) findViewById(R.id.activity_city_selected_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CitySelectedActivity.this, GuideRequestActivity.class);
                intent.putExtra("destination", destinationList.get(position).getName());
                startActivity(intent);
            }
        });

        destinationList = new ArrayList<>();
        adapter = new DestinationAdapter(this, destinationList);

        listView.setAdapter(adapter);


        new SearchTopLocationsInBackground().execute("");

    }

    private class SearchTopLocationsInBackground extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... URLs)
        {
            try {
                //https://api.test.sabre.com/v1/lists/top/destinations?destinationcountry=NYC&lookbackweeks=8&topdestinations=5
                String uri = "https://api.test.sabre.com/v1/lists/utilities/geosearch/locations";
                URL url = new URL(uri);
                HttpURLConnection connection =
                        (HttpURLConnection) url.openConnection();
                if(connection == null)
                {
                    Log.i("RAFA", "connection == null");
                }
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Bearer " + PrefsH.getAccessToken(CitySelectedActivity.this));
                connection.setRequestProperty("Content-Type", "application/json");
                //connection.setRequestProperty("Accept-Encoding", "gzip");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                JSONObject obj = new JSONObject();
                obj.put("name", "MapQuest:TOURIST ATTRACTION:TOURIST ATTRACTION");

                JSONArray ofCategory = new JSONArray();
                ofCategory.put(0, obj);

                JSONObject forPlaces = new JSONObject();
                forPlaces.put("OfCategory",ofCategory);

                JSONObject resultSetConfig = new JSONObject();
                resultSetConfig.put("maxResults", 20);

                /*JSONObject placeByAddress = new JSONObject();
                placeByAddress.put("City", cityName);
                placeByAddress.put("Country", countryCode);
                placeByAddress.put("Zip", "20750");*/

                JSONObject placeByLatLong = new JSONObject();
                placeByLatLong.put("latitude", latitude);
                placeByLatLong.put("longitude", longitude);

                JSONObject aroundObject = new JSONObject();
                //aroundObject.put("PlaceByAddress", placeByAddress);
                aroundObject.put("PlaceByLatLong", placeByLatLong);
                aroundObject.put("distance", 80);
                aroundObject.put("distanceUnit", "KM");

                // Main Level
                JSONObject geoSearchRQ = new JSONObject();
                geoSearchRQ.put("Around", aroundObject);
                geoSearchRQ.put("ResultSetConfig", resultSetConfig);
                geoSearchRQ.put("ForPlaces", forPlaces);

                JSONObject topLevel = new JSONObject();
                topLevel.put("GeoSearchRQ", geoSearchRQ);


                DataOutputStream output = new DataOutputStream(connection.getOutputStream());


                output.writeBytes(topLevel.toString());

                output.close();

                int status = connection.getResponseCode();
                Log.i("RAFA", String.valueOf(status));
                InputStream data = connection.getInputStream();
                String s_data = MainActivity.getStringFromInputStream(data);
                //Log.i("RAFA", s_data);

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
            if(data != null)
            {
                try
                {
                    JSONObject jsonData = new JSONObject(data);
                    JSONArray jsonArray = jsonData.getJSONObject("GeoSearchRS").getJSONObject("Found").getJSONArray("Place");
                    //results.clear();
                    destinationList.clear();
                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        Destination dest = new Destination();
                        dest.setName(obj.getString("Name"));
                        dest.setCountry(obj.getString("Country"));
                        dest.setCity(obj.getString("City"));
                        dest.setState(obj.getString("State"));
                        destinationList.add(dest);
                        //Log.i("RAFA", obj.getString("Name"));
                        //results.add(obj.getString("name"));
                    }
                    adapter.notifyDataSetChanged();
                    //accessToken = jsonData.getString("access_token");
                }
                catch(Exception exc)
                {
                    Log.i("RAFA", exc.toString());
                }

            }
        }
    }
}
