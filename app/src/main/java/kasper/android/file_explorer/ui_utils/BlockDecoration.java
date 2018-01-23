package kasper.android.file_explorer.ui_utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class BlockDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int topMargin;
    private int bottomMargin;

    public BlockDecoration(int spanCount, int topMargin, int bottomMargin) {

        this.spanCount = spanCount;
        this.topMargin = topMargin;
        this.bottomMargin = bottomMargin;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int itemPosition = parent.getChildAdapterPosition(view);

        if (itemPosition < spanCount) {

            outRect.top = topMargin;
        }

        if (itemPosition >= parent.getAdapter().getItemCount() - spanCount) {

            outRect.bottom = bottomMargin;
        }
    }
}