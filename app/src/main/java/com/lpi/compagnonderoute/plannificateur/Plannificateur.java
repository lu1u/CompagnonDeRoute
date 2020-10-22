/***
 * Classe de gestion de la plannification des alarmes
 * Singleton
 */
package com.lpi.compagnonderoute.plannificateur;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lpi.compagnonderoute.Carillon;
import com.lpi.compagnonderoute.R;
import com.lpi.compagnonderoute.preferences.Preferences;
import com.lpi.compagnonderoute.report.Report;

import java.util.Calendar;

public class Plannificateur
{
	@NonNull
	public static final String ACTION_MESSAGE_UI = Plannificateur.class.getName() + ".messageUI";
	@NonNull
	public static final String ACTION_ALARME = Plannificateur.class.getName() + ".action";

	@NonNull
	public static final String EXTRA_MESSAGE_UI = "MessageUI";
	private static final int REQUEST_CODE = 12;
	private static final long ALARM_WINDOW = 30L * 1000L;       // Fenetre de temps au cours de laquelle l'alarme doit être déclenchée à partir de l'heure donnée, en millisecondes

	@Nullable
	private static Plannificateur INSTANCE = null;

	final private AlarmManager _alarmManager;
	private PendingIntent _pendingIntent;

	/**
	 * Point d'accès pour l'instance unique du singleton
	 */
	@NonNull public static synchronized Plannificateur getInstance(@NonNull Context context)
	{
		if (INSTANCE == null)
		{
			INSTANCE = new Plannificateur(context);
		}
		return INSTANCE;
	}

	private Plannificateur(@NonNull final Context context)
	{
		_alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	}

	/***
	 * Calcule la prochaine heure pleine apres celle donnee en parametres
	 * @param maintenant
	 * @return
	 */
	public static @NonNull
	Calendar prochaineHeure(final @NonNull Calendar maintenant)
	{
		Calendar res = (Calendar) maintenant.clone();
		res.set(Calendar.SECOND, 0);
		res.set(Calendar.MINUTE, 0);
		res.add(Calendar.HOUR_OF_DAY, 1);
		return res;
	}

	/***
	 * Calcule la prochaine demi heure pleine apres celle donnee en parametres
	 * @param maintenant
	 * @return
	 */
	public static @NonNull
	Calendar prochaineDemiHeure(final @NonNull Calendar maintenant)
	{
		Calendar res = (Calendar) maintenant.clone();
		res.set(Calendar.SECOND, 0);
		final int minute = maintenant.get(Calendar.MINUTE);
		if (minute < 30)
			// Entre 0 et 30 minutes
			res.set(Calendar.MINUTE, 30);
		else
		{
			// 30 a 60 minute -> prochaine heure
			res.set(Calendar.MINUTE, 0);
			res.add(Calendar.HOUR_OF_DAY, 1);
		}
		return res;
	}

	/***
	 * Calcule le prochain quart d'heure plein apres celui donne en parametres
	 * @param maintenant
	 * @return
	 */
	public static @NonNull
	Calendar prochaineQuartDHeure(final @NonNull Calendar maintenant)
	{
		Calendar res = (Calendar) maintenant.clone();
		res.set(Calendar.SECOND, 0);

		final int minute = maintenant.get(Calendar.MINUTE);

		if (minute < 15)
			// Entre 0 et 15 minutes
			res.set(Calendar.MINUTE, 15);
		else if (minute < 30)
			// Entre 15 et 30 minutes
			res.set(Calendar.MINUTE, 30);
		else if (minute < 45)
			// Entre 30 et 45 minutes
			res.set(Calendar.MINUTE, 45);
		else
		{
			// 45 a 60 minute -> prochaine heure
			res.set(Calendar.MINUTE, 0);
			res.add(Calendar.HOUR_OF_DAY, 1);
		}

		return res;
	}

	/***
	 * Plannifie la prochaine notification pause ou carillon, celle qui arrive en premier
	 * @param context
	 */
	public void plannifieProchaineNotification(@NonNull final Context context)
	{
		try
		{
			String messageUI = "";
			Preferences preferences = Preferences.getInstance(context);
			if (!preferences.actif.get() || !preferences.annonceHeure.get())
			{
				// Arreter toute plannification
				//Notification.getInstance(context).cancel(context);
				messageUI = context.getString(R.string.deactivated);
			}
			else
			{
				// Prochain carillon
				Calendar maintenant = Calendar.getInstance();
				Calendar prochaineNotification = Carillon.getProchaineNotification(maintenant, preferences);
				if (prochaineNotification != null)
				{
					//String message = context.getString(R.string.next_alarm_notification, Carillon.toHourString(context, prochaineNotification));
					messageUI = context.getString(R.string.next_alarm_ui, Carillon.toHourString(context, prochaineNotification));

					plannifie(context, prochaineNotification);
					//Notification.getInstance(context).notify(context, message, "Démarré");
				}
			}

			// Mise a jour de l'interface utilisateur
			Intent intent = new Intent(ACTION_MESSAGE_UI);
			intent.putExtra(EXTRA_MESSAGE_UI, messageUI);
			context.sendBroadcast(intent);

		} catch (Exception e)
		{
			Report r = Report.getInstance(context);
			r.log(Report.ERROR, "Erreur dans Plannificateur.plannifieProchaineNotification");
			r.log(Report.ERROR, e);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/***
	 * Programme une alarme Android
	 * @param context
	 * @param prochaineNotification heure de la prochaine notification
	 */
	public void plannifie(final @NonNull Context context, @NonNull final Calendar prochaineNotification)
	{
		Report r = Report.getInstance(context);
		r.log(Report.DEBUG, "set alarme: " + Carillon.toHourString(context, prochaineNotification));
		try
		{
			//if (_pendingIntent != null)
			arrete(context);
			//else
			{
				Intent intent = new Intent(context, AlarmReceiver.class);
				intent.setAction(ACTION_ALARME);
				_pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			}

			//_alarmManager.setExact(AlarmManager.RTC_WAKEUP, prochaineNotification.getTimeInMillis(), _pendingIntent);
			_alarmManager.setWindow(AlarmManager.RTC_WAKEUP, prochaineNotification.getTimeInMillis(), ALARM_WINDOW, _pendingIntent);
		} catch (Exception e)
		{
			r.log(Report.ERROR, "Plannificateur.plannifie");
			r.log(Report.ERROR, e);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/***
	 * Arrete la plannificaton
	 * @param context
	 */
	public void arrete(@NonNull final Context context)
	{
		try
		{
			if (_pendingIntent != null)
				_alarmManager.cancel(_pendingIntent);

			//Notification.getInstance(context).cancel(context);
		} catch (Exception e)
		{
			Report r = Report.getInstance(context);
			r.log(Report.ERROR, "Erreur dans Plannificateur.arrete");
			r.log(Report.ERROR, e);
		}
	}
}
