package com.bendod.androlbg.activity;

import com.bendod.androlbg.R;
import com.bendod.androlbg.Settings;
import com.bendod.androlbg.utils.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.Toast;

// import gnu.android.app.appmanualclient.AppManualReaderClient;

public final class ActivityMixin {

    public static void setTheme(final Activity activity) {
        if (Settings.isLightSkin()) {
            activity.setTheme(R.style.Theme_Sherlock_Light);
        } else {
            activity.setTheme(R.style.Theme_Sherlock);
        }
    }

    public static void showToast(final Activity activity, final String text) {
        if (!Utils.isBlank(text)) {
            Toast toast = Toast.makeText(activity, text, Toast.LENGTH_LONG);

            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 100);
            toast.show();
        }
    }

    public static void showShortToast(final Activity activity, final String text) {
        if (!Utils.isBlank(text)) {
            Toast toast = Toast.makeText(activity, text, Toast.LENGTH_SHORT);

            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 100);
            toast.show();
        }
    }

    public static void helpDialog(final Activity activity, final String title, final String message, final Drawable icon) {
        if (Utils.isBlank(message)) {
            return;
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(activity).setTitle(title).setMessage(message).setCancelable(true);
        dialog.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        if (icon != null) {
            dialog.setIcon(icon);
        }

        AlertDialog alert = dialog.create();
        alert.show();
    }

    public static void helpDialog(Activity activity, String title, String message) {
        helpDialog(activity, title, message, null);
    }

}
