package kasper.android.file_explorer.blocks.drivers;

import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import kasper.android.file_explorer.R;
import kasper.android.file_explorer.core.Core;
import kasper.android.file_explorer.models.App;
import kasper.android.file_explorer.utils.AppUtils;
import kasper.android.file_explorer.utils.FileUtils;
import kasper.android.file_explorer.utils.UIUtils;
import kasper.android.file_explorer.blocks.base.BlockDriver;

public class AppsDriver implements BlockDriver {

    @Override
    public void fillData(View blockView, ImageView blockIconIV, TextView dataPart1TV, TextView dataPart2TV) {

        blockIconIV.setImageResource(R.drawable.app_light);

        final int appsCount = AppUtils.getAppsDict().size();
        long appsSize = 0;
        for (App app : AppUtils.getAppsDict().values()) {
            appsSize += app.getSize();
        }
        final long appsTSize = appsSize;

        dataPart1TV.setText(appsCount + " Apps" );
        dataPart2TV.setText(Formatter.formatFileSize(Core.getInstance(), appsTSize));

        blockView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIUtils.closeExtraUI();
                FileUtils.openDocument("/storage/apps");
            }
        });
    }
}