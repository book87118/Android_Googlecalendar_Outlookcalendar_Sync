package ching.android_googlecalendar_outlookcalender_sync.RequestTask;

import android.os.AsyncTask;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ching.android_googlecalendar_outlookcalender_sync.MainActivity;
import microsoft.exchange.webservices.data.Appointment;
import microsoft.exchange.webservices.data.CalendarFolder;
import microsoft.exchange.webservices.data.CalendarView;
import microsoft.exchange.webservices.data.ExchangeCredentials;
import microsoft.exchange.webservices.data.ExchangeService;
import microsoft.exchange.webservices.data.ExchangeVersion;
import microsoft.exchange.webservices.data.FindItemsResults;
import microsoft.exchange.webservices.data.PropertySet;
import microsoft.exchange.webservices.data.WebCredentials;
import microsoft.exchange.webservices.data.WellKnownFolderName;

/**
 * Created by book871181 on 16/8/6.
 */
public class OutlookRequestTask extends AsyncTask<Void,Void,List<String>>{

    private Exception mLastError = null;
    private final static String TAG = "OutlookRequesetTask";
    private ExchangeService outlook_service;
    private MainActivity mMainActivity;
    private SearchDate mDate;
    final class SearchDate{
        public Date startDate;
        public Date endDate;

    }
    String account ;
    public OutlookRequestTask(MainActivity mMainActivity,String account,String password){
        this.mMainActivity = mMainActivity;

        outlook_service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
        ExchangeCredentials credentials = new WebCredentials(account,password);

        outlook_service.setCredentials(credentials);
        this.account = account;



    }
    @Override
    protected List<String> doInBackground(Void... params) {
        try {
            outlook_service.autodiscoverUrl(account);
            mDate = settingDate(30);
            return findAppointments(mDate);
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<String> message) {
        if (message == null || message.size() == 0) {
            ArrayList<String> eventStrings = new ArrayList<String>();
            eventStrings.add(0,"OutLook has no results returned.");
            mMainActivity.sendOutLookMessage(message);

        } else {
            message.add(0, "Outlook Data retrieved using the EWS Calendar API: ");
            mMainActivity.sendOutLookMessage(message);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if(mLastError != null){
            Log.e(TAG,"The following error occurred:\n"
                    + mLastError.getMessage());
        }else {
            Log.e(TAG, "Request cancelled.");

        }
    }

    private SearchDate settingDate(int howManyDate) throws  Exception {
        SearchDate mDate = new SearchDate();
        SimpleDateFormat mTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date()); // using today's date.
        String string_startDate = mTimeFormat.format(c.getTime());
        mDate.startDate = mTimeFormat.parse(string_startDate);

        c.add(Calendar.DATE, howManyDate);

        String string_endDate = mTimeFormat.format(c.getTime());
        mDate.endDate = mTimeFormat.parse(string_endDate);


            return mDate;
        }


        private List<String> findAppointments(SearchDate mDate) throws Exception{


            CalendarFolder cf = CalendarFolder.bind(outlook_service, WellKnownFolderName.Calendar);

            FindItemsResults<Appointment> findResults = cf.findAppointments(
                    new CalendarView(mDate.startDate, mDate.endDate));
            outlook_service.loadPropertiesForItems(findResults, PropertySet.FirstClassProperties);

            List<String> message = new ArrayList<String>();

            for (Appointment appt : findResults.getItems()) {

                String appoint = "Outlook :" + "\n" + appt.getSubject() + "\n" + appt.getStart();
                Log.e(TAG,"add : "+appoint );
                message.add(appoint);

            }

            if(message != null){
                Log.d(TAG, "message:" + message.size());
                return message;
            }else{
                Log.e(TAG,"message is null");
                return message;

            }


        }


}
