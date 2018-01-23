package kasper.android.file_explorer.listeners;

public interface OnCbrdTransactListener {

    void fileTransacting(String path, int progress);

    void workingQueueDone();
}