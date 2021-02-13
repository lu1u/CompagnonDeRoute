package com.lpi.compagnonderoute.notificationListener;

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.lpi.compagnonderoute.R;
import com.lpi.compagnonderoute.database.NotificationDatabase;
import com.lpi.compagnonderoute.preferences.Preferences;
import com.lpi.compagnonderoute.report.Report;
import com.lpi.compagnonderoute.tts.TTSService;

public class NotificationGMail
{
	/***
	 * Notification de GMail
	 * @param sbn
	 */
	public static void reception(@NonNull final Context context, @NonNull final StatusBarNotification sbn)
	{
		Report r = Report.getInstance(context);
		Preferences preferences = Preferences.getInstance(context);
		r.log(Report.HISTORIQUE, "Notification GMail");
		if ((sbn.getNotification().flags & Notification.FLAG_GROUP_SUMMARY) != 0)
		{
			//Ignore the notification
			r.log(Report.DEBUG, "Notification groupee de GMail, ignorer");
			return;
		}
		if (!preferences.eMailsGerer.get())
		{
			// Ne pas s'occuper de WhatsApp
			r.log(Report.DEBUG, "Messages Gmail désactivés");
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

			String expediteur = null;
			String sujet = null;

			if (preferences.eMailsAnnonceExpediteur.get())
			{
				Object o = bundle.get(NotificationCompat.EXTRA_TITLE);
				if (o != null)
					expediteur = o.toString();
			}

			if (preferences.eMailsAnnonceSujet.get())
			{
				Object o = bundle.get(NotificationCompat.EXTRA_TEXT);
				if (o != null)
					sujet = o.toString();
			}

			String message;
			if (expediteur != null && sujet != null)
				message = context.getResources().getString(R.string.received_gmail, expediteur, sujet);
			else
				message = context.getResources().getString(R.string.received_gmail_null);

			TTSService.speakFromAnywhere(context, preferences.getSoundId(context), preferences.volumeDefaut.get() ? preferences.volume.get() : -1, message);
			NotificationDatabase.getInstance(context).ajoute(message);

		} catch (Exception e)
		{
			r.log(Report.ERROR, "Erreur dans NotificationListener.receptionMessageWhatsApp");
			r.log(Report.ERROR, e);
		}
	}
}
