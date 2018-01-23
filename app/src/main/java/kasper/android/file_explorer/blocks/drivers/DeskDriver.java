package kasper.android.file_explorer.blocks.drivers;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import kasper.android.file_explorer.R;
import kasper.android.file_explorer.utils.FileUtils;
import kasper.android.file_explorer.utils.UIUtils;
import kasper.android.file_explorer.blocks.base.BlockDriver;

public class DeskDriver implements BlockDriver {

    @Override
    public void fillData(View blockView, ImageView blockIconIV, TextView dataPart1TV, TextView dataPart2TV) {

        blockIconIV.setImageResource(R.drawable.desk_light);

        final File file = new File(FileUtils.getPrimaryRootFolder(), "Desktop");

        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }

        int foldersCount = 0;
        int docsCount = 0;

        File[] files = file.listFiles();

        for (File childFile : files) {
            if (childFile.isDirectory()) {
                foldersCount++;
            } else {
                docsCount++;
            }
        }

        dataPart1TV.setText(foldersCount + " folders");
        dataPart2TV.setText(docsCount + " documents");

        blockView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIUtils.closeExtraUI();
                FileUtils.openDocument(file.getAbsolutePath());
            }
        });
    }
}