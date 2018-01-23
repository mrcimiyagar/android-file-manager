package kasper.android.file_explorer.ui_utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import kasper.android.file_explorer.core.Core;
import kasper.android.file_explorer.utils.HWareUtils;

public class DocDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;

    public DocDecoration(int spanCount) {

        this.spanCount = spanCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        /*int itemPosition = parent.getChildAdapterPosition(view);

        if (itemPosition < spanCount) {
            outRect.top = (int)(HWareUtils.getScreenDensity() * 16);
        }
        else {
            outRect.top = 0;//(int)(4 * HWareUtils.getScreenDensity());
        }

        int r0 = parent.getAdapter().getItemCount() % spanCount;

        if (r0 > 0) {
            if (itemPosition >= parent.getAdapter().getItemCount() - r0) {
                outRect.bottom = (int) (HWareUtils.getScreenDensity() * 45);
            }
            else {
                outRect.bottom = (int)(4 * HWareUtils.getScreenDensity());
            }
        }
        else {
            if (itemPosition >= parent.getAdapter().getItemCount() - spanCount) {
                outRect.bottom = (int) (HWareUtils.getScreenDensity() * 45);
            }
            else {
                outRect.bottom = (int)(4 * HWareUtils.getScreenDensity());
            }
        }

        int position = parent.getChildAdapterPosition(view);

        if (position % spanCount == 0) {
            outRect.left = (int)(8 * Core.getInstance().getResources().getDisplayMetrics().density);
        }
        else {
            outRect.left = 0;
        }

        if (position % spanCount == spanCount - 1) {
            outRect.right = (int)(8 * Core.getInstance().getResources().getDisplayMetrics().density);
        }
        else {
            outRect.right = 0;
        }*/

        /*int sidePadding = (int)(8 * HWareUtils.getScreenDensity());

        if (itemPosition % spanCount == 0) {

            outRect.left = sidePadding + sidePadding / 2;
            outRect.right = sidePadding / 2;
        }
        else if (itemPosition % spanCount == spanCount - 1) {

            outRect.left = sidePadding / 2;
            outRect.right = sidePadding + sidePadding / 2;
        }
        else {
            outRect.left = sidePadding;
            outRect.right = sidePadding;
        }*/

        /*int r = itemPosition % spanCount;
        float between = (spanCount / 2) - ((spanCount % 2 == 0) ? 0.5f : 0);
        outRect.left = (int)(((between - r) * 16 - 8 )* HWareUtils.getScreenDensity());*/
    }
}