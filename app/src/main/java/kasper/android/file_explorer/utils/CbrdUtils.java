package kasper.android.file_explorer.utils;

import android.support.v4.provider.DocumentFile;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.HashSet;

import kasper.android.file_explorer.core.Core;
import kasper.android.file_explorer.listeners.OnCbrdTransactListener;
import kasper.android.file_explorer.listeners.OnConfirmDialogListener;
import kasper.android.file_explorer.listeners.OnSelectionUpdate;
import kasper.android.file_explorer.models.BackTask;

public class CbrdUtils {

    private static HashSet<String> copiedFilesPaths = new HashSet<>();
    private static boolean cutWhenPaste;
    private static HashSet<String> selectedPaths = new HashSet<>();
    private static OnSelectionUpdate onSelectionUpdate;

    public static int getSelectedFilesCount() { return selectedPaths.size(); }

    public static int getCopiedFilesCount() { return copiedFilesPaths.size(); }

    public static void setup(OnSelectionUpdate onSelectionUpdate) {
        CbrdUtils.onSelectionUpdate = onSelectionUpdate;
    }

    public static void copy(String path) {

        copiedFilesPaths = new HashSet<>();
        copiedFilesPaths.add(path);
        selectedPaths = new HashSet<>();
        cutWhenPaste = false;
    }

    public static void copy() {

        copiedFilesPaths = new HashSet<>(selectedPaths);
        selectedPaths = new HashSet<>();
        cutWhenPaste = false;
    }

    public static void cut(String path) {

        copy(path);
        cutWhenPaste = true;
    }

    public static void cut() {

        copy();
        cutWhenPaste = true;
    }

    public static void paste(final String destFolderPath, OnCbrdTransactListener cbrdTransactListener) {

        TaskUtils.startNewBackTask(new PasteTask(), destFolderPath, cutWhenPaste, cbrdTransactListener
                , new HashSet<>(copiedFilesPaths));

        copiedFilesPaths = new HashSet<>();
        cutWhenPaste = false;
    }

    public static void delete(String path, final OnCbrdTransactListener cbrdTransactListener) {

        final HashSet<String> paths = new HashSet<>();
        paths.add(path);

        UIUtils.showDialogBox(DialogUtils.getDeleteConfirmDialogView(UIUtils.getLayoutInflater(), new OnConfirmDialogListener() {
            @Override
            public void onOk() {

                UIUtils.showDialogBox(DialogUtils.getFileTransferDialogView(UIUtils.getLayoutInflater(), "Deleting files"));

                TaskUtils.startNewBackTask(new DeleteTask(), cbrdTransactListener, paths);
            }
        }));
    }

    public static void delete(final OnCbrdTransactListener cbrdTransactListener) {

        UIUtils.showDialogBox(DialogUtils.getDeleteConfirmDialogView(UIUtils.getLayoutInflater(), new OnConfirmDialogListener() {
            @Override
            public void onOk() {

                UIUtils.showDialogBox(DialogUtils.getFileTransferDialogView(UIUtils.getLayoutInflater(), "Deleting files..."));

                TaskUtils.startNewBackTask(new DeleteTask(), cbrdTransactListener, new HashSet<>(selectedPaths));
            }
        }));
    }

    public static void delete(final OnCbrdTransactListener cbrdTransactListener, final Runnable onDeleteAccenpted) {

        UIUtils.showDialogBox(DialogUtils.getDeleteConfirmDialogView(UIUtils.getLayoutInflater(), new OnConfirmDialogListener() {
            @Override
            public void onOk() {

                UIUtils.showDialogBox(DialogUtils.getFileTransferDialogView(UIUtils.getLayoutInflater(), "Deleting files..."));

                TaskUtils.startNewBackTask(new DeleteTask(), cbrdTransactListener, new HashSet<>(selectedPaths));

                onDeleteAccenpted.run();
            }
        }));
    }

    public static void selectPath(String path) {

        if (!selectedPaths.contains(path)) {
            selectedPaths.add(path);
            onSelectionUpdate.selectionStatesChanged();
        }
    }

    public static void deselectPath(String path) {

        if (selectedPaths.contains(path)) {
            selectedPaths.remove(path);
            onSelectionUpdate.selectionStatesChanged();
        }
    }

    public static boolean isPathSelected(String path) {

        return selectedPaths.contains(path);
    }

    private static class PasteTask extends BackTask {

        @Override
        public void doInBackground(Object... params) {

            final String destFolderPath = (String) params[0];
            final boolean cutWhenPaste = (Boolean) params[1];
            final OnCbrdTransactListener cbrdTransactListener = (OnCbrdTransactListener) params[2];
            final HashSet<String> paths = (HashSet<String>) params[3];
            final HashSet<String> newPathsOnCut = new HashSet<>();

            boolean someFilesExisted = false;

            boolean isFolder = false;

            int counter = 0;

            for (final String path : paths) {

                counter++;

                final int currentProgress = (int)(((float)counter / (float)paths.size()) * 100);

                shootToUI(new Runnable() {
                    @Override
                    public void run() {
                        cbrdTransactListener.fileTransacting(FileUtils.getTitleOfPath(path), currentProgress);
                    }
                });

                boolean done = false;

                try {

                    if (FileUtils.isFileInPrimaryStorage(path)) {

                        File sourceFile = new File(path);

                        if (FileUtils.isFileInPrimaryStorage(destFolderPath)) {

                            File finalFile = new File(destFolderPath + File.separator + FileUtils.getTitleOfPath(path));

                            if (!finalFile.exists()) {

                                if (sourceFile.isDirectory()) {
                                    finalFile.mkdirs();
                                    isFolder = true;
                                } else {
                                    finalFile.createNewFile();
                                }

                                FileUtils.copyFile(sourceFile, finalFile);

                                done = true;
                            }
                        }
                        else {

                            DocumentFile parentFile = FileUtils.getFileInSecondaryStorage(destFolderPath);

                            if (parentFile.findFile(FileUtils.getTitleOfPath(path)) == null) {
                                if (sourceFile.isDirectory()) {
                                    parentFile.createDirectory(FileUtils.getTitleOfPath(path));
                                    isFolder = true;
                                } else {
                                    parentFile.createFile(FileUtils.getMimeType(path), FileUtils.getTitleOfPath(path));
                                }

                                DocumentFile finalFile = FileUtils.getFileInSecondaryStorage(destFolderPath + File.separator + FileUtils.getTitleOfPath(path));

                                FileUtils.copyFile(sourceFile, finalFile);

                                done = true;
                            }
                        }
                    }
                    else {

                        DocumentFile sourceFile = FileUtils.getFileInSecondaryStorage(path);

                        if (FileUtils.isFileInPrimaryStorage(destFolderPath)) {

                            File finalFile = new File(destFolderPath, FileUtils.getTitleOfPath(sourceFile.getUri().getPath()));

                            if (!finalFile.exists()) {

                                if (sourceFile.isDirectory()) {
                                    finalFile.mkdirs();
                                    isFolder = true;
                                } else {
                                    finalFile.createNewFile();
                                }

                                FileUtils.copyFile(sourceFile, finalFile);

                                done = true;
                            }
                        }
                        else {

                            DocumentFile parentFile = FileUtils.getFileInSecondaryStorage(destFolderPath);

                            if (parentFile.findFile(FileUtils.getTitleOfPath(path)) == null) {
                                if (sourceFile.isDirectory()) {
                                    parentFile.createDirectory(FileUtils.getTitleOfPath(path));
                                    isFolder = true;
                                } else {
                                    parentFile.createFile(FileUtils.getMimeType(path), FileUtils.getTitleOfPath(path));
                                }

                                DocumentFile finalFile = FileUtils.getFileInSecondaryStorage(destFolderPath + File.separator + FileUtils.getTitleOfPath(path));

                                FileUtils.copyFile(sourceFile, finalFile);

                                done = true;
                            }
                        }
                    }

                    if (done) {

                        if (cutWhenPaste) {

                            FileUtils.deleteFile(new File(path));
                        }

                        final String finalPath = destFolderPath + File.separator + FileUtils.getTitleOfPath(path);

                        if (isFolder) {

                            shootToUI(new Runnable() {
                                @Override
                                public void run() {
                                    UIUtils.notifyFolderInserted(finalPath);
                                }
                            });
                        }
                        else {
                            shootToUI(new Runnable() {
                                @Override
                                public void run() {
                                    UIUtils.notifyDocInserted(finalPath);
                                }
                            });
                        }

                        if (cutWhenPaste) {
                            if (isFolder) {
                                shootToUI(new Runnable() {
                                    @Override
                                    public void run() {
                                        UIUtils.notifyFolderDeleted(path);
                                    }
                                });
                            }
                            else {
                                shootToUI(new Runnable() {
                                    @Override
                                    public void run() {
                                        UIUtils.notifyDocDeleted(path);
                                    }
                                });
                            }
                            newPathsOnCut.add(finalPath);
                        }
                        else {
                            newPathsOnCut.add(path);
                        }
                    }
                    else {
                        someFilesExisted = true;
                    }
                }
                catch (Exception ignored) { }
            }

            copiedFilesPaths = newPathsOnCut;

            if (someFilesExisted) {
                shootToUI(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Core.getInstance(), "one or more_dark files already exist in destination", Toast.LENGTH_LONG).show();
                    }
                });
            }

            shootToUI(new Runnable() {
                @Override
                public void run() {
                    cbrdTransactListener.workingQueueDone();
                }
            });
        }
    }

    private static class DeleteTask extends BackTask {

        @Override
        public void doInBackground(final Object... params) {

            final OnCbrdTransactListener cbrdTransactListener = (OnCbrdTransactListener) params[0];
            final HashSet<String> paths = (HashSet<String>) params[1];

            int counter = 0;

            for (final String tempPath : paths) {

                counter++;

                final int currentProgress = (int)(((float)counter / (float)paths.size()) * 100);

                shootToUI(new Runnable() {
                    @Override
                    public void run() {
                        cbrdTransactListener.fileTransacting(FileUtils.getTitleOfPath(tempPath), currentProgress);
                    }
                });

                File file = new File(tempPath);

                final boolean isFolder = file.isDirectory();

                FileUtils.deleteFile(file);

                shootToUI(new Runnable() {
                    @Override
                    public void run() {

                        if (isFolder) {
                            UIUtils.notifyFolderDeleted(tempPath);
                        }
                        else {
                            UIUtils.notifyDocDeleted(tempPath);
                        }
                    }
                });
            }

            for (String deletedPath : paths) {
                deselectPath(deletedPath);
            }

            shootToUI(new Runnable() {
                @Override
                public void run() {
                    cbrdTransactListener.workingQueueDone();
                }
            });
        }
    }
}