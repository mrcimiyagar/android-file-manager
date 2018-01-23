package kasper.android.file_explorer.models;

import java.io.Serializable;

public interface Action extends Serializable {

    void run();
}