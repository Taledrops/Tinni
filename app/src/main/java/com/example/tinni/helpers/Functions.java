package com.example.tinni.helpers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.exifinterface.media.ExifInterface;

import com.example.tinni.R;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <h1>Functions</h1>
 * Class with various functions for multiple usage
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   19.06.2020
 */

public class Functions
{
    /**
     * <h2>PX from DP</h2>
     * Get the PX value for a given DP value
     *
     * Source:
     * https://stackoverflow.com/a/9904844/2700965
     *
     * @param context the corresponding context
     * @param dp the DP value
     * @return the PX value
     */

    public int pxFromDp(Context context, int dp)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * <h2>Paint Drawable</h2>
     * Color the given drawable and set as source for given ImageView
     *
     * @param imageView ImageView to add drawable to
     * @param drawable resource ID of drawable
     * @param color id of color
     */

    public void paintDrawable (ImageView imageView, int drawable, int color)
    {
        Drawable d = ContextCompat.getDrawable(imageView.getContext(), drawable);
        if (d != null)
        {
            d = DrawableCompat.wrap(d);
            d = d.mutate();
            DrawableCompat.setTint(d, ContextCompat.getColor(imageView.getContext(), color));
            imageView.setImageDrawable(d);
        }
    }

    /**
     * <h2>Milliseconds to Timer</h2>
     * Converts milliseconds to minutes:seconds
     *
     * Source:
     * https://stackoverflow.com/a/36426019/2700965
     *
     * @param milliseconds milliseconds to convert
     * @return the converted time as a String
     */

    public String millisecondsToTimer(long milliseconds)
    {
        String finalTimerString = "";
        String secondsString;
        String minutesString;

        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (hours > 0)
        {
            finalTimerString = hours + ":";
        }

        if (seconds < 10)
        {
            secondsString = "0" + seconds;
        } else
        {
            secondsString = "" + seconds;
        }

        if (minutes < 10)
        {
            minutesString = "0" + minutes;
        } else
        {
            minutesString = "" + minutes;
        }

        finalTimerString = finalTimerString + minutesString + ":" + secondsString;

        return finalTimerString;
    }

    /**
     * <h2>Get Total Time</h2>
     * Compute the total time and transform it into a human-readable time
     *
     * Source:
     * https://stackoverflow.com/a/23215152/2700965
     *
     * @param context the current context
     * @param date The date to transform
     */

    public String getTotalTime (Context context, long date)
    {
        List<Long> times = Arrays.asList(
                TimeUnit.DAYS.toMillis(365),
                TimeUnit.DAYS.toMillis(30),
                TimeUnit.DAYS.toMillis(1),
                TimeUnit.HOURS.toMillis(1),
                TimeUnit.MINUTES.toMillis(1),
                TimeUnit.SECONDS.toMillis(1) );

        List<String> singleTimes = Arrays.asList(
                context.getResources().getString(R.string.year),
                context.getResources().getString(R.string.month),
                context.getResources().getString(R.string.day),
                context.getResources().getString(R.string.hour),
                context.getResources().getString(R.string.minute),
                context.getResources().getString(R.string.second));

        List<String> multiTimes = Arrays.asList(
                context.getResources().getString(R.string.years),
                context.getResources().getString(R.string.months),
                context.getResources().getString(R.string.days),
                context.getResources().getString(R.string.hours),
                context.getResources().getString(R.string.minutes),
                context.getResources().getString(R.string.seconds));

        long now = System.currentTimeMillis();
        date = now - (date * 1000);
        long timeAgo = now - date;
        StringBuilder res = new StringBuilder();
        for(int i = 0; i < times.size(); i++)
        {
            Long current = times.get(i);
            long temp = timeAgo / current;
            if(temp > 0)
            {
                if (temp != 1)
                {
                    res.append(temp).append(" ").append(multiTimes.get(i));
                }
                else
                {
                    res.append(temp).append(" ").append(singleTimes.get(i));
                }
                break;
            }
        }
        if(!res.toString().equals(""))
        {
            return res.toString();
        }
        else
        {
            return context.getResources().getString(R.string.short_time);
        }
    }

    /**
     * <h2>Rotate Bitmap</h2>
     * Rotates the bitmap in case a default rotation is specified in the image metas
     *
     * Source:
     * https://stackoverflow.com/a/36426019/2700965
     *
     * @param context the current context
     * @param uri the uri of the selected image file
     * @param bitmap The bitmap file
     * @return the rotated bitmap
     */

    public Bitmap rotateBitmap (Context context, Uri uri, Bitmap bitmap)
    {
        ExifInterface ei;
        Bitmap rotatedBitmap = null;
        try
        {
            ei = new ExifInterface(PathUtil.getRealPath(context, uri));
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return rotatedBitmap;
    }

    /**
     * <h2>Rotate Image</h2>
     * Does the actual rotation on the bitmap file
     *
     * Source:
     * https://stackoverflow.com/a/36426019/2700965
     *
     * @param source the bitmap file
     * @param angle the angle the bitmap should rotate
     * @return the rotated bitmap
     */

    public static Bitmap rotateImage(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    /**
     * <h2>Have Same Aspect Ratio</h2>
     * Checks whether two {@link RectF} have the same aspect ratio.
     *
     * Source: https://github.com/flavioarfaria/KenBurnsView/blob/master/library/src/main/java/com/flaviofaria/kenburnsview/MathUtils.java
     *
     * Edited by: Nassim Amar
     *
     * @param r1 the first rect.
     * @param r2  the second rect.
     * @return {@code true} if both rectangles have the same aspect ratio,
     * {@code false} otherwise.
     */

    public boolean haveSameAspectRatio(RectF r1, RectF r2)
    {
        float srcRectRatio = truncate(getRectRatio(r1), 3);
        float dstRectRatio = truncate(getRectRatio(r2), 3);

        return (Math.abs(srcRectRatio-dstRectRatio) <= 0.01f);
    }

    /**
     * <h2>get Rect Ratio</h2>
     * Computes the aspect ratio of a given rect.
     *
     * Source: https://github.com/flavioarfaria/KenBurnsView/blob/master/library/src/main/java/com/flaviofaria/kenburnsview/MathUtils.java
     *
     * Edited by: Nassim Amar
     *
     * @param rect the rect to have its aspect ratio computed.
     * @return the rect aspect ratio.
     */

    public float getRectRatio(RectF rect)
    {
        return rect.width() / rect.height();
    }

    /**
     * <h2>Truncate</h2>
     * Truncates a float number {@code f} to {@code decimalPlaces}.
     *
     * Source: https://github.com/flavioarfaria/KenBurnsView/blob/master/library/src/main/java/com/flaviofaria/kenburnsview/MathUtils.java
     *
     * Edited by: Nassim Amar
     *
     * @param f the number to be truncated.
     * @param decimalPlaces the amount of decimals that {@code f}
     * will be truncated to.
     * @return a truncated representation of {@code f}.
     */

    public float truncate(float f, int decimalPlaces)
    {
        float decimalShift = (float) Math.pow(10, decimalPlaces);
        return Math.round(f * decimalShift) / decimalShift;
    }

    /**
     * <h2>Create notification builder</h2>
     * Creates a default notification builder
     *
     * @param context the current context
     * @param channelId the ID of the channel
     * @param importance the importance constant
     *
     * @return an instance of NotificationCompat.Builder
     */

    public NotificationCompat.Builder createNotificationBuilder(Context context, String channelId, int importance)
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            String channelName = context.getResources().getString(R.string.app_name);
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationChannel.setLightColor(ContextCompat.getColor(context, R.color.colorAccent));
            notificationChannel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PRIVATE);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null)
            {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        return new NotificationCompat.Builder(context, channelId);
    }

}
