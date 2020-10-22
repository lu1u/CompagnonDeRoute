package com.lpi.compagnonderoute.notificationListener;

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.lpi.compagnonderoute.R;
import com.lpi.compagnonderoute.preferences.Preferences;
import com.lpi.compagnonderoute.report.Report;
import com.lpi.compagnonderoute.tts.TTSService;

class NotificationWhatsApp
{
	/***
	 * Annoncer un message WhatsApp
	 * @param sbn
	 */
	public static void reception(@NonNull final Context context, @Nullable final StatusBarNotification sbn)
	{
		Report r = Report.getInstance(context);
		r.log(Report.HISTORIQUE, "Notification WhatsApp");
		if ((sbn.getNotification().flags & Notification.FLAG_GROUP_SUMMARY) != 0)
		{
			//Ignore the notification
			r.log(Report.DEBUG, "Notification groupee de WhatsApp, ignorer");
			return;
		}

		Preferences preferences = Preferences.getInstance(context);

		if (!preferences.messageWhatsAppActif.get())
		{
			// Ne pas s'occuper de WhatsApp
			r.log(Report.DEBUG, "Messages whatsapp désactivés");
			return;
		}

		try
		{
			final Notification notification = sbn.getNotification();
			if (notification == null)
			{
				r.log(Report.WARNING, "Pas de notification");
				return;
			}

			final Bundle bundle = notification.extras;
			if (bundle == null)
			{
				r.log(Report.WARNING, "Pas de bundle");
				return;
			}

			final String from = bundle.getString(NotificationCompat.EXTRA_TITLE);
			r.log(Report.DEBUG, "From: " + from);
			final String message = bundle.getString(NotificationCompat.EXTRA_TEXT);
			r.log(Report.DEBUG, ": " + message);

			if (from != null && message != null)
				TTSService.speakFromAnywhere(context, R.raw.beep, preferences.volumeDefaut.get() ? preferences.volume.get() : -1, R.string.received_whatsapp, from, message);
			else
				TTSService.speakFromAnywhere(context, R.raw.beep, preferences.volumeDefaut.get() ? preferences.volume.get() : -1, R.string.received_whatsapp_null);

		} catch (Exception e)
		{
			r.log(Report.ERROR, "Erreur dans NotificationListener.receptionMessageWhatsApp");
			r.log(Report.ERROR, e);
		}
	}
}
