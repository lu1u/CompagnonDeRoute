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
import com.lpi.compagnonderoute.database.NotificationDatabase;
import com.lpi.compagnonderoute.preferences.Preferences;
import com.lpi.compagnonderoute.report.Report;
import com.lpi.compagnonderoute.tts.TTSService;

class NotificationAutresApplis
{
	public static void reception(@NonNull final Context context, @NonNull final StatusBarNotification sbn)
	{
		Report r = Report.getInstance(context);
		try
		{
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
				r.log(Report.DEBUG, "Messages d'une autre application désactivés");
				return;
			}

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
			if( packageName!=null)
			{
				final String notreApplication = context.getPackageName();
				if ( notreApplication.equals(packageName))
					return;

				final boolean nomAppli = preferences.getNotificationNomAppli(packageName);
				final boolean titre = preferences.getNotificationTitre(packageName);
				final boolean contenu = preferences.getNotificationContenu(packageName);
				if (!nomAppli && !titre && !contenu)
				{
					r.log(Report.DEBUG, "NomAppli, Titre et contenu désactivé");
					return;
				}

				StringBuilder sb = new StringBuilder();

				// Dire le nom de l'application
				if (nomAppli)
				{
					final String nom = getApplicationName(context, packageName);
					r.log(Report.DEBUG, "Nom:" + nom);
					sb.append(context.getString(R.string.received_autre_appli_nom, nom));
				}

				// Dire le titre de la notification
				if (titre)
				{
					Object o = bundle.get(NotificationCompat.EXTRA_TITLE);
					if (o != null)
						sb.append(o.toString() + ". ");
				}

				// Dire le contenu
				if (contenu)
				{
					Object o = bundle.get(NotificationCompat.EXTRA_TEXT);
					if (o != null)
						sb.append(o.toString() + ". ");
				}

				TTSService.speakFromAnywhere(context, preferences.getSoundId(context), preferences.volumeDefaut.get() ? preferences.volume.get() : -1, sb.toString());
				NotificationDatabase.getInstance(context).ajoute(sb.toString());
			}
		} catch (Exception e)
		{
			r.log(Report.ERROR, "Erreur dans NotificationListener.receptionMessageAutresApplis");
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
			return "(unknown)";
		}
		return pm.getApplicationLabel(ai).toString() ;
	}
}
