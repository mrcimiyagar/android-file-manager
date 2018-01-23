package kasper.android.file_explorer.utils;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import kasper.android.file_explorer.listeners.OnFilesChangeListener;
import kasper.android.file_explorer.listeners.OnUITransactListener;
import kasper.android.file_explorer.models.Action;
import kasper.android.file_explorer.models.App;
import kasper.android.file_explorer.models.RRAction;

public class UIUtils {

    private static OnUITransactListener uiTransactListener;
    private static OnFilesChangeListener pageEventListener;

    public static void setup(OnUITransactListener uiTransactListener, OnFilesChangeListener pagerEventListener) {

        UIUtils.uiTransactListener = uiTransactListener;
        UIUtils.pageEventListener = pagerEventListener;
    }

    public static void openSoftKeyboard() {

        uiTransactListener.openSoftKeyboard();
    }

    public static void closeSoftKeyboard() {

        uiTransactListener.closeSoftKeyboard();
    }

    public static void showCxtMenu(View view, int[] iconsResources, String[] labels, Action[] actions) {

        uiTransactListener.showCxtMenu(view, iconsResources, labels, actions);
    }

    public static void updateTopPage() {

        uiTransactListener.updateTopPage();
    }

    public static void notifyAppInserted(App app) {

        pageEventListener.appInserted(app);
    }

    public static void notifyAppDeleted(App app) {

        pageEventListener.appDelete(app);
    }

    public static void notifyDocInserted(String path) {

        String parentPath = path.substring(0, path.lastIndexOf("/"));

        pageEventListener.docInserted(parentPath, path);
    }

    public static void notifyDocDeleted(String path) {

        String parentPath = path.substring(0, path.lastIndexOf("/"));

        pageEventListener.docDeleted(parentPath, path);
    }

    public static void notifyFolderInserted(String path) {

        if (path.endsWith("/") && path.length() > 1) {
            path = path.substring(0, path.length() - 1);
        }

        String parentPath = path.substring(0, path.lastIndexOf("/"));

        pageEventListener.folderInserted(parentPath, path);
    }

    public static void notifyFolderDeleted(String path) {

        if (path.endsWith("/") && path.length() > 1) {
            path = path.substring(0, path.length() - 1);
        }

        String parentPath = path.substring(0, path.lastIndexOf("/"));

        pageEventListener.folderDeleted(parentPath, path);
    }

    public static void showDialogBox(View view) {

        Log.d("KasperLogger", "test 1");

        uiTransactListener.showDialogBox(view);
    }

    public static void closeDialogBox() {

        uiTransactListener.closeDialogBox();
    }

    public static LayoutInflater getLayoutInflater() {

        return uiTransactListener.getLayoutInflater();
    }

    public static void runIntentForResult(Intent intent, RRAction action) {

        uiTransactListener.runIntentForResult(intent, action);
    }

    public static void notifySDCardStateChanged() {

        pageEventListener.sdCardStateChanged();
    }

    public static void openNewTab(String path) {

        uiTransactListener.openTab(path);
    }

    public static void openNewTab() {

        uiTransactListener.openTab();
    }

    public static void closeExtraUI() {

        uiTransactListener.closeExtraUI();
    }
}