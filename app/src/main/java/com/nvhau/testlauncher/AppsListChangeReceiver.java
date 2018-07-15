package com.nvhau.testlauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import static com.nvhau.testlauncher.AppDrawerActivity.APPS_LIST_CHANGE_INTENT;

/**
 * Listen for app install/uninstall event
 */
public class AppsListChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /* There is app install/uninstall, reload the app infos list */
        TestLauncherApplication testLauncherApplication =
                (TestLauncherApplication) context.getApplicationContext();
        testLauncherApplication.loadAppInfos();

        /* Notify app drawer to update apps list */
        LocalBroadcastManager.getInstance(context.getApplicationContext()).
                sendBroadcast(new Intent(APPS_LIST_CHANGE_INTENT));
    }
}
