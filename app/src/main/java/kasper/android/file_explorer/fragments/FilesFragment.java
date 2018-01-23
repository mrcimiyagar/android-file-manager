package kasper.android.file_explorer.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kasper.android.file_explorer.R;
import kasper.android.file_explorer.activities.DocsActivity;
import kasper.android.file_explorer.utils.FileUtils;
import kasper.android.file_explorer.utils.UIUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilesFragment extends Fragment {

    private CardView photosCV;
    private CardView musicsCV;
    private CardView videosCV;
    private CardView downloadsCV;

    public FilesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View contentView = inflater.inflate(R.layout.fragment_files, container, false);

        this.initViews(contentView);
        this.initListeners();

        return contentView;
    }

    private void initViews(View contentView) {
        this.photosCV = contentView.findViewById(R.id.fragment_files_photos_card_view);
        this.musicsCV = contentView.findViewById(R.id.fragment_files_musics_card_view);
        this.videosCV = contentView.findViewById(R.id.fragment_files_videos_card_view);
        this.downloadsCV = contentView.findViewById(R.id.fragment_files_downloads_card_view);
    }

    private void initListeners() {

        this.photosCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), DocsActivity.class).putExtra("doc-type", "photos"));
                //UIUtils.closeExtraUI();
                //FileUtils.openDocument("/storage/photos");
            }
        });

        this.musicsCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), DocsActivity.class).putExtra("doc-type", "musics"));
                //UIUtils.closeExtraUI();
                //FileUtils.openDocument("/storage/musics");
            }
        });

        this.videosCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), DocsActivity.class).putExtra("doc-type", "videos"));
                //UIUtils.closeExtraUI();
                //FileUtils.openDocument("/storage/videos");
            }
        });
    }
}
