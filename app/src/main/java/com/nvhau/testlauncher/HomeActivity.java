package com.nvhau.testlauncher;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.nvhau.testlauncher.widget.LocationWidgetProvider;

public class HomeActivity extends AppCompatActivity {

    /* Host widget stuff, taken from here
     * http://coderender.blogspot.com/2012/01/hosting-android-widgets-my.html
     * and add some fixes to work on new platforms
     * */
    private AppWidgetManager mAppWidgetManager;
    private AppWidgetHost mAppWidgetHost;

    /* Widgets will be added into this */
    private ViewGroup mMainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mMainView = (ViewGroup) findViewById(R.id.view_home);

        mAppWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        mAppWidgetHost = new AppWidgetHost(getApplicationContext(),
                TestLauncherApplication.normalizeId(R.id.APPWIDGET_HOST_ID));

        /* Auto add location widget on launcher start */
        /* Below codes is working but it will show a dialog asking user to grant permission first,
         * this is not the expected behavior on this test project so will disable this block.
         * */
//        int appWidgetId = mAppWidgetHost.allocateAppWidgetId();
//        ComponentName locationWidget = new ComponentName(getApplicationContext(),
//                LocationWidgetProvider.class);
//        Intent bindIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_BIND);
//        bindIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
//        bindIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, locationWidget);
//        startActivityForResult(bindIntent,
//                TestLauncherApplication.normalizeId(R.id.REQUEST_BIND_APPWIDGET));
    }

    public void onButtonClick(View view) {
        if (view.getId() == R.id.button_all_apps) {
            /* Show app drawer */
            Intent intent = new Intent(this, AppDrawerActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.button_add_widget) {
            selectWidget();
        } else if (view.getId() == R.id.button_remove_widget) {
            int childCount = mMainView.getChildCount();
            if (childCount > 0) {
                View homeView = mMainView.getChildAt(childCount - 1);
                if (homeView instanceof AppWidgetHostView) {
                    removeWidget((AppWidgetHostView) homeView);
                }
            }
        }
    }

    /**
     * Launches the menu to select the widget. The selected widget will be on
     * the result of the activity.
     */
    private void selectWidget() {
        int appWidgetId = mAppWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        startActivityForResult(pickIntent,
                TestLauncherApplication.normalizeId(R.id.REQUEST_PICK_APPWIDGET));
    }

    /**
     * If the user has selected an widget, the result will be in the 'data' when
     * this function is called.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == TestLauncherApplication.normalizeId(R.id.REQUEST_PICK_APPWIDGET) ||
                    requestCode == TestLauncherApplication.normalizeId(R.id.REQUEST_BIND_APPWIDGET)
                    ) {
                configureWidget(data);
            } else if (requestCode ==
                    TestLauncherApplication.normalizeId(R.id.REQUEST_CREATE_APPWIDGET)) {
                createWidget(data);
            }
        } else if (resultCode == RESULT_CANCELED && data != null) {
            int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (appWidgetId != -1) {
                mAppWidgetHost.deleteAppWidgetId(appWidgetId);
            }
        }
    }

    /**
     * Checks if the widget needs any configuration. If it needs, launches the
     * configuration activity.
     */
    private void configureWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        if (appWidgetInfo.configure != null) {
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            startActivityForResult(intent,
                    TestLauncherApplication.normalizeId(R.id.REQUEST_CREATE_APPWIDGET));
        } else {
            createWidget(data);
        }
    }

    /**
     * Creates the widget and adds to our view layout.
     */
    public void createWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);

        AppWidgetHostView hostView = mAppWidgetHost.createView(
                getApplicationContext(), appWidgetId, appWidgetInfo);
        hostView.setAppWidget(appWidgetId, appWidgetInfo);
        mMainView.addView(hostView);
    }

    /**
     * Removes the widget displayed by this AppWidgetHostView.
     */
    public void removeWidget(AppWidgetHostView hostView) {
        mAppWidgetHost.deleteAppWidgetId(hostView.getAppWidgetId());
        mMainView.removeView(hostView);
    }

    /**
     * Registers the AppWidgetHost to listen for updates to any widgets this app
     * has.
     */
    @Override
    protected void onStart() {
        super.onStart();
        mAppWidgetHost.startListening();
    }

    /**
     * Stop listen for updates for our widgets (saving battery).
     */
    @Override
    protected void onStop() {
        super.onStop();
        mAppWidgetHost.stopListening();
    }

    @Override
    public void onBackPressed() {
        /* A launcher should never close */
        //super.onBackPressed();
    }
}
