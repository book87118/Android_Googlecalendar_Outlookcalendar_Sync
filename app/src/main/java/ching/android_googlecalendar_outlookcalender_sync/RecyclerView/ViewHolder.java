package ching.android_googlecalendar_outlookcalender_sync.RecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ching.android_googlecalendar_outlookcalender_sync.R;

/**
 * Created by book871181 on 16/7/22.
 */
public class ViewHolder extends RecyclerView.ViewHolder {
    public TextView mTextView;

    public ViewHolder(View itemView) {

        super(itemView);
        mTextView = (TextView)itemView.findViewById(R.id.text);

    }
}
