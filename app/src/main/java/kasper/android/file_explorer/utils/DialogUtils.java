package kasper.android.file_explorer.utils;

import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import kasper.android.file_explorer.R;
import kasper.android.file_explorer.activities.FileExplorerActivity;
import kasper.android.file_explorer.core.Core;
import kasper.android.file_explorer.listeners.OnConfirmDialogListener;
import kasper.android.file_explorer.listeners.OnDialogMessageChangeListener;
import kasper.android.file_explorer.listeners.OnFileRenameListener;

public class DialogUtils {

    private static OnDialogMessageChangeListener dialogMessageChangeListener;

    public static void setFileTransactionMessage(String message) {
        if (dialogMessageChangeListener != null) {
            dialogMessageChangeListener.updateMessage(message);
        }
    }

    public static void setFileTransactionProgress(int progress) {
        if (dialogMessageChangeListener != null) {
            dialogMessageChangeListener.updateProgress(progress);
        }
    }

    public static View getCreateFolderDialogView(LayoutInflater inflater, final String dirPath) {

        View view = inflater.inflate(R.layout.dialog_folder, null);

        final EditText entryET = (EditText) view.findViewById(R.id.EntryDialogFragmentEntryEditText);
        final Button makeBTN = (Button) view.findViewById(R.id.EntryDialogFragmentAction1Button);
        final Button discardBTN = (Button) view.findViewById(R.id.EntryDialogFragmentAction2Button);

        entryET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    tryMakeFolder(dirPath, entryET.getText().toString());
                    FileExplorerActivity.getInstance().recheckEmpty();
                }
                return false;
            }
        });

        makeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryMakeFolder(dirPath, entryET.getText().toString());
                FileExplorerActivity.getInstance().recheckEmpty();
            }
        });

        discardBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIUtils.closeDialogBox();
                UIUtils.closeSoftKeyboard();
            }
        });

        entryET.requestFocus();

        UIUtils.openSoftKeyboard();

        return view;
    }

    private static void tryMakeFolder(String dirPath, String title) {

        if (title.length() > 0) {
            if (FileUtils.createFolder(dirPath, title)) {
                File file = new File(dirPath + File.separator + title);
                UIUtils.notifyFolderInserted(file.getAbsolutePath());
                UIUtils.closeDialogBox();
                UIUtils.closeSoftKeyboard();
            }
            else
                Toast.makeText(Core.getInstance(), "Folder with this name already exists.", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(Core.getInstance(), "Folder name must not be empty.", Toast.LENGTH_SHORT).show();
    }

    public static View getDeleteConfirmDialogView(LayoutInflater inflater, final OnConfirmDialogListener confirmDialogListener) {

        View view = inflater.inflate(R.layout.dialog_confirm, null);

        Button okBTN = (Button) view.findViewById(R.id.dialog_confirm_ok_button);
        Button cancelBTN = (Button) view.findViewById(R.id.dialog_confirm_cancel_button);

        okBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UIUtils.closeDialogBox();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        confirmDialogListener.onOk();
                    }
                }, 300);
            }
        });

        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UIUtils.closeDialogBox();
            }
        });

        return view;
    }

    public static View getFileTransferDialogView(LayoutInflater inflater, String title) {

        View view = inflater.inflate(R.layout.dialog_file_task, null);

        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.dialog_file_task_progressbar);
        TextView titleTV = (TextView) view.findViewById(R.id.dialog_working_title_text_view);
        titleTV.setText(title);

        final TextView fileTransactionMessageTV = (TextView) view.findViewById(R.id.dialog_working_message_text_view);

        fileTransactionMessageTV.setText(title);

        dialogMessageChangeListener = new OnDialogMessageChangeListener() {
            @Override
            public void updateMessage(String message) {

                fileTransactionMessageTV.setText(message);
                FileExplorerActivity.getInstance().recheckEmpty();
            }

            @Override
            public void updateProgress(int progress) {

                progressBar.setProgress(progress);
                FileExplorerActivity.getInstance().recheckEmpty();
            }
        };

        return view;
    }

    public static View getPendingTaskDialogView(LayoutInflater inflater, String message) {

        View view = inflater.inflate(R.layout.dialog_pending_task, null);

        ((TextView)view.findViewById(R.id.dialog_pending_task_message_text_view)).setText(message);

        return view;
    }

    public static View getPropertiesDialogView(LayoutInflater inflater, String path) {

        View dialogView = inflater.inflate(R.layout.dialog_properties, null);

        TextView detailsTV = (TextView) dialogView.findViewById(R.id.dialog_properties_details_text_view);

        File file = new File(path);

        String details = "Last Modified : ".concat(new SimpleDateFormat("dd MMM yyyy hh:mm aa", Locale.ENGLISH).format(new Date(file.lastModified())).concat("\n\n"));

        details += "Path : ".concat(file.getAbsolutePath()).concat("\n\n");

        if (path.endsWith(".KasperLauncherShortcut")) {

            String targetPath = null;

            try {
                targetPath = (String) FileUtils.readObjectFromFile(file);
            }
            catch (Exception ignored) { }

            if (targetPath != null) {
                details = details.concat("Target path : ").concat(targetPath).concat("\n\n");
            }
        }

        if (file.isDirectory()) {

            details = details.concat("Size : ").concat(Formatter.formatFileSize(Core.getInstance(), FileUtils.getSizeOfFolder(file))).concat("\n\n");
        }
        else {

            details = details.concat("Size : ").concat(Formatter.formatFileSize(Core.getInstance(), file.length())).concat("\n\n");
        }

        detailsTV.setText(details);

        Button okBTN = (Button) dialogView.findViewById(R.id.dialog_properties_ok_button);
        okBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UIUtils.closeDialogBox();
            }
        });

        return dialogView;
    }

    public static View getRenameDialogView(LayoutInflater inflater, final String path, final OnFileRenameListener fileRenameListener) {

        View dialogView = inflater.inflate(R.layout.dialog_rename, null);

        final EditText entryET = (EditText) dialogView.findViewById(R.id.rename_file_dialog_entry_edit_text);
        final Button renameBTN = (Button) dialogView.findViewById(R.id.rename_file_dialog_rename_button);
        final Button cancelBTN = (Button) dialogView.findViewById(R.id.rename_file_dialog_discard_button);

        entryET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    tryMakeFolder(path, entryET.getText().toString());
                }
                return false;
            }
        });

        renameBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryRenameFile(path, entryET.getText().toString(), fileRenameListener);
            }
        });

        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIUtils.closeDialogBox();
                UIUtils.closeSoftKeyboard();
            }
        });

        entryET.requestFocus();

        UIUtils.openSoftKeyboard();

        return dialogView;
    }

    private static void tryRenameFile(String path, String newTitle, OnFileRenameListener fileRenameListener) {

        if (FileUtils.testRenameFile(path, newTitle)) {

            UIUtils.closeDialogBox();
            UIUtils.closeSoftKeyboard();
            FileUtils.renameFile(path, newTitle, fileRenameListener);
        }
        else {
            Toast.makeText(Core.getInstance(), "The file with this title already exists.", Toast.LENGTH_LONG).show();
        }
    }
}