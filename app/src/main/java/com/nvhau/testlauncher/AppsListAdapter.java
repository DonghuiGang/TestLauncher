package com.nvhau.testlauncher;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AppsListAdapter extends RecyclerView.Adapter<AppsListAdapter.ViewHolder> {

    /* App infos list to be shown */
    private ArrayList<AppInfo> mAppInfos;

    public AppsListAdapter() {
        mAppInfos = new ArrayList<AppInfo>();
    }

    /**
     * Set adapter's data
     *
     * @param appsList
     */
    public void setData(ArrayList<AppInfo> appsList) {
        mAppInfos.clear();
        mAppInfos.addAll(appsList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.app_drawer_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        String appName = mAppInfos.get(position).mName.toString();
        Drawable appIcon = mAppInfos.get(position).mIcon;

        viewHolder.mAppName.setText(appName);
        viewHolder.mAppIcon.setImageDrawable(appIcon);
    }

    @Override
    public int getItemCount() {
        return mAppInfos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView mAppIcon;
        public TextView mAppName;

        public ViewHolder(View view) {
            super(view);
            mAppIcon = (ImageView) view.findViewById(R.id.image_app_icon);
            mAppName = (TextView) view.findViewById(R.id.text_app_name);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Context context = view.getContext();

            /* Launch app */
            Intent intent = context.getPackageManager().
                    getLaunchIntentForPackage(mAppInfos.get(position).mPackageName.toString());
            context.startActivity(intent);
        }
    }
}
