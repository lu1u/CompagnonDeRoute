/**
 * Enregistre les traces du programme dans une base de donnees, consultable avec ReportActivity
 * Les traces ne sont enregistrees que si le fichier build.gradle contient la definition suivante:
 *   buildConfigField "boolean", "REPORT", "true"
 *
 *   exemple:
 *       defaultConfig {
 *         applicationId "com.lpi.compagnonderoute"
 *         minSdkVersion 27
 *         targetSdkVersion 28
 *         versionCode 1
 *         versionName "1.0"
 *         buildConfigField "boolean", "REPORT", "true"
 *         testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
 *     }
 */
package com.lpi.compagnonderoute.report;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lpi.compagnonderoute.BuildConfig;

/**
 * @author lucien
 */
@SuppressWarnings("nls")
public class Report
{
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

		if (BuildConfig.REPORT)
			_tracesDatabase = TracesDatabase.getInstance(context);
		else
			_tracesDatabase = null;
	}

	public static boolean isGenererTraces()
	{
		return BuildConfig.REPORT;
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

	public void log(@NonNull int niv, @NonNull Exception e)
	{
		if (_tracesDatabase != null)
		{
			log(niv, e.getLocalizedMessage());
			for (int i = 0; i < e.getStackTrace().length && i < MAX_BACKTRACE; i++)
				log(niv, e.getStackTrace()[i].getClassName() + '/' + e.getStackTrace()[i].getMethodName() + ':' + e.getStackTrace()[i].getLineNumber());
		}
	}

	public void log(@NonNull int niv, @NonNull String message)
	{
		if (_tracesDatabase != null)
		{
			Log.d(TAG, message);
			_tracesDatabase.Ajoute(DatabaseHelper.CalendarToSQLiteDate(null), niv, message);
		}
	}
}
