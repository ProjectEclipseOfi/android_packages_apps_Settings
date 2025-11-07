package org.eclipse.settings.aboutphone;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.widget.ImageView;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.LayoutPreference;

public class EclipseHeaderController extends AbstractPreferenceController {

    private static final String TAG = "EclipseHeaderController";
    private static final float BLUR_RADIUS = 30f;
    private LayoutPreference mLayoutPreference;

    public EclipseHeaderController(Context context, String key) {
        super(context);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getPreferenceKey() {
        return "eclipse_header";
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mLayoutPreference = screen.findPreference(getPreferenceKey());
        if (mLayoutPreference != null) {
            updateState(mLayoutPreference);
        }
    }

    @Override
    public void updateState(Preference preference) {
        if (mLayoutPreference == null) {
            return;
        }
        ImageView wallpaperImageView = mLayoutPreference.findViewById(R.id.wallpaper_blur_image);
        if (wallpaperImageView != null) {
            try {
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(mContext);
                Drawable wallpaperDrawable = wallpaperManager.getDrawable();
                
                if (wallpaperDrawable == null) {
                    //Log.e(TAG, "Wallpaper null!");
                    return;
                }

                Bitmap wallpaperBitmap = drawableToBitmap(wallpaperDrawable);
                Bitmap blurredBitmap = blurBitmap(mContext, wallpaperBitmap, BLUR_RADIUS);
                wallpaperImageView.setImageBitmap(blurredBitmap);
                //Log.d(TAG, "Wallpaper successfully");
            } catch (Exception e) {
                Log.e(TAG, "Failed to blur wallpaper", e);
            }
        }
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1; 

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    //blur
    private static Bitmap blurBitmap(Context context, Bitmap bitmap, float radius) {
        Bitmap outputBitmap = Bitmap.createBitmap(bitmap);
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outputBitmap);
        blurScript.setRadius(radius > 25f ? 25f : radius);  //blur raduis
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);
        allOut.copyTo(outputBitmap);
        rs.destroy();
        return outputBitmap;
    }
}
