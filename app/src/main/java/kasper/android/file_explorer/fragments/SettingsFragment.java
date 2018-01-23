package kasper.android.file_explorer.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.provider.DocumentFile;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import kasper.android.file_explorer.R;
import kasper.android.file_explorer.core.Core;
import kasper.android.file_explorer.models.RRAction;
import kasper.android.file_explorer.utils.FileUtils;
import kasper.android.file_explorer.utils.UIUtils;

import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends Fragment {

    private LinearLayout sdCardBTN;
    private LinearLayout disableSDCard;
    private ImageButton backBTN;

    public SettingsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View contentView = inflater.inflate(R.layout.fragment_settings, container, false);

        initView(contentView);
        initData();

        return contentView;
    }

    private void initView(View contentView) {

        sdCardBTN = (LinearLayout) contentView.findViewById(R.id.page_settings_setup_sd_card_button);
        disableSDCard = (LinearLayout) contentView.findViewById(R.id.page_settings_disable_sd_card_button);
        backBTN = (ImageButton) contentView.findViewById(R.id.page_settings_back_image_button);
    }

    private void initData() {

        sdCardBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    File file = new File(FileUtils.getSecondaryStoragePath());

                    if (file.exists()) {

                        if (Build.VERSION.SDK_INT >= 21) {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            UIUtils.runIntentForResult(intent, new RRAction() {
                                @Override
                                public void run(int resultCode, Intent resultData) {
                                    if (resultCode == RESULT_OK) {
                                        Uri treeUri = resultData.getData();
                                        if (!treeUri.getPath().equals(FileUtils.getPrimaryStoragePath())) {
                                            getContext().grantUriPermission(getContext().getPackageName(), treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                            getContext().getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                            FileUtils.setSecondaryRootFolder(DocumentFile.fromTreeUri(Core.getInstance(), treeUri));
                                            final int takeFlags = (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                            Core.getInstance().getContentResolver().takePersistableUriPermission(treeUri, takeFlags);
                                            UIUtils.notifySDCardStateChanged();

                                            String uriStr = treeUri.toString();

                                            File sdCardUriPathFile = new File(FileUtils.getPrimaryRootFolder(), "sdCardUriString.KasperFileExplorer");

                                            try {
                                                if (!sdCardUriPathFile.exists()) {
                                                    sdCardUriPathFile.createNewFile();
                                                }
                                            } catch (IOException ignored) {
                                            }

                                            FileUtils.writeObjectToFile(sdCardUriPathFile, uriStr);
                                        }
                                    }
                                }
                            });
                        }
                    } else {
                        Toast.makeText(Core.getInstance(), "No removable storage found", Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception ignored) {

                }
            }
        });

        disableSDCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileUtils.setSecondaryRootFolder(null);
                UIUtils.notifySDCardStateChanged();
            }
        });

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().onBackPressed();
            }
        });
    }
}