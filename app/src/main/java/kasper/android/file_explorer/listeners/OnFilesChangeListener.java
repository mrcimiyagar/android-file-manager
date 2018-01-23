package kasper.android.file_explorer.listeners;

import kasper.android.file_explorer.models.App;

public interface OnFilesChangeListener {

    void appInserted(App app);
    void appDelete(App app);
    void docInserted(String parentPath, String path);
    void docDeleted(String parentPath, String path);
    void folderInserted(String parentPath, String path);
    void folderDeleted(String parentPath, String path);
    void sdCardStateChanged();
}