package com.weimed.lib;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Richard Lee on 4/1/14.
 */
public class CustomAlertDialog {

    public static void show(Context context, String title, String message) {
        AlertDialog alert = new AlertDialog.Builder(context).create();
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setIcon(R.drawable.ic_launcher);
        alert.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.show();
    }
}
