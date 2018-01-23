package kasper.android.file_explorer.utils;

import android.app.WallpaperManager;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;

import kasper.android.file_explorer.core.Core;
import kasper.android.file_explorer.models.BackTask;

public class ResUtils {

    public static void setup() {

        /*WallpaperManager wallpaperManager = WallpaperManager.getInstance(Core.getInstance());
        background = ImageUtils.bitmapToDrawable(Bitmap.createScaledBitmap(ImageUtils.drawableToBitmap(wallpaperManager.getDrawable()), 720, 1280, false));

        blurredBackground = ImageUtils.bitmapToDrawable(ImageUtils.blurBitmap(ImageUtils
                .drawableToBitmap(background)));*/
    }

    public static void setNewWallpaper(String path) {

        TaskUtils.startNewBackTask(new WallPaperTask(), path);
    }

    public static void setNewRingtone(String path) {

        File ringtoneFile = new File(path);

        ContentValues content = new ContentValues();
        content.put(MediaStore.MediaColumns.DATA, path);
        content.put(MediaStore.MediaColumns.TITLE, FileUtils.getTitleOfPath(path));
        content.put(MediaStore.MediaColumns.SIZE, ringtoneFile.length());
        content.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
        content.put(MediaStore.Audio.Media.DURATION, 0);
        content.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        content.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        content.put(MediaStore.Audio.Media.IS_ALARM, false);
        content.put(MediaStore.Audio.Media.IS_MUSIC, false);

        try {
            Core.getInstance().getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.MediaColumns
                    .DATA + "=\"" + ringtoneFile.getAbsolutePath() + "\"", null);
        }
        catch (IllegalArgumentException ignored) { }

        try {
            Uri newUri = Core.getInstance().getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, content);
            RingtoneManager.setActualDefaultRingtoneUri(Core.getInstance()
                    .getApplicationContext(), RingtoneManager.TYPE_RINGTONE, newUri);
            Toast.makeText(Core.getInstance(), "ringtone set", Toast.LENGTH_SHORT).show();
        }
        catch (IllegalArgumentException ignored) { }
    }

    private static class WallPaperTask extends BackTask {

        @Override
        public void doInBackground(Object... params) {

            String path = (String) params[0];

            WallpaperManager myWallpaperManager = WallpaperManager.getInstance(Core.getInstance());
            try {
                Bitmap myBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(path), 720, 1280, false);
                myWallpaperManager.setBitmap(myBitmap);
                shootToUI(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Core.getInstance(), "Wallpaper changed.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch (Exception ex) { Toast.makeText(Core.getInstance(), "Something went wrong setting wallpaper", Toast.LENGTH_LONG).show(); }

            shootToUI(new Runnable() {
                @Override
                public void run() {
                    UIUtils.closeDialogBox();
                }
            });
        }
    }
}