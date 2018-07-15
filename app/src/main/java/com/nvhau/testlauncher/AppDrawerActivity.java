package com.nvhau.testlauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

public class AppDrawerActivity extends AppCompatActivity {

    /* Local broadcast intent */
    public static String APPS_LIST_CHANGE_INTENT = "APPS_LIST_CHANGE_INTENT";

    private AppsListAdapter mAppsListAdapter;

    /* Listen for apps list change event */
    private BroadcastReceiver mAppsListChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            TestLauncherApplication testLauncherApplication =
                    (TestLauncherApplication) context.getApplicationContext();
            mAppsListAdapter.setData(testLauncherApplication.getAppsInfo());
            mAppsListAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_drawer);

        TestLauncherApplication testLauncherApplication =
                (TestLauncherApplication) getApplicationContext();
        mAppsListAdapter = new AppsListAdapter();
        mAppsListAdapter.setData(testLauncherApplication.getAppsInfo());

        RecyclerView appsList = findViewById(R.id.view_all_apps);
        appsList.setAdapter(mAppsListAdapter);

        /* Calculate grid columns count */
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        View view = View.inflate(this, R.layout.app_drawer_item, null);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int gridColumns = displayMetrics.widthPixels / view.getMeasuredWidth();

        appsList.setLayoutManager(new GridLayoutManager(this, gridColumns));

        /* Register apps list change event receiver */
        LocalBroadcastManager.getInstance(this).registerReceiver(mAppsListChangeReceiver,
                new IntentFilter(APPS_LIST_CHANGE_INTENT));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /* Unregister apps list change event receiver */
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mAppsListChangeReceiver);
    }

    @Override
    public void onBackPressed() {
        /* Go back to home screen and tend to keep this activity alive for later bring back */
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
