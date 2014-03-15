package com.logicdrop.fordchallenge.openxc.preferences;

import android.content.Context;
import android.util.Log;

import com.openxc.R;
import com.openxc.interfaces.bluetooth.BluetoothVehicleInterface;

/**
 * Enable or disable receiving vehicle data from a Bluetooth CAN device.
 */
public class BluetoothPreferenceManager extends VehiclePreferenceManager {
    private final static String TAG = "BluetoothPreferenceManager";

    public BluetoothPreferenceManager(Context context) {
        super(context);
    }

    protected PreferenceListener createPreferenceListener() {
        return new PreferenceListener() {
            private int[] WATCHED_PREFERENCE_KEY_IDS = {
                R.string.bluetooth_checkbox_key,
                R.string.bluetooth_mac_key,
            };

            protected int[] getWatchedPreferenceKeyIds() {
                return WATCHED_PREFERENCE_KEY_IDS;
            }

            public void readStoredPreferences() {
                setBluetoothStatus(getPreferences().getBoolean(
                            getString(R.string.bluetooth_checkbox_key), false));
            }
        };
    }

    private synchronized void setBluetoothStatus(boolean enabled) {
        Log.i(TAG, "Setting bluetooth data source to " + enabled);
        if(enabled) {
            String deviceAddress = getPreferenceString(
                    R.string.bluetooth_mac_key);
            if(deviceAddress != null ) {
                getVehicleManager().addVehicleInterface(
                        BluetoothVehicleInterface.class, deviceAddress);
            } else {
                Log.d(TAG, "No Bluetooth device MAC set yet (" + deviceAddress +
                        "), not starting source");
            }
        } else {
            getVehicleManager().removeVehicleInterface(
                    BluetoothVehicleInterface.class);
        }
    }
}
