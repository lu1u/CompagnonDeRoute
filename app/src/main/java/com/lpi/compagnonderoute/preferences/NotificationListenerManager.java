package com.lpi.compagnonderoute.preferences;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.lpi.compagnonderoute.ConfirmBox;

/***
 * Gestion de la configuration qui autorise une application à écouter les notifications système
 * C'est l'utilisateur qui doit autoriser manuellement une application
 */

class NotificationListenerManager
{
	/***
	 * Verifie que l'application est bien autorisée à intercepter les notifications,
	 * si ce n'est pas le cas, on propose à l'utilisateur d'ouvrir la fenetre de configuration
	 * pour qu'il puisse accorder la permission
	 */
	public static void checkNotificationServiceEnabled(@NonNull final Context context, @NonNull final checkNotificationServiceEnabledListener listener)
	{
		if (isNotificationServiceEnabled(context))
			listener.onEnabled();
		else
		{
			ConfirmBox.show(context, "Pour annoncer les messages Whatsapp, vous devez donner votre autorisation. L'écran de confirmation du système va être affiché, veuillez cocher l'option correspondant à Compagnon de route. Pressez 'Annuler' si vous ne voulez pas accorder l'aurorisation à l'application.",
					new ConfirmBox.ConfirmBoxListener()
					{
						@Override public void onPositive()
						{
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
		String pkgName = context.getPackageName();
		final String flat = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
		if (!TextUtils.isEmpty(flat))
		{
			final String[] names = flat.split(":");
			for (String name : names)
			{
				final ComponentName cn = ComponentName.unflattenFromString(name);
				if (cn != null)
					if (TextUtils.equals(pkgName, cn.getPackageName()))
						return true;
			}
		}
		return false;
	}

	public interface checkNotificationServiceEnabledListener
	{
		public void onEnabled();
		public void onCancel();
		public void onSettings();
	}
}
