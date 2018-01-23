package kasper.android.file_explorer.models;

import android.content.Intent;

public interface RRAction {

    void run(int resultCode, Intent resultData);
}