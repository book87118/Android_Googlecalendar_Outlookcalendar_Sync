package ching.android_googlecalendar_outlookcalender_sync;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<String> mDatas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        putDataBase();

        settingRecycleView();

    }
    private void putDataBase(){
        mDatas = new ArrayList<String>();
        mDatas.add("測試測試測試測試測試測試測試測試測試測試測試測試測試測試測試測試測試測試測試");
        for(int i =0 ; i < 50 ; i++) {
            mDatas.add(String.valueOf(i));
        }

    }

    private void settingRecycleView(){
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);

        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.addItemDecoration(new MarginDecoration(this, MarginDecoration.VERTICAL_LIST));
        mLayoutManager = new StaggeredGridLayoutManager((2), StaggeredGridLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(new RecycleViewAdapter(mDatas));
    }

}
