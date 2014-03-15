package com.logicdrop.fordchallenge.openxc.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.openxc.R;
import com.openxc.interfaces.network.NetworkVehicleInterface;

/**
 * Enable or disable receiving vehicle data from a Network device
 */
public class NetworkPreferenceManager extends VehiclePreferenceManager {
    private final static String TAG = "NetworkPreferenceManager";

    public NetworkPreferenceManager(Context context) {
        super(context);
    }

    protected PreferenceListener createPreferenceListener() {
        return new PreferenceListener() {
            private int[] WATCHED_PREFERENCE_KEY_IDS = {
                R.string.network_checkbox_key,
                R.string.network_host_key,
                R.string.network_port_key,
            };

            protected int[] getWatchedPreferenceKeyIds() {
                return WATCHED_PREFERENCE_KEY_IDS;

            }
            public void readStoredPreferences() {
                setNetworkStatus(getPreferences().getBoolean(getString(
                                R.string.network_checkbox_key), false));
            }
        };
    }

    private void setNetworkStatus(boolean enabled) {
        Log.i(TAG, "Setting network data source to " + enabled);
        if(enabled) {
            String address = getPreferenceString(R.string.network_host_key);
            String port = getPreferenceString(R.string.network_port_key);
            String combinedAddress = address + ":" + port;

            if(address == null || port == null ||
                    !NetworkVehicleInterface.validateResource(
                        combinedAddress)) {
                String error = "Network host URI (" + combinedAddress +
                    ") not valid -- not starting network data source";
                Log.w(TAG, error);
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                SharedPreferences.Editor editor = getPreferences().edit();
                editor.putBoolean(getString(R.string.uploading_checkbox_key),
                        false);
                editor.commit();
            } else {
                getVehicleManager().addVehicleInterface(
                        NetworkVehicleInterface.class, combinedAddress);
            }
        } else {
            getVehicleManager().removeVehicleInterface(
                    NetworkVehicleInterface.class);
        }
    }
}
