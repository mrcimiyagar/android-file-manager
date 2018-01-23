package kasper.android.file_explorer.listeners;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import kasper.android.file_explorer.models.Action;
import kasper.android.file_explorer.models.RRAction;

public interface OnUITransactListener {

    void openSoftKeyboard();

    void closeSoftKeyboard();

    void showCxtMenu(View view, int[] iconsResources, String[] labels, Action[] actions);

    void updateTopPage();

    void showDialogBox(View view);

    void closeDialogBox();

    LayoutInflater getLayoutInflater();

    void runIntentForResult(Intent intent, RRAction action);

    void openTab(String path);

    void openTab();

    void closeExtraUI();
}