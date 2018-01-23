package kasper.android.file_explorer.ui_utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import kasper.android.file_explorer.utils.HWareUtils;

public class RocketDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;

    public RocketDecoration(int spanCount) {

        this.spanCount = spanCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int itemPosition = parent.getChildAdapterPosition(view);

        if (itemPosition < spanCount) {
            outRect.top = (int)(HWareUtils.getScreenDensity() * 32);
        }
        else {
            outRect.top = (int)(4 * HWareUtils.getScreenDensity());
        }

        int r0 = parent.getAdapter().getItemCount() % spanCount;

        if (r0 > 0) {
            if (itemPosition >= parent.getAdapter().getItemCount() - r0) {
                outRect.bottom = (int) (HWareUtils.getScreenDensity() * 8);
            }
            else {
                outRect.bottom = (int)(4 * HWareUtils.getScreenDensity());
            }
        }
        else {
            if (itemPosition >= parent.getAdapter().getItemCount() - spanCount) {
                outRect.bottom = (int) (HWareUtils.getScreenDensity() * 8);
            }
            else {
                outRect.bottom = (int)(4 * HWareUtils.getScreenDensity());
            }
        }
    }
}