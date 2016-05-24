package ie.lero.chintucloud.openstack.lerocloudapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.hardware.fingerprint.FingerprintManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@SuppressWarnings("ResourceType")
public class MyActivity extends Activity {

    String SUC;
    public final static String EXTRA_MESSAGE = "ie.lero.chintucloud.openstack.lerocloudapp.MESSAGE";
    //String DeviceType = getDeviceType();
    public String WiFiName;
    public String NetworkType;
    public Location coordinates;
    public String address;
    public double GPS[];
    public double lati, longi, latitude, longitude;
    public boolean isLocationAvailable = false;
    public String message;
    public String AUTH_METHOD = "";
    JSONObject JUC;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ContextInfo CI = new ContextInfo(this);

        final TextView CTV = (TextView) findViewById(R.id.ContextText);
        GPSService LOC = new GPSService(this);
        address = LOC.getLocationAddress();
        coordinates = LOC.getLocation();
        NetworkType = getNetworktype();
        WiFiName = getWIFIName();
        //GPS = getGPS();
        //lati = GPS[0];
        //longi = GPS[1];
        final GPSTracker gps = new GPSTracker(this);

// check if GPS enabled
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        } else {
// can't get location
// GPS or Network is not enabled
// Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
        Button RefreshButton = (Button) findViewById(R.id.RefreshButton);
        RefreshButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GPSTracker gps1 = new GPSTracker(MyActivity.this);
                if (gps1.canGetLocation()) {
                    latitude = gps1.getLatitude();
                    longitude = gps1.getLongitude();
                    NetworkType = getNetworktype();

                    try {
                        JSONObject JUC = getUserContextJSON();
                        CTV.setText(JUC.getString("USERCONTEXT"));
                        CONNECT C = new CONNECT();
                        C.execute(JUC);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
// can't get location
// GPS or Network is not enabled
// Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }
            }
        });

        //SUC = " { \"USERCONTEXT\" : { \"PHYSICAL LOCATION\":\"" + address + "\",\"NETWORK TYPE\":\"" + NetworkType + "\",\"DEVICE TYPE\":\"" + getDeviceType() + "\"}}";
        try {
            JSONObject JUC = getUserContextJSON();
            CTV.setText(JUC.getString("USERCONTEXT"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public void Authenticate(View view) throws JSONException {
        if (AUTH_METHOD == null) {
            AlertDialog alertDialog = new AlertDialog.Builder(MyActivity.this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage("Click refresh button first!!!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
        if (AUTH_METHOD.equalsIgnoreCase("OPEN_ACCESS")) {
            Intent intent = new Intent(this, Authenticate.class);
            intent.putExtra(EXTRA_MESSAGE, AUTH_METHOD);
            startActivity(intent);
        } else if(AUTH_METHOD.equalsIgnoreCase("PWD_ACCESS")){
            Intent intent = new Intent(this, AuthenticatePWD.class);
            intent.putExtra(EXTRA_MESSAGE, AUTH_METHOD);
            startActivity(intent);
        } else if(AUTH_METHOD.equalsIgnoreCase("TWOFACTOR_ACCESS")){
            Intent intent = new Intent(this, TwoFacAnthenticate.class);
            intent.putExtra(EXTRA_MESSAGE, AUTH_METHOD);
            startActivity(intent);
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("click refresh button first!!!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    }


    public JSONObject getUserContextJSON() throws JSONException {
        SUC = " { \"USERCONTEXT\" : { \"PHYSICAL_LOCATION\":\"" + whereAmI(latitude, longitude) + "\",\"LATITUDE\":\"" + latitude + "\",\"LONGITUDE\":\"" + longitude + "\",\"NETWORK_TYPE\":\"" + NetworkType + "\",\"DEVICE_TYPE\":\"" + getDeviceType() + "\"}}";
        //SUC = " { \"USERCONTEXT\" : { \"PHYSICAL_LOCATION\":\"HOME\",\"LATITUDE\":\"" + latitude + "\",\"LONGITUDE\":\"" + longitude + "\",\"NETWORK_TYPE\":\"" + NetworkType + "\",\"DEVICE_TYPE\":\"" + getDeviceType() + "\"}}";
        JSONObject JUC = new JSONObject(SUC);
        return JUC;
    }


    public String getNetworktype() {
        final ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnectedOrConnecting()) {
            return "WiFi";
        } else if (mobile.isConnectedOrConnecting()) {
            return "MOBILE_DATA";
        } else {
            return "NO_NETWORK";
        }
    }


    public String getWIFIName() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        //Log.d("wifiInfo", wifiInfo.toString());
        //Log.d("NetworkId",wifiInfo.getNetworkId());

        return (wifiInfo.getSSID());
    }


    public boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }


    private String getDeviceType() {
        if (isTablet(this))
            return "ANDROID_TABLET";
        else
            return "ANDROID_PHONE";
    }


    private double[] getGPS() {
        LocationManager lm = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);

        Location l = null;

        for (int i = providers.size() - 1; i >= 0; i--) {
            l = lm.getLastKnownLocation(providers.get(i));
            if (l != null) break;
        }

        double[] gps = new double[2];
        if (l != null) {
            gps[0] = l.getLatitude();
            gps[1] = l.getLongitude();
        }
        isLocationAvailable = true;
        return gps;
    }


    public String getLocationAddress() {

        if (isLocationAvailable) {

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            // Get the current location from the input parameter list
            // Create a list to contain the result address
            List<Address> addresses = null;
            try {
                /*
                 * Return 1 address.
				 */
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException e1) {
                e1.printStackTrace();
                return ("IO Exception trying to get address:" + e1);
            } catch (IllegalArgumentException e2) {
                // Error message to post in the log
                String errorString = "Illegal arguments "
                        + Double.toString(latitude) + " , "
                        + Double.toString(longitude)
                        + " passed to address service";
                e2.printStackTrace();
                return errorString;
            }
            // If the reverse geocode returned an address
            if (addresses != null && addresses.size() > 0) {
                // Get the first address
                Address address = addresses.get(0);
                /*
                 * Format the first line of address (if available), city, and
				 * country name.
				 */
                String addressText = String.format(
                        "%s, %s, %s",
                        // If there's a street address, add it
                        address.getMaxAddressLineIndex() > 0 ? address
                                .getAddressLine(0) : "",
                        // Locality is usually a city
                        address.getLocality(),
                        // The country of the address
                        address.getCountryName());
                // Return the text
                return addressText;
            } else {
                return "No address found by the service: Note to the developers, If no address is found by google itself, there is nothing you can do about it.";
            }
        } else {
            return "Location Not available";
        }

    }


    public String whereAmI(double latitude, double longitude) {
        if (latitude >= 52.674018 && latitude <= 52.675274 && longitude >= -8.578253 && longitude <= -8.575982) {
            return "LERO";
        } else if (latitude >= 52.664639 && latitude <= 52.666923 && longitude >= -8.559796 && longitude <= -8.554840) {
            return "HOME";
        } else
            return "OUTSIDE";
    }


    public class CONNECT extends AsyncTask<JSONObject, Void, String> {
        @Override
        protected String doInBackground(JSONObject... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                Response response;
                SUC = " { \"USERCONTEXT\" : { \"PHYSICAL_LOCATION\":\"" + whereAmI(latitude, longitude) + "\",\"LATITUDE\":\"" + latitude + "\",\"LONGITUDE\":\"" + longitude + "\",\"NETWORK_TYPE\":\"" + NetworkType + "\",\"DEVICE_TYPE\":\"" + getDeviceType() + "\"}}";
                //SUC = " { \"USERCONTEXT\" : { \"PHYSICAL_LOCATION\":\"HOME\",\"LATITUDE\":\"" + latitude + "\",\"LONGITUDE\":\"" + longitude + "\",\"NETWORK_TYPE\":\"" + NetworkType + "\",\"DEVICE_TYPE\":\"" + getDeviceType() + "\"}}";
                RequestBody body = RequestBody.create(JSON, SUC);
                String url = "http://193.1.97.18:9999/login";
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                try {
                    response = client.newCall(request).execute();
                    if (!response.isSuccessful()) {
                        return null;
                    } else {
                        return (response.body().string());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String AuthMethod) {
            if (AuthMethod != null) {
                TextView CTV = (TextView) findViewById(R.id.ContextText);
                CTV.append("\n\nChosen Authentication method is: " + AuthMethod);
                AUTH_METHOD = AuthMethod;
            }
        }


    }
}