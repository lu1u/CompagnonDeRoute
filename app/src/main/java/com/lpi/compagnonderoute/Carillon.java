package com.lpi.compagnonderoute;
/***
 * Annonce les heures, les demis, les quarts
 */

import android.content.Context;
import android.text.format.DateUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lpi.compagnonderoute.plannificateur.Plannificateur;
import com.lpi.compagnonderoute.preferences.Preferences;
import com.lpi.compagnonderoute.report.Report;

import java.util.Calendar;

public class Carillon
{
	/*******************************************************************************************************************
	 * Calcule l'heure de la prochaine notification d'annonce de l'heure
	 * @param maintenant
	 * @param preferences
	 * @return prochain carillon, ou null
	 *******************************************************************************************************************/
	public static @Nullable
	Calendar getProchaineNotification(@NonNull final Calendar maintenant, @NonNull final Preferences preferences)
	{
		if (!preferences.horlogeAnnoncer.get())
			return null;

		switch (preferences.horlogeDelai.get())
		{
			case Preferences.DELAI_ANNONCE_HEURE_HEURES:
				return Plannificateur.prochaineHeure(maintenant);

			case Preferences.DELAI_ANNONCE_HEURE_DEMI:
				return Plannificateur.prochaineDemiHeure(maintenant);

			case Preferences.DELAI_ANNONCE_HEURE_QUART:
				return Plannificateur.prochaineQuartDHeure(maintenant);

			default: //??? On ne devrait jamais passer par la
				Report.getInstance(null).log(Report.ERROR, "Delai Annonce Heure incorrect dans Carillon.getProchaineNotification " + preferences.horlogeDelai.get());
				return null;
		}
	}

	/*******************************************************************************************************************
	 * Calcule une representation textuelle de l'heure actuelle
	 *******************************************************************************************************************/
	public static String toHourString(@NonNull Context context, @NonNull Calendar c)
	{
		return DateUtils.formatDateTime(context, c.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME);
	}

	/***
	 * Change le delai entre les carillons
	 * @param context
	 */
	public static void changeDelai(@NonNull final Context context)
	{
		Preferences prefs = Preferences.getInstance(context);
		if (!prefs.actif.get())
			// Pas actif
			return;

		Plannificateur.getInstance(context).plannifieProchaineNotification(context);
	}
}
