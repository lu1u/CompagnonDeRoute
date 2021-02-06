package com.lpi.compagnonderoute.preferences;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.lpi.compagnonderoute.plannificateur.Plannificateur;
import com.lpi.compagnonderoute.report.Report;

/***
 * Evite de remettre l'application en activite si l'option"Reactiver après redémarrage" n'est pas cochée
 */
public class RebootReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(@NonNull final Context context, @NonNull  final Intent intent)
	{
		Report r = Report.getInstance(context);
		r.log(Report.DEBUG, "RebootReceiver.onReceive " + intent.getAction());

		if ( Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
		{
			Preferences preferences = Preferences.getInstance(context);
			r.log(Report.DEBUG, "Actif apres reboot: " + preferences.actifApresReboot.get());

			if (!preferences.actifApresReboot.get())
			{
				r.log(Report.DEBUG, "Non actif apres reboot");
				preferences.actif.set(false);
			}
			else if (preferences.actif.get())
				Plannificateur.getInstance(context).plannifieProchaineNotification(context);
		}
		else
			r.log(Report.WARNING, "Action inconnue dans RebootReceiver.onReceive " + intent.getAction());
	}
}
