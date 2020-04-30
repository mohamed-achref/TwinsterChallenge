package com.khattech.twinsterchallenge.net;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.TypedValue;

/**
 * Created by Mohamed Achref on 4/30/20.
 */
public class Constant {

    public static final String BASE_URL = "https://api.themoviedb.org/3/movie/";
    public static final String URL_IMAGE = "https://image.tmdb.org/t/p/w500/";

    public static final String API_TOKEN = "e16f9ec421f01f05db45a6d069d84d56";
    public static final String PREFS_USER_CONNECTED = "prefs_user_connected";
    public static final String PREFS_MOVIE = "prefs_movie";
    public static final String PREFS_FAVORITE_MOVIE = "prefs_favorite_movie";


    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, pixels, pixels, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static float dpToPx(float dp, Context context) {
        // Converts dip into its equivalent px
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
}
