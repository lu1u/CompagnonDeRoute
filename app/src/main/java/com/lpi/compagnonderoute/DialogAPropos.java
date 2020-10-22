package com.lpi.compagnonderoute;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class DialogAPropos
{
	public static void start(Activity activity)
	{
		final AlertDialog dialogBuilder = new AlertDialog.Builder(activity).create();
		LayoutInflater inflater = activity.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.activity_apropos, null);
		String message = "Application Id:" + BuildConfig.APPLICATION_ID
				+ "\nBuild type:" + BuildConfig.BUILD_TYPE
				+ "\nVersion name:" + BuildConfig.VERSION_NAME
				+ "\nVersion code:" + BuildConfig.VERSION_CODE
				+ "\nDebug:" + BuildConfig.DEBUG;

		TextView tv = dialogView.findViewById(R.id.textViewDescription);
		tv.setText(message);
		dialogBuilder.setView(dialogView);
		dialogBuilder.show();
	}
}
