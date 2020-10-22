package com.lpi.compagnonderoute.report;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import androidx.annotation.NonNull;

/**
 * Base des traces (log)
 */
public class TracesDatabase extends ReportDatabase
{
	private static final int NB_MAX_TRACES = 500;

	private TracesDatabase(Context context)
	{
		super(ReportDatabaseHelper.TABLE_TRACES, context);
	}


	/**
	 * Point d'accès pour l'instance unique du singleton
	 */
	@NonNull
	public static synchronized TracesDatabase getInstance(Context context)
	{
		if (INSTANCE == null)
		{
			INSTANCE = new TracesDatabase(context);
		}
		return (TracesDatabase) INSTANCE;
	}

	public void Ajoute(int Date, int niveau, String ligne)
	{
		try
		{
			if (getNbLignes() > NB_MAX_TRACES)
			{
				// Supprimer les 50 premieres pour eviter que la table des traces ne grandisse trop
				database.execSQL("DELETE FROM " + ReportDatabaseHelper.TABLE_TRACES + " WHERE " + ReportDatabaseHelper.COLONNE_TRACES_ID
						+ " IN (SELECT " + ReportDatabaseHelper.COLONNE_TRACES_ID + " FROM " + ReportDatabaseHelper.TABLE_TRACES + " ORDER BY " + ReportDatabaseHelper.COLONNE_TRACES_ID + " LIMIT 50)");
			}

			ContentValues initialValues = new ContentValues();
			initialValues.put(ReportDatabaseHelper.COLONNE_TRACES_DATE, Date);
			initialValues.put(ReportDatabaseHelper.COLONNE_TRACES_NIVEAU, niveau);
			initialValues.put(ReportDatabaseHelper.COLONNE_TRACES_LIGNE, ligne);

			database.insert(ReportDatabaseHelper.TABLE_TRACES, null, initialValues);
		} catch (Exception e)
		{
			// Surtout ne pas faire une TRACE, on vient d'échouer a en faire une!
			e.printStackTrace();
		}
	}


	public Cursor getCursor(int niveau)
	{
		return database.query(ReportDatabaseHelper.TABLE_TRACES, null,
				ReportDatabaseHelper.COLONNE_TRACES_NIVEAU + " >= " + niveau, null, null, null, ReportDatabaseHelper.COLONNE_TRACES_ID + " DESC");
	}


}
