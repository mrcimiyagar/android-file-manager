package kasper.android.file_explorer.blocks.base;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public interface BlockDriver {

    void fillData(View blockView, ImageView blockIconIV, TextView dataPart1TV, TextView dataPart2TV);
}