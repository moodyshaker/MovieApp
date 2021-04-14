package com.example.MovieDB.utils;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ViewGroup;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import com.example.MovieDB.R;
import com.example.MovieDB.endpoints.AppConstants;
import com.example.MovieDB.ui.activity.NowPlaying_OnTheAir;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import dmax.dialog.SpotsDialog;

public class Utils {

    public static void pushNotification(Context context, String title, String message, int icon, int largeIcon) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(AppConstants.NEW_MOVIE_CHANNEL_ID,
                    AppConstants.NEW_MOVIE_CHANNEL_NAME,
                    AppConstants.NEW_MOVIE_CHANNEL_IMPORTANCE);
            manager.createNotificationChannel(channel);
            builder = new Notification.Builder(context, AppConstants.NEW_MOVIE_CHANNEL_ID);
        } else {
            builder = new Notification.Builder(context);
        }

        Notification notification = builder.setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIcon))
                .setStyle(new Notification.BigTextStyle()
                        .bigText(message)
                ).setAutoCancel(true)
                .build();
        Intent i = new Intent(context, NowPlaying_OnTheAir.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 123, i, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        manager.notify(AppConstants.NOTIFICATION_ID, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void exitDialog(Activity activity) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle("Exit");
        dialog.setIcon(R.drawable.movie_icon);
        dialog.setMessage("Do you want to exit ?");
        dialog.setPositiveButton("Yes", (dialog1, which) -> activity.finishAffinity());
        dialog.setNegativeButton("No", (dialog1, which) -> {

        });
        dialog.setCancelable(false);
        dialog.show();
    }

    public static void goActivity(Activity activity, Class<?> des) {
        Intent i = new Intent(activity, des);
        activity.startActivity(i);
    }

    public static void goActivityWithFinish(Activity activity, Class<?> des) {
        Intent i = new Intent(activity, des);
        activity.startActivity(i);
        activity.finish();
    }

    public static AlertDialog showLoadingDialog(Context context) {
        return new SpotsDialog.Builder()
                .setContext(context)
                .setTheme(R.style.SpotDialog)
                .setCancelable(false)
                .build();
    }

    public static void changeLanguage(Context context, String languageToLoad) {
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());
    }

    public static Dialog transparentDialog(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.loading_dialog);
        return dialog;
    }

    public static void messageDialog(Activity context, String title, String message) {
        AlertDialog.Builder messageDialog = new AlertDialog.Builder(context);
        messageDialog.setCancelable(false);
        messageDialog.setTitle(title);
        messageDialog.setMessage(message);
        messageDialog.setPositiveButton("OK", (d, w) ->
                goActivityWithFinish(context, NowPlaying_OnTheAir.class));
        messageDialog.show();
    }

    public static void requestPermissionForPhoto(Activity activity) {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(activity, permissions, AppConstants.REQUEST_CODE_PERMISSION);
    }

    public static boolean checkPermissionForPhoto(Context context) {
        boolean isGranted = false;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            isGranted = true;
        }
        return isGranted;
    }

    public static void openGallery(Activity activity) {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        activity.startActivityForResult(i, AppConstants.IMAGE_GALLERY_REQUEST);
    }

    public static void openCamera(Activity activity) {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(i, AppConstants.IMAGE_CAPTURE_REQUEST);
    }

    public static String getPathFromURI(Context context, Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                res = cursor.getString(column_index);
            }
            cursor.close();
        }
        return res;
    }

    public static File loadImage(Context context, String picturePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, options);
        int height_ = options.outHeight;
        int width_ = options.outWidth;
        Log.d(AppConstants.TAG, "loadImage: " + height_ + " " + width_);
        float ratio = (float) width_ / height_;
        Log.d(AppConstants.TAG, "loadImage: " + ratio);
        int width = 480;
        int height = 480;
        if (width_ > height_) {
            height = Math.round(width / ratio);
            Log.d(AppConstants.TAG, "loadImage: " + height);
        } else {
            width = Math.round(width * ratio);
            Log.d(AppConstants.TAG, "loadImage: " + width);
        }
        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        Bitmap pic = BitmapFactory.decodeFile(picturePath, options);
        return saveFile(context, pic);
    }

    public static File saveFile(Context context, Bitmap pic) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "JPEG_"
                + timeStamp
                + ".jpg");
        //Form a directory with a file named "pic"
        Log.d(AppConstants.TAG, "saveFile: " + dir.getAbsolutePath());
        try {
            //Prepare output stream that write byte to the directory
            FileOutputStream out = new FileOutputStream(dir);
            //Save the picture to the directory
            pic.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dir;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
                                            int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.d(AppConstants.TAG, "calculateInSampleSize: " + height + " " + width);
        int stretch_width = Math.round((float) width / (float) reqWidth);
        int stretch_height = Math.round((float) height / (float) reqHeight);
        Log.d(AppConstants.TAG, "calculateInSampleSize: " + stretch_height + " " + stretch_width);
        if (stretch_width <= stretch_height)
            return stretch_height;
        else
            return stretch_width;
    }

    public static BottomSheetDialog showDisconnectionDialog(Context context) {
        BottomSheetDialog connectionDialog = new BottomSheetDialog(context);
        connectionDialog.setCancelable(false);
        connectionDialog.setContentView(R.layout.internet_connection_layout);
        connectionDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        connectionDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        return connectionDialog;
    }
}
