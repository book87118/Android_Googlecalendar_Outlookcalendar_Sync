package ching.android_googlecalendar_outlookcalender_sync;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ching.android_googlecalendar_outlookcalender_sync.RecyclerView.MarginDecoration;
import ching.android_googlecalendar_outlookcalender_sync.RecyclerView.RecycleViewAdapter;
import ching.android_googlecalendar_outlookcalender_sync.RequestTask.GoogleRequestTask;
import ching.android_googlecalendar_outlookcalender_sync.RequestTask.OutlookRequestTask;
import ching.android_googlecalendar_outlookcalender_sync.Utils.Conditions;
import ching.android_googlecalendar_outlookcalender_sync.Utils.Environment;
import ching.android_googlecalendar_outlookcalender_sync.Utils.SavingSharedPreferences;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{


    private RecyclerView mRecyclerView;
    private RecycleViewAdapter mRecycleViewAdapter;
    private SavingSharedPreferences mSavingSharedPreferences;

    private RecyclerView.LayoutManager mLayoutManager;
    static final String TAG = "MainActivty";
    private GoogleAccountCredential mCredential;
    private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };

    private List<String> list_GoogleMessage;
    private List<String> list_OutlookMessage;
    private List<String> list_CombineMessage;
    private ProgressDialog mProgress;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);

        mProgress = new ProgressDialog(this);
        sp = this.getSharedPreferences(
                "account", Context.MODE_PRIVATE);
        mSavingSharedPreferences = new SavingSharedPreferences(sp);
        initRecycleView();

        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        String accountName = mSavingSharedPreferences.getGoogleAccount();
        if(accountName != null) {
            mCredential.setSelectedAccountName(accountName);
        }
    }

    private void initRecycleView(){
        List<String> initMessage ;
        initMessage = mSavingSharedPreferences.getAllMessage();
        if (initMessage == null){
            initMessage = new ArrayList<String>();
            initMessage.add("Temporarily no data. Please try to reflash");
        }
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);

        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.addItemDecoration(new MarginDecoration(this, MarginDecoration.VERTICAL_LIST));
        mLayoutManager = new StaggeredGridLayoutManager((2), StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecycleViewAdapter = new RecycleViewAdapter(initMessage);
        mRecyclerView.setAdapter(mRecycleViewAdapter);
    }

    private void checkConnectingConditions(){
         Conditions mConditions = new Conditions(this);

        if(!mConditions.isDeviceOnline()) {
            Toast.makeText(this,"Sorry, internet is unavailable",Toast.LENGTH_LONG).show();
        }else {

            if(mSavingSharedPreferences.getOutlookAccount() != null
                    && mSavingSharedPreferences.getOutlookPassword() != null) {
                outlookRequestTask();
            }

            if (! mConditions.isGooglePlayServicesAvailable()) {
                Log.d(TAG, "into acquireGooglePlayServices");
                mConditions.acquireGooglePlayServices();
            }else if (mCredential.getSelectedAccountName() == null) {

                    Log.d(TAG, "into ChooseAcoount");

                    chooseGoogleAccount();
                }else {
                googleRequestTask();

                }

            }

        }



    @AfterPermissionGranted(Environment.REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseGoogleAccount(){
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = mSavingSharedPreferences.getGoogleAccount();
            if (accountName != null) {

                mCredential.setSelectedAccountName(accountName);

                  checkConnectingConditions();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        Environment.REQUEST_ACCOUNT_PICKER);
            }
        } else {
            Log.d(TAG,"Choosse Account REQUEST Permissions");
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    Environment.REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Environment.REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(this," This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.",
                            Toast.LENGTH_LONG).show();
                } else {
                    checkConnectingConditions();
                }
                break;

            /**
             * Choose Account and Saving in SharedPreferences
             * */
            case Environment.REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        mSavingSharedPreferences.setGoogleAccount(accountName);
                        mCredential.setSelectedAccountName(accountName);
                    }
                }
                break;

            case Environment.REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    checkConnectingConditions();
                }
                break;

            default:
                break;
        }

    }

    private void outlookRequestTask(){
        new OutlookRequestTask(this,mSavingSharedPreferences.getOutlookAccount(),
                mSavingSharedPreferences.getOutlookPassword()).execute();

    }

    private void googleRequestTask(){
        new GoogleRequestTask(mCredential,this).execute();
    }



    public void sendGoogleMessage(List<String> message){
        list_GoogleMessage = new ArrayList<String>();
        list_GoogleMessage = message;
        combineData();
    }


    public void sendOutLookMessage(List<String> message){
        list_OutlookMessage = new ArrayList<String>();
        list_OutlookMessage = message;
        combineData();
    }
    public void combineData(){
        list_CombineMessage = new ArrayList<String>();
        list_CombineMessage.clear();
        if (list_OutlookMessage != null) {
            list_CombineMessage.addAll(list_OutlookMessage);
            Log.e(TAG,"Outlook has Data" + list_OutlookMessage.size());

        }

        if(list_GoogleMessage != null) {

            list_CombineMessage.addAll(list_GoogleMessage);
        }
        if(list_OutlookMessage != null && list_GoogleMessage != null) {
        mProgress.hide();
        }
        mSavingSharedPreferences.setLeastMessage(list_CombineMessage);
        mRecycleViewAdapter.swap(list_CombineMessage);


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reload:
                mProgress.show();
                checkConnectingConditions();
                break;

            case R.id.google:
                startActivityForResult(mCredential.newChooseAccountIntent(), Environment.REQUEST_ACCOUNT_PICKER);
                break;

            case R.id.outlook:
                PopUpOutLook popUpOutLook = new PopUpOutLook(this,this,mSavingSharedPreferences);
                popUpOutLook.popUpShow();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    //Do nothing
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    //Do nothing

    }
}
