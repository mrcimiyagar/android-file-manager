package kasper.android.file_explorer.utils;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.provider.DocumentFile;
import android.support.v4.util.Pair;
import android.text.format.Formatter;
import android.webkit.MimeTypeMap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import kasper.android.file_explorer.core.Core;
import kasper.android.file_explorer.listeners.OnFileRenameListener;
import kasper.android.file_explorer.listeners.OnFileToUIListener;
import kasper.android.file_explorer.models.BackTask;

public class FileUtils {

    final static String shortcutExtension = ".KasperLauncherShortcut";

    private static File primaryRootFolder;
    public static File getPrimaryRootFolder() { return primaryRootFolder; }

    private static DocumentFile secondaryRootFolder;
    public static void setSecondaryRootFolder(DocumentFile df) {
        secondaryRootFolder = df;
    }
    public static DocumentFile getSecondaryRootFolder() { return secondaryRootFolder; }

    private static OnFileToUIListener fileToUIListener;
    public static void setFileToUIListener(OnFileToUIListener fileToUIListener) { FileUtils.fileToUIListener = fileToUIListener; }

    public static void setup() {

        primaryRootFolder = Environment.getExternalStorageDirectory();
    }

    @Nullable
    public static Pair<ArrayList<String>, ArrayList<String>> getFilesOfFolder(String folderPath) {

        if (folderPath.equals("primaryStorage")) {
            folderPath = primaryRootFolder.getPath();
        }
        else if (folderPath.equals("secondaryStorage")) {

            if (secondaryRootFolder != null) {

                File fileList[] = new File("/storage_dark/").listFiles();
                for (File file : fileList) {
                    if (!file.getAbsolutePath().equalsIgnoreCase(Environment.getExternalStorageDirectory()
                            .getAbsolutePath()) && file.isDirectory() && file.canRead() && file.getName().contains("-")) {
                        folderPath = file.getAbsolutePath();
                        break;
                    }
                }
            }
        }

        if (folderPath.length() == 0) {
            return null;
        }

        ArrayList<String> foldersList = new ArrayList<>();
        ArrayList<String> docsList = new ArrayList<>();

        File folder = new File(folderPath);

        if (folder.exists()) {

            File[] filesList = folder.listFiles();

            if (filesList != null) {

                for (File file : filesList) {

                    if (file.isDirectory()) {
                        foldersList.add(file.getAbsolutePath());
                    } else {
                        docsList.add(file.getAbsolutePath());
                    }
                }
            }
        }

        Collections.sort(foldersList, new Comparator<String>() {
            @Override
            public int compare(String p1, String p2) {
                return FileUtils.getTitleOfPath(p1).compareToIgnoreCase(FileUtils.getTitleOfPath(p2));
            }
        });

        Collections.sort(docsList, new Comparator<String>() {
            @Override
            public int compare(String p1, String p2) {
                return FileUtils.getTitleOfPath(p1).compareToIgnoreCase(FileUtils.getTitleOfPath(p2));
            }
        });

        return new Pair<>(foldersList, docsList);
    }

    @NonNull
    public static String getTitleOfPath(String path) {

        int index = path.lastIndexOf("/");
        if (index > 0 && index < path.length() - 1) {
            return path.substring(index + 1);
        }
        else {
            return "";
        }
    }

    public static boolean isFileInPrimaryStorage(String path) {

        return path.startsWith(primaryRootFolder.getAbsolutePath());
    }

    @NonNull
    public static String getPrimaryStoragePath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    public static String getSecondaryStoragePath() {

        try {
            String secPath = null;

            File fileList[] = new File("/storage/").listFiles();
            for (File file : fileList) {

                if (!file.getAbsolutePath().equalsIgnoreCase(Environment.getExternalStorageDirectory()
                        .getAbsolutePath()) && file.isDirectory() && file.canRead()) {
                    secPath = file.getAbsolutePath();
                    break;
                }
            }

            return secPath;
        }
        catch (Exception ignored) {

        }

        return "";
    }

    @Nullable
    public static Object readObjectFromFile(File file) {

        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream is = new ObjectInputStream(fis);
            Object object = is.readObject();
            is.close();
            fis.close();

            return object;
        }
        catch (Exception ignored) { }

        return null;
    }

    public static void writeObjectToFile(File file, Object object) {

        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(object);
            os.close();
            fos.close();
        }
        catch (Exception ignored) { }
    }

    public static void copyFile(File sourceFile, File finalFile) {

        try {

            if (sourceFile.isDirectory()) {

                File[] childFiles = sourceFile.listFiles();

                if (childFiles != null) {
                    for (File childFile : childFiles) {
                        String childTitle = getTitleOfPath(childFile.getAbsolutePath());
                        File finalChildFile = new File(finalFile.getAbsolutePath(), childTitle);
                        if (childFile.isDirectory()) {
                            finalChildFile.mkdirs();
                        }
                        else {
                            finalChildFile.createNewFile();
                        }
                        copyFile(childFile, finalChildFile);
                    }
                }
            }
            else {

                InputStream in = new FileInputStream(sourceFile);
                OutputStream out = new FileOutputStream(finalFile);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) out.write(buffer, 0, read);
                in.close();
                out.flush();
                out.close();
            }
        }
        catch (Exception ignored) { }
    }

    public static void copyFile(File sourceFile, DocumentFile finalFile) {

        try {

            if (sourceFile.isDirectory()) {

                File[] childFiles = sourceFile.listFiles();

                if (childFiles != null) {
                    for (File childFile : childFiles) {
                        String childTitle = getTitleOfPath(childFile.getAbsolutePath());
                        DocumentFile finalChildFile;
                        if (childFile.isDirectory()) {
                            finalChildFile = finalFile.createDirectory(childTitle);
                        }
                        else {
                            finalChildFile = finalFile.createFile(getMimeType(sourceFile.getAbsolutePath()), childTitle);
                        }
                        copyFile(childFile, finalChildFile);
                    }
                }
            }
            else {

                InputStream in;
                OutputStream out;

                try {

                    in = new FileInputStream(sourceFile);
                    out = Core.getInstance().getContentResolver().openOutputStream(finalFile.getUri());

                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        assert out != null;
                        out.write(buffer, 0, read);
                    }

                    in.close();
                    assert out != null;
                    out.flush();
                    out.close();
                }
                catch (Exception ignored) { }
            }
        }
        catch (Exception ignored) { }
    }

    public static void copyFile(DocumentFile sourceFile, File finalFile) {

        try {

            if (sourceFile.isDirectory()) {

                DocumentFile[] childFiles = sourceFile.listFiles();

                if (childFiles != null) {
                    for (DocumentFile childFile : childFiles) {
                        String childTitle = getTitleOfPath(childFile.getUri().getPath());
                        File finalChildFile = new File(finalFile, childTitle);
                        if (childFile.isDirectory()) {
                            finalChildFile.mkdirs();
                        }
                        else {
                            finalChildFile.createNewFile();
                        }
                        copyFile(childFile, finalChildFile);
                    }
                }
            }
            else {

                InputStream in;
                OutputStream out;

                try {

                    in = Core.getInstance().getContentResolver().openInputStream(sourceFile.getUri());
                    out = new FileOutputStream(finalFile);

                    byte[] buffer = new byte[1024];
                    int read;
                    assert in != null;
                    while ((read = in.read(buffer)) != -1) out.write(buffer, 0, read);

                    in.close();
                    out.flush();
                    out.close();
                }
                catch (Exception ignored) { }
            }
        }
        catch (Exception ignored) { }
    }

    public static void copyFile(DocumentFile sourceFile, DocumentFile finalFile) {

        try {
            if (sourceFile.isDirectory()) {

                DocumentFile[] childFiles = sourceFile.listFiles();

                if (childFiles != null) {
                    for (DocumentFile childFile : childFiles) {
                        DocumentFile finalChildFile;
                        if (childFile.isDirectory()) {
                            finalChildFile = finalFile.createDirectory(childFile.getName());
                        }
                        else {
                            finalChildFile = finalFile.createFile(childFile.getType(), childFile.getName());
                        }
                        copyFile(childFile, finalChildFile);
                    }
                }
            }
            else {

                InputStream in;
                OutputStream out;

                try {

                    in = Core.getInstance().getContentResolver().openInputStream(sourceFile.getUri());
                    out = Core.getInstance().getContentResolver().openOutputStream(finalFile.getUri());

                    byte[] buffer = new byte[1024];
                    int read;
                    assert in != null;
                    while ((read = in.read(buffer)) != -1) {
                        assert out != null;
                        out.write(buffer, 0, read);
                    }

                    in.close();
                    assert out != null;
                    out.flush();
                    out.close();
                }
                catch (Exception ignored) { }
            }
        }
        catch (Exception ignored) { }
    }

    public static void deleteFile(File path) {

        if (path.exists()) {
            if (!FileUtils.isFileInPrimaryStorage(path.getAbsolutePath())) {
                DocumentFile file = getFileInSecondaryStorage(path.getAbsolutePath());
                if (file != null) {
                    file.delete();
                }
            }
            else {
                if (path.isDirectory()) {
                    for (File file : path.listFiles()) {
                        if (file.isDirectory())
                            deleteFile(file);
                        file.delete();
                    }
                }
                path.delete();
            }
        }
    }

    @Nullable
    public static String getMimeType(String url) {

        try {
            if (url.endsWith(shortcutExtension)) {
                url = url.substring(0, url.length() - shortcutExtension.length());
            }
            String mimeType = URLConnection.guessContentTypeFromName(url);
            return mimeType == null ? "document" : mimeType;
        }
        catch (Exception ignored) { return null; }
    }

    public static boolean createFolder(String parentPath, String title) {

        if (!isFileInPrimaryStorage(parentPath + File.separator + title)) {
            DocumentFile parentFolder = getFileInSecondaryStorage(parentPath);
            if (parentFolder != null) {
                if (parentFolder.findFile(title) == null) {
                    if (parentFolder.isDirectory()) {
                        parentFolder.createDirectory(title);
                        return true;
                    }
                }
            }
        }
        else {
            File file = new File(parentPath + File.separator + title);
            if (!file.exists()) {
                if (file.mkdirs()) {
                    return true;
                }
            }
        }

        return false;
    }

    @Nullable
    public static DocumentFile getFileInSecondaryStorage(String path) {

        if (!isFileInPrimaryStorage(path)) {

            String[] namesChain = path.split("/");

            DocumentFile temp = secondaryRootFolder;

            for (int counter = 3; counter < namesChain.length; counter++) {

                temp = temp.findFile(namesChain[counter]);

                if (temp == null) {
                    return null;
                }
            }

            return temp;
        }

        return null;
    }

    public static long getSizeOfFolder(File dir) {

        if (dir.exists()) {
            long result = 0;
            File[] fileList = dir.listFiles();
            if (fileList != null) {
                for (File aFileList : fileList) {
                    if (aFileList.isDirectory()) {
                        result += getSizeOfFolder(aFileList);
                    } else {
                        result += aFileList.length();
                    }
                }
                return result;
            }
        }
        return 0;
    }

    public static void openDocumentWith(String path) {

        File file = new File(path);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        if (extension.equalsIgnoreCase("") || mimeType == null)
            intent.setDataAndType(Uri.fromFile(file), "text/*");
        else intent.setDataAndType(Uri.fromFile(file), mimeType);
        Core.getInstance().startActivity(Intent.createChooser(intent, "Choose an Application:").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static void openDocument(String path) {

        UIUtils.closeSoftKeyboard();

        if (path.equals("/storage/photos") || path.equals("/storage/musics") || path.equals("/storage/videos") || path.equals("/storage/apps")) {
            fileToUIListener.openFolder(path);
        }
        else {
            File file = new File(path);

            if (file.isDirectory()) {

                fileToUIListener.openFolder(path);
            }
            else {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setType(getMimeType(path));

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    intent.setDataAndType(FileProvider.getUriForFile(Core.getInstance(),
                            Core.getInstance().getApplicationContext().getPackageName()
                                    + ".kasper.android.file_explorer.utils", file), getMimeType(path));
                }
                else {
                    intent.setDataAndType(Uri.fromFile(file), getMimeType(path));
                }

                try {
                    Core.getInstance().startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
                catch (ActivityNotFoundException ignored) { openDocumentWith(path); }
            }
        }
    }

    public static void createShortcutOf(String title, String targetPath) {

        File desktopDir = new File(FileUtils.getPrimaryRootFolder(), "Desktop");
        if (!desktopDir.exists()) {
            try {
                if (desktopDir.mkdirs()) {
                    UIUtils.notifyFolderInserted(desktopDir.getPath());
                }
            }
            catch (Exception ignored) { }
        }
        File sc = new File(desktopDir, title + ".KasperLauncherShortcut");
        try {
            sc.createNewFile();
            FileUtils.writeObjectToFile(sc, targetPath);
        }
        catch (Exception ignored) { }
    }

    public static void zipFile(String sourcePath, String toLocation) {

        UIUtils.showDialogBox(DialogUtils.getPendingTaskDialogView(UIUtils.getLayoutInflater(), "Zipping files..."));

        TaskUtils.startNewBackTask(new ZipTask(), sourcePath, toLocation);
    }

    public static void unzipFile(String inputPath, String outputParentPath) {

        UIUtils.showDialogBox(DialogUtils.getPendingTaskDialogView(UIUtils.getLayoutInflater(), "Unzipping file..."));

        TaskUtils.startNewBackTask(new UnzipTask(), inputPath, outputParentPath);
    }

    public static void renameFile(String path, String newTitle, OnFileRenameListener fileRenameListener) {

        int extensionIndex = -1;

        if (!path.endsWith("/")) {
            extensionIndex = path.substring(path.lastIndexOf("/") + 1).indexOf(".");
        } else {
            path = path.substring(0, path.length() - 1);
        }

        String extension = "";

        if (extensionIndex >= 0) {
            extension = path.substring(path.lastIndexOf("/") + 1).substring(extensionIndex);
        }

        String newPath = path.substring(0, path.lastIndexOf("/") + 1).concat(newTitle).concat(extension);

        if (FileUtils.isFileInPrimaryStorage(path)) {

            File oldFile = new File(path);

            if (oldFile.exists()) {

                File newFile = new File(newPath);

                oldFile.setWritable(true);

                oldFile.renameTo(newFile);

                fileRenameListener.renameFileTo(path, newPath);
            }
        }
        else {

            DocumentFile oldFile = FileUtils.getFileInSecondaryStorage(path);

            if (oldFile != null) {

                oldFile.renameTo(newTitle.concat(extension));

                fileRenameListener.renameFileTo(path, newPath);
            }
        }
    }

    public static boolean testRenameFile(String path, String newTitle) {

        int extensionIndex = -1;

        if (!path.endsWith("/")) {
            extensionIndex = path.substring(path.lastIndexOf("/") + 1).indexOf(".");
        } else {
            path = path.substring(0, path.length() - 1);
        }

        String extension = "";

        if (extensionIndex >= 0) {
            extension = path.substring(path.lastIndexOf("/") + 1).substring(extensionIndex);
        }

        String newPath = path.substring(0, path.lastIndexOf("/") + 1).concat(newTitle).concat(extension);

        File newFile = new File(newPath);

        return !newFile.exists();
    }

    public static Pair<Pair<String, String>, Integer> getStorageInfo(String path) {

        StatFs stat = new StatFs(path);
        double sdAvailSize = (double)stat.getAvailableBlocks() * (double)stat.getBlockSize();
        String freeSpaceStr = Formatter.formatFileSize(Core.getInstance(), (long)sdAvailSize);

        long blockSize, totalBlocks;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            totalBlocks = stat.getBlockCountLong();
        }
        else {
            blockSize = stat.getBlockSize();
            totalBlocks = stat.getBlockCount();
        }
        String totalSpaceStr = Formatter.formatFileSize(Core.getInstance(), totalBlocks * blockSize);

        return new Pair<>(new Pair<>(freeSpaceStr, totalSpaceStr), (int)(100 * ((double)(totalBlocks * blockSize) - sdAvailSize) / (double)(totalBlocks * blockSize)));
    }

    static class ZipTask extends BackTask {

        @Override
        public void doInBackground(Object... params) {

            final String sourcePath = (String) params[0];
            final String toLocation = (String) params[1];

            final int BUFFER = 16384;

            File sourceFile = new File(sourcePath);
            File finalFile = new File(toLocation);

            if (!finalFile.exists()) {
                try {
                    finalFile.createNewFile();
                }
                catch (Exception ignored) { }
            }

            try {
                BufferedInputStream origin;
                FileOutputStream dest = new FileOutputStream(toLocation);
                ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
                if (sourceFile.isDirectory()) {
                    zipSubFolder(out, sourceFile, sourceFile.getParent());
                } else {
                    byte data[] = new byte[BUFFER];
                    FileInputStream fi = new FileInputStream(sourcePath);
                    origin = new BufferedInputStream(fi, BUFFER);
                    ZipEntry entry = new ZipEntry(getLastPathComponent(sourcePath));
                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0, BUFFER)) != -1) {
                        out.write(data, 0, count);
                    }
                }
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            shootToUI(new Runnable() {
                @Override
                public void run() {
                    UIUtils.notifyDocInserted(toLocation);
                    UIUtils.closeDialogBox();
                }
            });
        }

        private static void zipSubFolder(ZipOutputStream out, File folder, String basePath) throws IOException {

            String filePath = folder.getPath().substring(basePath.length());
            out.putNextEntry(new ZipEntry(filePath.endsWith("/") ? filePath : filePath.concat("/")));

            final int BUFFER = 16384;

            File[] fileList = folder.listFiles();
            BufferedInputStream origin;
            for (File file : fileList) {
                if (file.isDirectory()) {
                    zipSubFolder(out, file, basePath);
                }
                else {
                    byte data[] = new byte[BUFFER];
                    String unmodifiedFilePath = file.getPath();
                    String relativePath = unmodifiedFilePath.substring(basePath.length());
                    FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                    origin = new BufferedInputStream(fi, BUFFER);
                    ZipEntry entry = new ZipEntry(relativePath);
                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0, BUFFER)) != -1) {
                        out.write(data, 0, count);
                    }
                    origin.close();
                }
            }
        }

        public static String getLastPathComponent(String filePath) {

            String[] segments = filePath.split("/");
            if (segments.length == 0) return "";
            return segments[segments.length - 1];
        }
    }

    static class UnzipTask extends BackTask {

        @Override
        public void doInBackground(Object... params) {

            final String inputPath = (String) params[0];
            final String outputParentPath = (String) params[1];

            InputStream is;
            ZipInputStream zis;
            try
            {
                is = new FileInputStream(inputPath);
                zis = new ZipInputStream(new BufferedInputStream(is));
                ZipEntry ze;
                byte[] buffer = new byte[16384];
                int count;

                while ((ze = zis.getNextEntry()) != null)
                {
                    final String filename = ze.getName().startsWith("/") ? ze.getName() : "/".concat(ze.getName());

                    if (ze.isDirectory()) {
                        File fmd = new File(outputParentPath + filename);
                        fmd.mkdirs();
                        shootToUI(new Runnable() {
                            @Override
                            public void run() {
                                UIUtils.notifyFolderInserted(outputParentPath + filename);
                            }
                        });
                        continue;
                    }

                    FileOutputStream fout = new FileOutputStream(outputParentPath + filename);

                    while ((count = zis.read(buffer)) != -1)
                    {
                        fout.write(buffer, 0, count);
                    }

                    fout.close();
                    zis.closeEntry();

                    shootToUI(new Runnable() {
                        @Override
                        public void run() {
                            UIUtils.notifyDocInserted(outputParentPath + filename);
                        }
                    });
                }

                zis.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }

            shootToUI(new Runnable() {
                @Override
                public void run() {

                    UIUtils.closeDialogBox();
                }
            });
        }
    }
}