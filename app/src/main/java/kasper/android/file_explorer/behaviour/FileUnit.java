package kasper.android.file_explorer.behaviour;

public interface FileUnit {

    String getTitle();

    String getPath();

    long getSize();

    void destroy();

    boolean isFly();
}