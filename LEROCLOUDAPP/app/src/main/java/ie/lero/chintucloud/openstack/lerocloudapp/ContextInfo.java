package ie.lero.chintucloud.openstack.lerocloudapp;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chintu on 07/04/16.
 */
public class ContextInfo {
    Context mContext;
    GPSService PhysicalLocation;
    JSONObject UserContext;
    //String DeviceType = getDeviceType();
    //public String WiFiName = getWIFIName();
    //String NetworkType = getNetworktype();
    Location coordinates = PhysicalLocation.getLocation();
    String address = PhysicalLocation.getLocationAddress();

    public JSONObject getUserContext() throws JSONException {
        //UserContext.put("Device Type", DeviceType);
        //UserContext.put("Network Type", NetworkType);
        //if (NetworkType == "WiFi")
        //UserContext.put("Wifi Name", WiFiName);
        UserContext.put("Physical Location", address);

        return UserContext;
    }

    public ContextInfo(Context mContext) {
        this.mContext = mContext;
    }


   /* public String getNetworktype() {
        final ConnectivityManager connMgr = (ConnectivityManager)
                Context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnectedOrConnecting()) {
            return "WiFi";
        } else if (mobile.isConnectedOrConnecting()) {
            return "Mobile Data";
        } else {
            return "No Network";
        }
    }*/



    /*public String getWIFIName() {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        //Log.d("wifiInfo", wifiInfo.toString());
        //Log.d("NetworkId",wifiInfo.getNetworkId());

        return (wifiInfo.getSSID());
    }*/

   /* private String getDeviceType() {
        if ()
            return "Android TABLET";
        else
            return "Android Phone";
    }*/


}
