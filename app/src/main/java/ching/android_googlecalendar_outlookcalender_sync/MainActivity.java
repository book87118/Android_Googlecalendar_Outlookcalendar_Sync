package ching.android_googlecalendar_outlookcalender_sync;

import android.content.Context;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import ching.android_googlecalendar_outlookcalender_sync.RecyclerView.MarginDecoration;
import ching.android_googlecalendar_outlookcalender_sync.RecyclerView.RecycleViewAdapter;
import microsoft.exchange.webservices.data.ExchangeService;

public class MainActivity extends AppCompatActivity {

    private ExchangeService outlook_service ;

    private RecyclerView mRecyclerView;
    private RecycleViewAdapter mRecycleViewAdapter;

    private RecyclerView.LayoutManager mLayoutManager;
    private List<String> mDatas;
    static final String TAG = "MainActivty";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);

        initRecycleView();

    }

    private void initRecycleView(){
        if (mDatas == null){
            mDatas = new ArrayList<String>();
            mDatas.add("Temporarily no data");
        }
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);

        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.addItemDecoration(new MarginDecoration(this, MarginDecoration.VERTICAL_LIST));
        mLayoutManager = new StaggeredGridLayoutManager((2), StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecycleViewAdapter = new RecycleViewAdapter(mDatas);
        mRecyclerView.setAdapter(mRecycleViewAdapter);
    }
    @Override
    protected void onStart() {
        super.onStart();
        chechConnectingConnditions();
    }
    private void chechConnectingConnditions(){
        requestTask();

    }
    private void requestTask(){
        new OutlookRequesetTask(this).execute();

    }

    public void sendOutLookMessage(List<String> message){
        mDatas = message;
        mRecycleViewAdapter.swap(mDatas);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.google:
//                String accountName = getPreferences(Context.MODE_PRIVATE)
//                        .getString(PREF_ACCOUNT_NAME, null);
//
//                startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);

                break;
            case R.id.outllok:
//                PopUpOutLook popUpOutLook = new PopUpOutLook(this,this);
//                popUpOutLook.PopUp();
//                Toast.makeText(this, "OutLook", Toast.LENGTH_SHORT).show();

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);


    }

}
