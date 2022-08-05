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

////////////////////////////////////////////////////////////////////////////////////////////////////
//
// Reception des notifications WhatsApp
// TODO: differencier messages et appels, notificitations "systeme"
//
////////////////////////////////////////////////////////////////////////////////////////////////////

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
		r.log(Report.DEBUG, "FLAG_ONGOING_EVENT " + (sbn.getNotification().flags | Notification.FLAG_ONGOING_EVENT));
		r.log(Report.DEBUG, "FLAG_INSISTENT " + (sbn.getNotification().flags | Notification.FLAG_INSISTENT));
		r.log(Report.DEBUG, "FLAG_ONLY_ALERT_ONCE " + (sbn.getNotification().flags | Notification.FLAG_ONLY_ALERT_ONCE));
		r.log(Report.DEBUG, "FLAG_AUTO_CANCEL " + (sbn.getNotification().flags | Notification.FLAG_AUTO_CANCEL));
		r.log(Report.DEBUG, "FLAG_NO_CLEAR " + (sbn.getNotification().flags | Notification.FLAG_NO_CLEAR));
		r.log(Report.DEBUG, "FLAG_FOREGROUND_SERVICE " + (sbn.getNotification().flags | Notification.FLAG_FOREGROUND_SERVICE));
		r.log(Report.DEBUG, "FLAG_HIGH_PRIORITY " + (sbn.getNotification().flags | Notification.FLAG_HIGH_PRIORITY));
		r.log(Report.DEBUG, "FLAG_LOCAL_ONLY " + (sbn.getNotification().flags | Notification.FLAG_LOCAL_ONLY));
		r.log(Report.DEBUG, "FLAG_GROUP_SUMMARY " + (sbn.getNotification().flags | Notification.FLAG_GROUP_SUMMARY));

		// Eliminer les notifications "systemes" telles que "recherche de messages", "whatsapp web actif"...
		if ((sbn.getNotification().flags & (Notification.FLAG_GROUP_SUMMARY | Notification.FLAG_FOREGROUND_SERVICE)) != 0)
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
				TTSService.speakFromAnywhere(context, preferences.getSoundId(context), preferences.volumeDefaut.get() ? preferences.volume.get() : -1, R.string.received_whatsapp, from, message);
			else
				TTSService.speakFromAnywhere(context, preferences.getSoundId(context), preferences.volumeDefaut.get() ? preferences.volume.get() : -1, R.string.received_whatsapp_null);

		} catch (Exception e)
		{
			r.log(Report.ERROR, "Erreur dans NotificationListener.receptionMessageWhatsApp");
			r.log(Report.ERROR, e);
		}
	}
}