package com.lpi.compagnonderoute.notificationListener;

import android.app.Notification;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.lpi.compagnonderoute.R;
import com.lpi.compagnonderoute.preferences.Preferences;
import com.lpi.compagnonderoute.report.Report;
import com.lpi.compagnonderoute.tts.TTSService;

class NotificationAutresApplis
{
	public static void reception(@NonNull final Context context, @NonNull final StatusBarNotification sbn)
	{
		Report r = Report.getInstance(context);
		Preferences preferences = Preferences.getInstance(context);
		r.log(Report.HISTORIQUE, "Autre notification");
		if ((sbn.getNotification().flags & Notification.FLAG_GROUP_SUMMARY) != 0)
		{
			//Ignore the notification
			r.log(Report.DEBUG, "Notification groupee d'une application, ignorer");
			return;
		}

		if (!preferences.autresApplisActif.get())
		{
			// Ne pas s'occuper des autres applications
			r.log(Report.DEBUG, "Messages d'une application désactivés");
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

			final String packageName = sbn.getPackageName();
			boolean titre = preferences.getNotificationTitre(packageName);
			boolean contenu = preferences.getNotificationContenu(packageName);
			if (!titre && !contenu)
			{
				r.log(Report.DEBUG, "Titre et contenu désactivé");
				return;
			}

			String sTitre = null;
			String sSujet = null;
			final String applicationName = getApplicationName(context, packageName);
			if (titre)
			{
				Object o = bundle.get(NotificationCompat.EXTRA_TITLE);
				if (o != null)
					sTitre = o.toString();
			}

			if (contenu)
			{
				Object o = bundle.get(NotificationCompat.EXTRA_TEXT);
				if (o != null)
					sSujet = o.toString();
			}

			if (sTitre != null && sSujet != null)
				TTSService.speakFromAnywhere(context, preferences.getSoundId(context), preferences.volumeDefaut.get() ? preferences.volume.get() : -1, R.string.received_autre_appli, applicationName, sTitre, sSujet);
			else
				TTSService.speakFromAnywhere(context, preferences.getSoundId(context), preferences.volumeDefaut.get() ? preferences.volume.get() : -1, R.string.received_autre_appli_null);

		} catch (Exception e)
		{
			r.log(Report.ERROR, "Erreur dans NotificationListener.receptionMessageWhatsApp");
			r.log(Report.ERROR, e);
		}
	}

	private static String getApplicationName(@NonNull final Context context, @NonNull final String packageName)
	{
		final PackageManager pm = context.getPackageManager();
		ApplicationInfo ai;
		try
		{
			ai = pm.getApplicationInfo(packageName, 0);
		} catch (final PackageManager.NameNotFoundException e)
		{
			ai = null;
		}
		return (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
	}
}
