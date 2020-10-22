package com.lpi.compagnonderoute.notificationListener;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.lpi.compagnonderoute.ConfirmBox;

/***
 * Gestion de la configuration qui autorise une application à écouter les notifications système
 * C'est l'utilisateur qui doit autoriser manuellement une application
 */

public class NotificationListenerManager
{
	/***
	 * Verifie que l'application est bien autorisée à intercepter les notifications,
	 * si ce n'est pas le cas, on propose à l'utilisateur d'ouvrir la fenetre de configuration
	 * pour qu'il puisse accorder la permission
	 *
	 * Appels du listener:
	 * - onEnabled: les droits sont deja accordes
	 * - onSettings: droits pas encore accordes, l'utilisateur est redirigé vers l'écran de configuration du systeme
	 * - onCancel: les droits ne sont pas accordes, l'utilisateur ne veut pas changer sa configuration
	 */
	public static void checkNotificationServiceEnabled(@NonNull final Context context, @StringRes int idMessage, @NonNull final checkNotificationServiceEnabledListener listener)
	{
		if (isNotificationServiceEnabled(context))
			listener.onEnabled();
		else
		{
			ConfirmBox.show(context, context.getString(idMessage),
					new ConfirmBox.ConfirmBoxListener()
					{
						@Override public void onPositive()
						{
							// Afficher l'ecran de configuration pour intercepter les notifications
							context.startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
							listener.onSettings();
						}

						@Override public void onNegative()
						{
							listener.onCancel();
						}
					});
		}
	}

	/***
	 * Verifie que notre application est autorisee à intercepter les notifications
	 * @return
	 */
	public static boolean isNotificationServiceEnabled(@NonNull final Context context)
	{
		String packageName = context.getPackageName();
		final String flat = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
		if (!TextUtils.isEmpty(flat))
		{
			final String[] names = flat.split(":");
			for (String name : names)
			{
				final ComponentName componentName = ComponentName.unflattenFromString(name);
				if (componentName != null)
					if (TextUtils.equals(packageName, componentName.getPackageName()))
						return true;
			}
		}
		return false;
	}

	public static void displayNotificationSettings(@NonNull final Context context)
	{
		context.startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
	}

	public interface checkNotificationServiceEnabledListener
	{
		void onEnabled();
		void onCancel();
		void onSettings();
	}
}
