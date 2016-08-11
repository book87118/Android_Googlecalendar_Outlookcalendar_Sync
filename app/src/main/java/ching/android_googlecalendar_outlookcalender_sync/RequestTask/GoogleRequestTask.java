package ching.android_googlecalendar_outlookcalender_sync.RequestTask;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ching.android_googlecalendar_outlookcalender_sync.MainActivity;
import ching.android_googlecalendar_outlookcalender_sync.Utils.Environment;

/**
 * Created by book871181 on 16/8/8.
 */
public class GoogleRequestTask extends AsyncTask<Void, Void, List<String>> {
    static final String TAG = "GoogleRequestTask";
    private com.google.api.services.calendar.Calendar mService = null;
    private Exception mLastError = null;
    private MainActivity mMainActivity;

    public GoogleRequestTask(GoogleAccountCredential credential,MainActivity mMainActivity) {
        this.mMainActivity = mMainActivity;



        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Google Calendar API Android Quickstart")
                .build();
    }

    /**
     * Background task to call Google Calendar API.
     * @param params no parameters needed for this task.
     */
    @Override
    protected List<String> doInBackground(Void... params) {
        try {
            return getDataFromApi();
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }
    }

    /**
     * Fetch a list of the next 10 events from the primary calendar.
     * @return List of Strings describing returned events.
     * @throws IOException
     */
    private List<String> getDataFromApi() throws IOException {
        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = mService.events().list("primary")
                .setMaxResults(20)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        ArrayList<String> eventStrings = new ArrayList<String>();

        for (Event event : items) {
            DateTime start = event.getStart().getDateTime();
            if (start == null) {
                // All-day events don't have start times, so just use
                // the start date.
                start = event.getStart().getDate();

            }
            //Get Event
            StringBuilder year = new StringBuilder();
            StringBuilder month = new StringBuilder();
            StringBuilder date = new StringBuilder();
            StringBuilder hour = new StringBuilder();
            StringBuilder min = new StringBuilder();

            for(int i=0 ;i< start.toString().length();i++){
                char c = start.toString().charAt(i);
                if (i <= 3){
                    year.append(c);

                }
                if( i == 5 || i == 6){
                    month.append(c);
                }
                if( i == 8 || i ==9){
                    date.append(c);
                }
                if (i == 11 || i == 12){
                    hour.append(c);
                }
                if (i == 14 || i == 15){
                    min.append(c);
                }


            }
            eventStrings.remove(true);

            eventStrings.add(
                    String.format("%s %s", "Title : "+event.getSummary(),
                            "\n"
                                    +"Year : " +year.toString()
                                    +"\n"
                                    +"Month : " +month.toString()
                                    +"\n"
                                    +"Date :  " +date.toString()
                                    +"\n"
                                    +"hour :  " +hour.toString()
                                    +"\n"
                                    +"min : " +min.toString()
                                    +"\n"
                                    + "Get Location : "+event.getLocation()
                                    +"\n"
                                    +"Get nots :"+event.getDescription()



                    )    );

        }
        return eventStrings;
    }


    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(List<String> output) {
        if (output == null || output.size() == 0) {
            mMainActivity.sendGoogleMessage(output);
        } else {
            output.add(0, "Data retrieved using the Google Calendar API:");
            mMainActivity.sendGoogleMessage(output);

        }
    }

    @Override
    protected void onCancelled() {
        if (mLastError != null) {
            if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {

                Log.e(TAG, String.valueOf(((GooglePlayServicesAvailabilityIOException) mLastError)
                        .getConnectionStatusCode()));
            } else if (mLastError instanceof UserRecoverableAuthIOException) {
                mMainActivity.startActivityForResult(
                        ((UserRecoverableAuthIOException) mLastError).getIntent(),
                        Environment.REQUEST_AUTHORIZATION);
            } else {
                Log.e(TAG,"The following error occurred:\n"
                        + mLastError.getMessage());
            }
        } else {
            Log.e(TAG, "Request cancelled.");
        }
    }
}

