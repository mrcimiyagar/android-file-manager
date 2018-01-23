package kasper.android.file_explorer.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.io.File;

import kasper.android.file_explorer.R;
import kasper.android.file_explorer.activities.FileExplorerActivity;
import kasper.android.file_explorer.core.Core;
import kasper.android.file_explorer.utils.AppUtils;
import kasper.android.file_explorer.utils.FileUtils;
import kasper.android.file_explorer.utils.UIUtils;

public class RocketFragment extends Fragment {

    RelativeLayout primaryLayout;
    RelativeLayout secondaryLayout;

    LinearLayout appsLayout;
    LinearLayout photosLayout;
    LinearLayout musicsLayout;
    LinearLayout videosLayout;
    LinearLayout desktopLayout;

    CircularProgressBar primaryPB;
    CircularProgressBar secondaryPB;

    TextView primaryTV;
    TextView secondaryTV;

    TextView appsTV;
    TextView photosTV;
    TextView musicsTV;
    TextView videosTV;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View contentView = inflater.inflate(R.layout.page_desk, container, false);

        initViews(contentView);
        initData(getContext(), inflater);

        return contentView;
    }

    private void initViews(View contentView) {

        primaryLayout = (RelativeLayout) contentView.findViewById(R.id.page_desk_storage_primary_layout);
        secondaryLayout = (RelativeLayout) contentView.findViewById(R.id.page_desk_storage_secondary_layout);

        appsLayout = (LinearLayout) contentView.findViewById(R.id.page_desk_part_apps_layout);
        photosLayout = (LinearLayout) contentView.findViewById(R.id.page_desk_part_photos_layout);
        musicsLayout = (LinearLayout) contentView.findViewById(R.id.page_desk_part_musics_layout);
        videosLayout = (LinearLayout) contentView.findViewById(R.id.page_desk_part_videos_layout);
        desktopLayout = (LinearLayout) contentView.findViewById(R.id.page_desk_part_desktop_layout);

        primaryPB = (CircularProgressBar) contentView.findViewById(R.id.page_desk_storage_primary_progressbar);
        secondaryPB = (CircularProgressBar) contentView.findViewById(R.id.page_desk_storage_secondary_progressbar);

        primaryTV = (TextView) contentView.findViewById(R.id.page_desk_storage_primary_size_text_view);
        secondaryTV = (TextView) contentView.findViewById(R.id.page_desk_storage_secondary_size_text_view);

        appsTV = (TextView) contentView.findViewById(R.id.page_desk_part_apps_count_text_view);
        photosTV = (TextView) contentView.findViewById(R.id.page_desk_part_photos_count_text_view);
        musicsTV = (TextView) contentView.findViewById(R.id.page_desk_part_musics_count_text_view);
        videosTV = (TextView) contentView.findViewById(R.id.page_desk_part_videos_count_text_view);
    }

    private void initData(Context context, LayoutInflater inflater) {

        Pair<Pair<String, String>, Integer> primaryInfo = FileUtils.getStorageInfo(FileUtils.getPrimaryStoragePath());
        primaryPB.setProgress(primaryInfo.second);
        primaryTV.setText(primaryInfo.first.first + " / " + primaryInfo.first.second);
        primaryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIUtils.closeExtraUI();
                FileUtils.openDocument(FileUtils.getPrimaryStoragePath());
            }
        });

        if (FileUtils.getSecondaryRootFolder() != null && FileUtils.getSecondaryStoragePath() != null && FileUtils.getSecondaryStoragePath().length() > 0) {
            Pair<Pair<String, String>, Integer> secondaryInfo = FileUtils.getStorageInfo(FileUtils.getSecondaryStoragePath());
            secondaryPB.setProgress(secondaryInfo.second);
            secondaryTV.setText(secondaryInfo.first.first + " / " + secondaryInfo.first.second);
            secondaryLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UIUtils.closeExtraUI();
                    FileUtils.openDocument(FileUtils.getSecondaryStoragePath());
                }
            });
        }
        else {
            secondaryPB.setProgress(0);
            secondaryTV.setText("0 GB / 0 GB");
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                final int appsCount = AppUtils.getAppsDict().size();
                final int photosCount = getPhotosCount();
                final int musicsCount = getMusicsCount();
                final int videosCount = getVideosCount();

                FileExplorerActivity.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        appsTV.setText(appsCount + " Apps");
                        photosTV.setText(photosCount + " Photos");
                        musicsTV.setText(musicsCount + " Musics");
                        videosTV.setText(videosCount + " Videos");
                    }
                });
            }
        }).start();

        appsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIUtils.closeExtraUI();
                FileUtils.openDocument("/storage/apps");
            }
        });

        photosLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIUtils.closeExtraUI();
                FileUtils.openDocument("/storage/photos");
            }
        });

        musicsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIUtils.closeExtraUI();
                FileUtils.openDocument("/storage/musics");
            }
        });

        videosLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIUtils.closeExtraUI();
                FileUtils.openDocument("/storage/videos");
            }
        });

        desktopLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.closeExtraUI();
                FileUtils.openDocument(FileUtils.getPrimaryStoragePath() + "/" + "Desktop");
            }
        });
    }

    private int getPhotosCount() {

        int photosCount = 0;

        try {
            String pathOfDoc;
            File fileOfDoc;
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.MediaColumns.DATA};
            Cursor cursor = Core.getInstance().getContentResolver().query(uri, projection, null, null, null);
            assert cursor != null;
            int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            while (cursor.moveToNext()) {
                pathOfDoc = cursor.getString(column_index_data);
                fileOfDoc = new File(pathOfDoc);
                if (fileOfDoc.exists()) {
                    photosCount++;
                }
            }
            cursor.close();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        return photosCount;
    }

    private int getMusicsCount() {

        int musicsCount = 0;

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
                }
            }
            cursor.close();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        return musicsCount;
    }

    private int getVideosCount() {

        int videosCount = 0;

        try {
            String pathOfDoc;
            File fileOfDoc;
            Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.MediaColumns.DATA};
            Cursor cursor = Core.getInstance().getContentResolver().query(uri, projection, null, null, null);
            assert cursor != null;
            int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            while (cursor.moveToNext()) {
                pathOfDoc = cursor.getString(column_index_data);
                fileOfDoc = new File(pathOfDoc);
                if (fileOfDoc.exists()) {
                    videosCount++;
                }
            }
            cursor.close();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        return videosCount;
    }
}