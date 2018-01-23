package kasper.android.file_explorer.listeners;

public interface OnDialogMessageChangeListener {

    void updateMessage(String message);

    void updateProgress(int progress);
}