/***
 * Recepteur des alarmes Carillon
 */
package com.lpi.compagnonderoute.plannificateur;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;

import androidx.annotation.NonNull;

import com.lpi.compagnonderoute.R;
import com.lpi.compagnonderoute.preferences.Preferences;
import com.lpi.compagnonderoute.report.Report;
import com.lpi.compagnonderoute.tts.TTSService;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver
{
	/***
	 * Reception d'une alarme TYPE_NOTIFICATION_CARILLON
	 */
	@Override
	public void onReceive(@NonNull Context context, @NonNull Intent intent)
	{
		Report r = Report.getInstance(context);
		r.log(Report.DEBUG, "Alarme recue, action=" + intent.getAction());

		try
		{
			String action = intent.getAction();

			if (Plannificateur.ACTION_ALARME.equals(action))
			{
				Preferences preferences = Preferences.getInstance(context);
				if (preferences.actif.get())
					if (preferences.horlogeAnnoncer.get())
					{
						Calendar maintenant = Calendar.getInstance();
						TTSService.speakFromAnywhere(context, preferences.getSoundId(context), preferences.volumeDefaut.get() ? preferences.volume.get() : -1, R.string.time_announce, DateUtils.formatDateTime(context, maintenant.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));
						Plannificateur.getInstance(context).plannifieProchaineNotification(context);
					}
			}
			else
				r.log(Report.WARNING, "action inconnue dans AlarmReceiver.onReceive " + action);
		}
		catch (Exception e)
		{
			r.log(Report.ERROR, "Erreur dans AlarmReceiver.onReceive");
			r.log(Report.ERROR, e.toString());
		}
	}
}
