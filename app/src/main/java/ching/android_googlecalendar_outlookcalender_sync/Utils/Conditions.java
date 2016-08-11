package ching.android_googlecalendar_outlookcalender_sync.Utils;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import ching.android_googlecalendar_outlookcalender_sync.MainActivity;


/**
 * Created by book871181 on 16/6/1.
 *
 *  Call in the UI Thread
 *  Make sure google's conditions is OK
 *
 *
 */
public class Conditions   {
    Context mContext;
    public Conditions(Context mContext){
        this.mContext = mContext;
    }

    public boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }




    /**
     * Helper class for verifying that the Google Play services APK is available and up-to-date on this device.
     * */
    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(mContext);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    public void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(mContext);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);

        }
    }

    MainActivity mMainActivity;

    public  void showGooglePlayServicesAvailabilityErrorDialog(int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                mMainActivity,
                connectionStatusCode,
                Environment.REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

}
