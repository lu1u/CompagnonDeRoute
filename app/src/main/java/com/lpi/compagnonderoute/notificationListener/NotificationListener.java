package com.lpi.compagnonderoute.notificationListener;

import android.app.Notification;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lpi.compagnonderoute.preferences.Preferences;
import com.lpi.compagnonderoute.report.Report;

/***
 * Interception des notification emises par des applications tierces
 */
public class NotificationListener extends android.service.notification.NotificationListenerService
{
	public static final String GMAIL_PACKAGE = "com.google.android.gm";
	public static final String WHATSAPP_PACKAGE = "com.whatsapp";

	/***********************************************************************************************
	 * Interception d'une notification emise par une application tierce
	 * @param sbn
	 ***********************************************************************************************/
	@Override
	public void onNotificationPosted(@NonNull StatusBarNotification sbn)
	{
		Report r = Report.getInstance(this);
		try
		{
			final String packageName = sbn.getPackageName();
			if (packageName == null)
			{
				r.log(Report.WARNING, "onNotificationPosted: packageName null");
				return;
			}

			r.log(Report.DEBUG, "onNotificationPosted: " + packageName);

			Preferences preferences = Preferences.getInstance(this);
			if (!preferences.actif.get())
			{
				// Inactif
				r.log(Report.DEBUG, "inactif");
				return;
			}

			switch (packageName)
			{
				case GMAIL_PACKAGE:
					dumpNotification(sbn.getNotification());
					NotificationGMail.reception(this, sbn);
					break;
				case WHATSAPP_PACKAGE:
					dumpNotification(sbn.getNotification());
					NotificationWhatsApp.reception(this, sbn);
					break;

				default:
					r.log(Report.DEBUG, "application inconnue");
					NotificationAutresApplis.reception(this, sbn);
					break;
			}
		} catch (Exception e)
		{
			r.log(Report.ERROR, "erreur dans onNofiticationPosted");
			r.log(Report.ERROR, e);
		}
	}

	private void dumpNotification(@Nullable final Notification notification)
	{
		Report r = Report.getInstance(this);
		if (notification != null)
		{
			final Bundle bundle = notification.extras;
			if (bundle != null)
				for (String k : bundle.keySet())
				{
					Object o = bundle.get(k);
					if (o != null)
						r.log(Report.DEBUG, k + "(" + o.getClass().getSimpleName() + ")=" + o.toString());
				}
		}
		else
			r.log(Report.WARNING, "notification nulle");
	}

}
