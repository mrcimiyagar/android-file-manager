package kasper.android.file_explorer.behaviour;

public interface Searchable {

    boolean isSeen();

    void markAsSeen();

    void clearSeenFlag();
}