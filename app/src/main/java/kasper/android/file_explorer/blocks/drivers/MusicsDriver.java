package kasper.android.file_explorer.blocks.drivers;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import kasper.android.file_explorer.R;
import kasper.android.file_explorer.core.Core;
import kasper.android.file_explorer.utils.FileUtils;
import kasper.android.file_explorer.utils.UIUtils;
import kasper.android.file_explorer.blocks.base.BlockDriver;

public class MusicsDriver implements BlockDriver {

    @Override
    public void fillData(View blockView, ImageView blockIconIV, TextView dataPart1TV, TextView dataPart2TV) {

        blockIconIV.setImageResource(R.drawable.music_light);

        int musicsCount = 0;
        long musicsSize = 0;

        try {
            String pathOfDoc;
            File fileOfDoc;
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.MediaColumns.DATA};
            Cursor cursor = Core.getInstance().getContentResolver().query(uri, projection, null, null, null);
            assert cursor != null;
            int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            while (cursor.moveToNext()) {
                pathOfDoc = cursor.getString(column_index_data);
                fileOfDoc = new File(pathOfDoc);
                if (fileOfDoc.exists()) {
                    musicsCount++;
                    musicsSize += fileOfDoc.length();
                }

            }
            cursor.close();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        dataPart1TV.setText(musicsCount + " Musics");
        dataPart2TV.setText(Formatter.formatFileSize(Core.getInstance(), musicsSize));

        blockView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIUtils.closeExtraUI();
                FileUtils.openDocument("/storage/musics");
            }
        });
    }
}