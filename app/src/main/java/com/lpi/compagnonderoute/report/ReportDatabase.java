package com.lpi.compagnonderoute.report;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Base des traces (log)
 */
public class ReportDatabase
{
	private static final int NB_MAX_TRACES = 500;

	@Nullable
	protected static ReportDatabase INSTANCE = null;
	protected final SQLiteDatabase database;
	protected final ReportDatabaseHelper dbHelper;

	protected ReportDatabase(Context context)
	{
		dbHelper = new ReportDatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
	}

	@Override
	public void finalize()
	{
		try
		{
			super.finalize();
		} catch (Throwable throwable)
		{
			throwable.printStackTrace();
		}
		dbHelper.close();
	}

	/**
	 * Point d'accès pour l'instance unique du singleton
	 */
	@NonNull
	public static synchronized ReportDatabase getInstance(Context context)
	{
		if (INSTANCE == null)
		{
			INSTANCE = new ReportDatabase(context);
		}
		return (ReportDatabase) INSTANCE;
	}

	public void Vide()
	{
		database.delete(ReportDatabaseHelper.TABLE_TRACES, null, null);
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

	/***
	 * Retrouve le nombre de lignes d'une table
	 * @return nombre de lignes
	 */
	protected int getNbLignes()
	{
		Cursor cursor = database.rawQuery("SELECT COUNT (*) FROM " + ReportDatabaseHelper.TABLE_TRACES, null);
		int count = 0;
		try
		{
			if (null != cursor)
				if (cursor.getCount() > 0)
				{
					cursor.moveToFirst();
					count = cursor.getInt(0);
					cursor.close();
				}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return count;
	}

	public Cursor getCursor(int niveau)
	{
		return database.query(ReportDatabaseHelper.TABLE_TRACES, null,
				ReportDatabaseHelper.COLONNE_TRACES_NIVEAU + " >= " + niveau, null, null, null, ReportDatabaseHelper.COLONNE_TRACES_ID + " DESC");
	}

}
