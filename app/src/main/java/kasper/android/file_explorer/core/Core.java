package kasper.android.file_explorer.core;

import android.app.Application;
import android.net.Uri;
import android.support.v4.provider.DocumentFile;

import java.io.File;

import kasper.android.file_explorer.utils.AppUtils;
import kasper.android.file_explorer.utils.FileUtils;
import kasper.android.file_explorer.utils.HWareUtils;
import kasper.android.file_explorer.utils.ImageUtils;
import kasper.android.file_explorer.utils.ResUtils;
import kasper.android.file_explorer.utils.TaskUtils;

public class Core extends Application {

    private static Core instance;
    public static Core getInstance() { return instance; }

    @Override
    public void onCreate() {

        super.onCreate();

        instance = this;

        TaskUtils.setup();
        HWareUtils.setup(this);
        AppUtils.setup();
        FileUtils.setup();
        ResUtils.setup();
        ImageUtils.setup(this);

        File sdCardUriStr = new File(FileUtils.getPrimaryRootFolder(), "sdCardUriString.KasperFileExplorer");

        if (sdCardUriStr.exists()) {
            try {
                String uriStr = (String) FileUtils.readObjectFromFile(sdCardUriStr);
                Uri sdCardUri = Uri.parse(uriStr);
                FileUtils.setSecondaryRootFolder(DocumentFile.fromTreeUri(Core.getInstance(), sdCardUri));
            }
            catch (Exception ignored) { }
        }
    }
}