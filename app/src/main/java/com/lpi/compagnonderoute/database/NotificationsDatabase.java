package com.lpi.compagnonderoute.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Base des traces (log)
 */
public class NotificationsDatabase
{
	private static final int NB_MAX_TRACES = 500;

	@Nullable
	protected static NotificationsDatabase INSTANCE = null;
	protected final SQLiteDatabase database;
	protected final DatabaseHelper dbHelper;

	protected NotificationsDatabase(Context context)
	{
		dbHelper = new DatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
	}

	/**
	 * Point d'accès pour l'instance unique du singleton
	 */
	@NonNull
	public static synchronized NotificationsDatabase getInstance(Context context)
	{
		if (INSTANCE == null)
		{
			INSTANCE = new NotificationsDatabase(context);
		}
		return (NotificationsDatabase) INSTANCE;
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

	public void Vide()
	{
		database.delete(DatabaseHelper.TABLE_NOTIFICATIONS, null, null);
	}

	public void Ajoute(int Date, final @NonNull String ligne)
	{
		try
		{
			if (getNbLignes() > NB_MAX_TRACES)
			{
				// Supprimer les 50 premieres pour eviter que la table des traces ne grandisse trop
				database.execSQL("DELETE FROM " + DatabaseHelper.TABLE_NOTIFICATIONS + " WHERE " + DatabaseHelper.COLONNE_NOTIFICATION_ID
						+ " IN (SELECT " + DatabaseHelper.COLONNE_NOTIFICATION_ID + " FROM " + DatabaseHelper.TABLE_NOTIFICATIONS + " ORDER BY " + DatabaseHelper.COLONNE_NOTIFICATION_ID + " LIMIT 50)");
			}

			ContentValues initialValues = new ContentValues();
			initialValues.put(DatabaseHelper.COLONNE_NOTIFICATION_DATE, Date);
			initialValues.put(DatabaseHelper.COLONNE_NOTIFICATION_LIGNE, ligne);

			database.insert(DatabaseHelper.TABLE_NOTIFICATIONS, null, initialValues);
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
		Cursor cursor = database.rawQuery("SELECT COUNT (*) FROM " + DatabaseHelper.TABLE_NOTIFICATIONS, null);
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

	public Cursor getCursor()
	{
		return database.query(DatabaseHelper.TABLE_NOTIFICATIONS, null,
				null, null, null, null, DatabaseHelper.COLONNE_NOTIFICATION_ID + " DESC");
	}

}
