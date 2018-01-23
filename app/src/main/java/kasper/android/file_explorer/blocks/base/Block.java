package kasper.android.file_explorer.blocks.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public abstract class Block {

    public abstract BlockTypes getBlockType();

    public abstract View getView(Context context, LayoutInflater inflater);

    public abstract void onResume();
}