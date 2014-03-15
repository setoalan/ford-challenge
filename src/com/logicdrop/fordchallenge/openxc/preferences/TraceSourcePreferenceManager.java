package com.logicdrop.fordchallenge.openxc.preferences;

import android.content.Context;
import android.util.Log;

import com.openxc.R;
import com.openxc.sources.DataSourceException;
import com.openxc.sources.trace.TraceVehicleDataSource;

/**
 * Enable or disable receiving vehicle data from a pre-recorded trace file.
 */
public class TraceSourcePreferenceManager extends VehiclePreferenceManager {
    private final static String TAG = "TraceSourcePreferenceManager";

    private TraceVehicleDataSource mTraceSource;

    public TraceSourcePreferenceManager(Context context) {
        super(context);
    }

    public void close() {
        super.close();
        stopTrace();
    }

    protected PreferenceListener createPreferenceListener() {
        return new PreferenceListener() {
            private int[] WATCHED_PREFERENCE_KEY_IDS = {
                R.string.trace_source_checkbox_key,
                R.string.trace_source_file_key,
            };

            protected int[] getWatchedPreferenceKeyIds() {
                return WATCHED_PREFERENCE_KEY_IDS;
            }

            public void readStoredPreferences() {
                setTraceSourceStatus(getPreferences().getBoolean(
                        getString(R.string.trace_source_checkbox_key), false));
            }
        };
    }

    private synchronized void setTraceSourceStatus(boolean enabled) {
        Log.i(TAG, "Setting trace data source to " + enabled);
        if(enabled) {
            String traceFile = getPreferenceString(
                    R.string.trace_source_file_key);
            if(traceFile != null ) {
                if(mTraceSource == null ||
                        !mTraceSource.sameFilename(traceFile)) {
                    stopTrace();

                    try {
                        mTraceSource = new TraceVehicleDataSource(
                                getContext(), traceFile);
                    } catch(DataSourceException e) {
                        Log.w(TAG, "Unable to add Trace source", e);
                        return;
                    }
                    getVehicleManager().addSource(mTraceSource);
                } else {
                    Log.d(TAG, "Trace file + " + traceFile + " already playing");
                }
            } else {
                Log.d(TAG, "No trace file set yet (" + traceFile +
                        "), not starting playback");
            }
        } else {
            stopTrace();
        }
    }

    private synchronized void stopTrace() {
        getVehicleManager().removeSource(mTraceSource);
        mTraceSource = null;
    }
}
