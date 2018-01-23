package kasper.android.file_explorer.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import kasper.android.file_explorer.core.Core;
import kasper.android.file_explorer.listeners.OnAppDeletedListener;
import kasper.android.file_explorer.listeners.OnAppInsertedListener;
import kasper.android.file_explorer.models.App;

public class AppUtils {

    private static Hashtable<String, App> appsDict;
    public static Hashtable<String, App> getAppsDict() { return appsDict; }

    public static void setup() {

        appsDict = new Hashtable<>();

        try {
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> appsList = Core.getInstance().getPackageManager().queryIntentActivities(mainIntent, 0);
            //PackageManager packageManager = Core.getInstance().getPackageManager();
            for (ResolveInfo ri : appsList) {
                App app = new App(ri.activityInfo.packageName);
                appsDict.put(app.getPackageName(), app);
            }
        }
        catch (Exception ignored) { ignored.printStackTrace(); }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        AppUtils.AppsReceiver appsUtils = new AppUtils.AppsReceiver();
        appsUtils.setAppInsertedListener(new OnAppInsertedListener() {
            @Override
            public void appInserted(App app) {
                appsDict.put(app.getPackageName(), app);
                UIUtils.updateTopPage();

            }
        });
        appsUtils.setAppDeletedListener(new OnAppDeletedListener() {
            @Override
            public void appDeleted(App app) {
                appsDict.remove(app.getPackageName());
                UIUtils.updateTopPage();
            }
        });
        Core.getInstance().registerReceiver(appsUtils, intentFilter);
    }

    public static class AppsReceiver extends BroadcastReceiver {

        private OnAppInsertedListener appInsertedListener;
        public void setAppInsertedListener(OnAppInsertedListener appInsertedListener) { this.appInsertedListener = appInsertedListener; }

        private OnAppDeletedListener appDeletedListener;
        public void setAppDeletedListener(OnAppDeletedListener appDeletedListener) { this.appDeletedListener = appDeletedListener; }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                try {
                    String packageName = intent.getData().getEncodedSchemeSpecificPart();
                    if (!appsDict.containsKey(packageName)) {
                        PackageManager pm = Core.getInstance().getPackageManager();
                        ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
                        //Drawable icon = pm.getApplicationIcon(ai);
                        App app = new App(packageName);
                        if (this.appInsertedListener != null) {
                            this.appInsertedListener.appInserted(app);
                        }
                    }
                }
                catch (Exception ignored) {}
            }
            else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
                try {
                    String packageName = intent.getData().getEncodedSchemeSpecificPart();
                    App app = appsDict.get(packageName);
                    if (app != null) {
                        if (this.appDeletedListener != null) {
                            this.appDeletedListener.appDeleted(app);
                        }
                    }
                }
                catch (Exception ignored) {}
            }
        }
    }

    public static void uninstallApp(String packageName) {

        Intent intent = new Intent(Intent.ACTION_DELETE, Uri.fromParts("package", packageName, null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Core.getInstance().startActivity(intent);
    }

    public static void showAppInfo(String packageName) {

        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromParts("package", packageName, null);
        intent.setData(uri);
        Core.getInstance().startActivity(intent);
    }

    public static void openApplication(String packageName) {

        try {
            Core.getInstance().startActivity(Core.getInstance().getPackageManager().getLaunchIntentForPackage(packageName));
        }
        catch (RuntimeException ignored) {
            Toast.makeText(Core.getInstance(), "Unable to start application", Toast.LENGTH_SHORT).show();
        }
    }

    public static ArrayList<App> getAppsList() {

        ArrayList<App> appsList = new ArrayList<>(AppUtils.getAppsDict().values());
        Collections.sort(appsList, new Comparator<App>() {
            @Override
            public int compare(App a1, App a2) {
                return a1.getTitle().compareToIgnoreCase(a2.getTitle());
            }
        });

        return appsList;
    }
}