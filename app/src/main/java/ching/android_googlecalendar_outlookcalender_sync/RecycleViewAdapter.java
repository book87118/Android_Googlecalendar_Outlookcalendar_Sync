package ching.android_googlecalendar_outlookcalender_sync;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by book871181 on 16/7/21.
 */
public class RecycleViewAdapter extends RecyclerView.Adapter <ViewHolder >{
    private List<String> mDatas;

    public RecycleViewAdapter(List<String> mDatas){

        if(mDatas == null){
            throw new IllegalArgumentException("invaild DataBase");
        }else{
           this.mDatas = mDatas;
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_holder, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;



    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.mTextView.setText(mDatas.get(position));
    }


    @Override
    public int getItemCount() {
        return mDatas.size();
    }


}
