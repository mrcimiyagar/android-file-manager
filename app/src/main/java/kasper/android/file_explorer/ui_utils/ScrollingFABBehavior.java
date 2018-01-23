package kasper.android.file_explorer.ui_utils;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

import kasper.android.file_explorer.utils.HWareUtils;

public class ScrollingFABBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {

    public ScrollingFABBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {

        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

        child.clearAnimation();

        if (dyConsumed > 0 && !isHidden) {
            child.animate().y(HWareUtils.getScreenSizeY()).setDuration(150).start();
            isHidden = true;
        } else if (dyConsumed < 0 && isHidden) {
            child.animate().y(HWareUtils.getScreenSizeY() - 36 * HWareUtils.getScreenDensity() - child.getMeasuredHeight()).setDuration(150).start();
            isHidden = false;
        }
    }

    private boolean isHidden = false;

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return true;
    }
}