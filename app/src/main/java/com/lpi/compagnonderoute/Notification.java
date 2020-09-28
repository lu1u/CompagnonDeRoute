package com.lpi.compagnonderoute;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

public class Notification
{
	public static final int NOTIFICATION_ID = 12455;
	/**
	 * The unique identifier for this type of notification.
	 */
	@NonNull
	private static final String CHANNEL_ID = "Compagnon01";
	@NonNull
	private static final String NOTIFICATION_TAG = "Compagnon";
	NotificationManager nm;
	private static Notification _instance;
	private String _texteTitre;
	private String _texteResume;

	/***
	 * Obtenir l'instance (unique) de Preferences
	 * @param context
	 * @return
	 */
	public static synchronized Notification getInstance(@NonNull final Context context)
	{
		if ( _instance == null)
			_instance = new Notification(context);

		return _instance;
	}

	/***
	 * Constructeur privé du singleton Preferences, on doit passer par getInstance pour obtenir une
	 * instance
	 * @param context
	 */
	private Notification(@NonNull final Context context)
	{
		nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	public @NonNull	android.app.Notification getNotification(@NonNull final Context context)
	{
		return getNotification(context, _texteTitre, _texteResume);
	}

	/**
	 * Shows the notification, or updates a previously shown notification of
	 * this type, with the given parameters.
	 *
	 * @see #cancel(Context)
	 */
	public void notify(@NonNull final Context context, @NonNull final String texteTitre, @NonNull final String texteResume)
	{
		_texteTitre = texteTitre;
		_texteResume = texteResume;
		if (nm != null)
			nm.notify(NOTIFICATION_TAG, NOTIFICATION_ID, getNotification(context, texteTitre, texteResume));
	}


	/**
	 * Cancels any notifications of this type previously shown using
	 */
	public void cancel(final Context context)
	{
		final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		if (nm != null)
		{
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR)
			{
				nm.cancel(NOTIFICATION_TAG, NOTIFICATION_ID);
			} else
			{
				nm.cancel(NOTIFICATION_TAG.hashCode());
			}
		}
	}

	public static @NonNull
	android.app.Notification getNotification(@NonNull Context context, @NonNull final String texte, @NonNull final String texteResume)
	{
		Intent ii = new Intent(context.getApplicationContext(), MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, ii, 0);

		NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
		bigText.bigText(texte);
		bigText.setBigContentTitle(context.getString(R.string.app_name));
		bigText.setSummaryText(texteResume);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), "CHANNEL_ID");
		builder.setDefaults(android.app.Notification.DEFAULT_ALL);
		builder.setContentIntent(pendingIntent);
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setContentTitle("Compagnon");
		builder.setContentText("content text");
		builder.setStyle(bigText);
		builder.setSound(null);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
		{
			final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Compagnon", NotificationManager.IMPORTANCE_DEFAULT);
			notificationChannel.setSound(null, null);
			notificationChannel.setShowBadge(false);
			nm.createNotificationChannel(notificationChannel);

			builder.setChannelId(CHANNEL_ID);
		}
		return builder.build();
	}
}
