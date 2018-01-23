package kasper.android.file_explorer.behaviour;

public interface ClipBoardUnit extends FileUnit {

    void markToBeCut();

    void clearCutFlag();

    boolean willBeCut();
}