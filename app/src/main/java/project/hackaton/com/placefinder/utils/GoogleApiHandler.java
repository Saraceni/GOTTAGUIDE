package project.hackaton.com.placefinder.utils;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by rafaelgontijo on 10/26/15.
 */
public class GoogleApiHandler implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "ApiHandler";
    private static Location userLocation;

    private static final long FIFTEEN_MINS_MILLIS = 900000L;
    public static final String LOCATION_RECEIVED_INTENT = "LOCATION_RECEIVED_INTENT";

    private Context context;
    private static GoogleApiClient mGoogleApiClient;
    private static Location lastUsrLocation;

    public GoogleApiHandler(Context context)
    {
        this.context = context;
        initGoogleAPIClient();
    }

    private void initGoogleAPIClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void connectGoogleApiClient()
    {
        if(!mGoogleApiClient.isConnected())
        {
            mGoogleApiClient.connect();
        }
    }

    public void disconnectGoogleApiClient()
    {
        if(mGoogleApiClient.isConnected())
        {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(location != null)
        {
            lastUsrLocation = location;
            Intent nuIntent = new Intent();
            nuIntent.setAction(LOCATION_RECEIVED_INTENT);
            context.sendBroadcast(nuIntent);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static Location getLastKnownLocation()
    {
        return lastUsrLocation;
    }

    public static String getLocationString(Context ctx, Location location)
    {
        Geocoder geoCoder = new Geocoder(ctx, Locale.getDefault());
        StringBuilder builder = new StringBuilder();
        try {
            List<Address> address = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            int maxLines = address.get(0).getMaxAddressLineIndex();
            for (int i=0; i<maxLines; i++) {
                String addressStr = address.get(0).getAddressLine(i);
                builder.append(addressStr);
                builder.append(" ");
            }

            return builder.toString(); //This is the complete address.
        } catch (IOException e) {
            return null;
        }
        catch (NullPointerException e) {
            return null;
        }
    }
}
