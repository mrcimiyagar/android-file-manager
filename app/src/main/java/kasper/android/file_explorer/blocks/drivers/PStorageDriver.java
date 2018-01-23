package kasper.android.file_explorer.blocks.drivers;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import kasper.android.file_explorer.blocks.base.BlockDriver;

public class PStorageDriver implements BlockDriver {

    @Override
    public void fillData(View blockView, ImageView blockIconIV, TextView dataPart1TV, TextView dataPart2TV) {

        /*blockIconIV.setImageResource(R.drawable.storage_light);

        Pair<Pair<String, String>, Integer> storageInfo = FileUtils.getStorageInfo(FileUtils.getPrimaryStoragePath());

        //dataPart1TV.setText(storageInfo.first.concat(" Free"));

        //dataPart2TV.setText(storageInfo.second.concat(" Used"));

        blockView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIUtils.closeExtraUI();
                FileUtils.openDocument(FileUtils.getPrimaryStoragePath());
            }
        });*/
    }
}