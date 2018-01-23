package kasper.android.file_explorer.models;

import android.os.Handler;
import android.os.Looper;

import kasper.android.file_explorer.behaviour.Taskable;

public abstract class BackTask implements Taskable {

    public final void shootToUI(Runnable runnable) {

        new Handler(Looper.getMainLooper()).post(runnable);
    }

    @Override
    public void onCancelled() {

    }
}