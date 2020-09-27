/**
 * Enregistre les traces du programme dans une base de donnees, consultable avec ReportActivity
 * Deux modes:
 *      - LOG
 *      - HISTORIQUE
 * Les traces ne sont enregistrees qu'en mode DEBUG
 */
package com.lpi.compagnonderoute.report;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;



/**
 * @author lucien
 */
@SuppressWarnings("nls")
public class Report
{
	@NonNull
	public static final String PREFERENCES = "lpi.com.reportlibrary.preferences";
	@NonNull
	public static final String PREF_TRACES = "lpi.com.reportlibrary.preferences.traces";

	@NonNull
	final private static String TAG = "Report";
	private static boolean GENERER_TRACES = true;

	// Niveaux de trace
	public static final int DEBUG=0;
	public static final int WARNING=1;
	public static final int ERROR=2;


	private static final int MAX_BACKTRACE = 10;
	@Nullable
	private static Report INSTANCE = null;
	TracesDatabase _tracesDatabase;

	private Report(Context context)
	{
		SharedPreferences settings = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		GENERER_TRACES = settings.getBoolean(PREF_TRACES, GENERER_TRACES);

		if (GENERER_TRACES) _tracesDatabase = TracesDatabase.getInstance(context);
	}

	public static boolean isGenererTraces()
	{
		return GENERER_TRACES;
	}

	public static void setGenererTraces(Context context, boolean valeur)
	{
		if (GENERER_TRACES != valeur)
		{
			GENERER_TRACES = valeur;
			SharedPreferences settings = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(PREF_TRACES, GENERER_TRACES);
			editor.apply();
		}
	}


	/**
	 * Point d'accès pour l'instance unique du singleton
	 *
	 * @param context: le context habituel d'ANdroid, peut être null si l'objet a deja ete utilise
	 */
	@NonNull
	public static synchronized Report getInstance(@NonNull Context context)
	{
		if (INSTANCE == null)
		{
			INSTANCE = new Report(context);
		}
		return INSTANCE;
	}





	public void log(@NonNull int niv, @NonNull String message)
	{
		if (GENERER_TRACES)
		{
			Log.d(TAG, message);
			_tracesDatabase.Ajoute(DatabaseHelper.CalendarToSQLiteDate(null), niv, message);
		}
	}

	public void log(@NonNull int niv, @NonNull Exception e)
	{
		if (GENERER_TRACES)
		{
			log(niv, e.getLocalizedMessage());
			for (int i = 0; i < e.getStackTrace().length && i < MAX_BACKTRACE; i++)
				log(niv, e.getStackTrace()[i].getClassName() + '/' + e.getStackTrace()[i].getMethodName() + ':' + e.getStackTrace()[i].getLineNumber());
		}
	}


}
