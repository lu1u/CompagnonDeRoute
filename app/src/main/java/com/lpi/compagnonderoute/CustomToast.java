package com.lpi.compagnonderoute;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

class CustomToast
{
	public static void show(@NonNull final Activity activity, int idString)
	{
		show(activity, activity.getString(idString));
	}

	public static void show(@NonNull final Activity activity, @NonNull final String message)
	{
		LayoutInflater inflater = activity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.custom_toast, null);
		TextView tv = (TextView) layout.findViewById(R.id.txtvw);
		tv.setText(message);
		Toast toast = new Toast(activity.getApplicationContext());
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		toast.show();
	}
}
