package kasper.android.file_explorer.ui_utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import kasper.android.file_explorer.utils.HWareUtils;

public class AppDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;

    public AppDecoration(int spanCount) {

        this.spanCount = spanCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){

        int itemPosition = parent.getChildAdapterPosition(view);


        if (itemPosition < spanCount) {
            outRect.top = (int) (16 * HWareUtils.getScreenDensity());
        }
        else {
            outRect.top = 0;
        }

        if (itemPosition >= parent.getAdapter().getItemCount() - (parent.getAdapter().getItemCount() % spanCount)) {
            outRect.bottom = (int) (HWareUtils.getScreenDensity() * 45);
        }
        else {
            outRect.bottom = 0;
        }

        int r = itemPosition % spanCount;

        float between = (spanCount / 2) - ((spanCount % 2 == 0) ? 0.5f : 0);

        outRect.left = (int)((between - r) * 8 * HWareUtils.getScreenDensity());
    }
}