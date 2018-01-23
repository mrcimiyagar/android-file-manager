package kasper.android.file_explorer.models;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import kasper.android.file_explorer.behaviour.Searchable;
import kasper.android.file_explorer.core.Core;

public class App implements Searchable {

    private String title = "";
    public String getTitle() {
        if (this.title.length() == 0) {
            try {
                this.title = Core.getInstance().getPackageManager().getApplicationLabel
                        (Core.getInstance().getPackageManager().getApplicationInfo(this.packageName, 0)).toString();
            }
            catch (Exception ignored) {
            }
        }
        return this.title;
    }

    private Drawable icon;
    public Drawable getIcon() {
        if (this.icon == null) {
            try {
                this.icon = Core.getInstance().getPackageManager().getApplicationIcon
                        (Core.getInstance().getPackageManager().getApplicationInfo(packageName, 0));
            }
            catch (Exception ignored) {
            }
        }
        return this.icon;
    }

    private String packageName;
    public String getPackageName() { return this.packageName; }

    private boolean isSeen = false;

    public App(String packageName) {

        this.packageName = packageName;
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof App && this.getPackageName().equals(((App)obj).getPackageName());
    }

    public long getSize() {

        try {
            final PackageManager pm = Core.getInstance().getPackageManager();
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
            java.io.File appDir = new java.io.File(applicationInfo.dataDir);
            return appDir.length();
        }
        catch (Exception ignored) { }

        return 0;
    }

    @Override
    public boolean isSeen() {

        return isSeen;
    }

    @Override
    public void markAsSeen() {

        this.isSeen = true;
    }

    @Override
    public void clearSeenFlag() {

        this.isSeen = false;
    }
}