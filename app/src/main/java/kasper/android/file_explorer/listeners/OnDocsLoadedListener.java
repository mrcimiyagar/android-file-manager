package kasper.android.file_explorer.listeners;

import java.util.ArrayList;

import kasper.android.file_explorer.models.files.Doc;

/**
 * Created by keyhan1376 on 12/15/2017.
 */

public interface OnDocsLoadedListener {
    void docsLoaded(ArrayList<Doc> docs);
}
