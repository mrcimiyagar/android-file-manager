package kasper.android.file_explorer.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class HWareUtils {

    private static int screenSizeX;
    public static int getScreenSizeX() { return screenSizeX; }

    private static int screenSizeY;
    public static int getScreenSizeY() { return screenSizeY; }

    private static float screenSizeDensity;
    public static float getScreenDensity() { return screenSizeDensity; }

    public static void setup(Context context) {

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        screenSizeX = dm.widthPixels;
        screenSizeY = dm.heightPixels;
        screenSizeDensity = dm.density;
    }
}