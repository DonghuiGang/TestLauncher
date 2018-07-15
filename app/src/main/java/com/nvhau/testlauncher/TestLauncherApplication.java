package com.nvhau.testlauncher;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.List;

public class TestLauncherApplication extends Application {

    /* Device's all app infos list */
    private ArrayList<AppInfo> mAppInfos;

    /**
     * Convert a 32bits id to a maximum 16bits value id
     *
     * @param id
     * @return
     */
    public static int normalizeId(int id) {
        return id & 0x0000FFFF;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAppInfos = new ArrayList<AppInfo>();

        loadAppInfos();
    }

    /**
     * Load device's all app infos
     */
    public void loadAppInfos() {
        PackageManager packageManager = getPackageManager();
        mAppInfos.clear();

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> appInfos = packageManager.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : appInfos) {
            AppInfo appInfo = new AppInfo();
            appInfo.mName = resolveInfo.loadLabel(packageManager);
            appInfo.mPackageName = resolveInfo.activityInfo.packageName;
            appInfo.mIcon = resolveInfo.activityInfo.loadIcon(packageManager);

            mAppInfos.add(appInfo);
        }
    }

    public ArrayList<AppInfo> getAppsInfo() {
        return mAppInfos;
    }
}
