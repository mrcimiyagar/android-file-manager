package kasper.android.file_explorer.blocks.drivers;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import kasper.android.file_explorer.blocks.base.BlockDriver;

public class SStorageDriver implements BlockDriver {

    @Override
    public void fillData(View blockView, ImageView blockIconIV, TextView dataPart1TV, TextView dataPart2TV) {

        /*blockIconIV.setImageResource(R.drawable.sd_card_light);

        Pair<String, String> storageInfo = FileUtils.getStorageInfo(FileUtils.getSecondaryStoragePath());

        dataPart1TV.setText(storageInfo.first.concat(" Free"));

        dataPart2TV.setText(storageInfo.second.concat(" Used"));

        blockView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIUtils.closeExtraUI();
                FileUtils.openDocument(FileUtils.getSecondaryStoragePath());
            }
        });*/
    }
}