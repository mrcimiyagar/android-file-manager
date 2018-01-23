package kasper.android.file_explorer.behaviour;

public interface Taskable {

    void doInBackground(Object... params);

    void onCancelled();
}