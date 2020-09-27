package com.lpi.compagnonderoute.report;

/*
  Utilitaire de gestion de la base de donnees
 */

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;

public class DatabaseHelper extends SQLiteOpenHelper
{
	public static final int DATABASE_VERSION = 5;
	public static final String DATABASE_NAME = "database.db";

	////////////////////////////////////////////////////////////////////////////////////////////////////
// Table historique
	public static final String TABLE_HISTORIQUE = "HISTORIQUE";
	public static final String COLONNE_HISTORIQUE_DATE = "DATE";
	public static final String COLONNE_HISTORIQUE_LIGNE = "LIGNE";
	public static final String COLONNE_HISTORIQUE_ID = "_id";
	public static final String TABLE_TRACES = "TRACES";

	////////////////////////////////////////////////////////////////////////////////////////////////////
// Table traces
	public static final String COLONNE_TRACES_ID = "_id";
	public static final String COLONNE_TRACES_DATE = "DATE";
	public static final String COLONNE_TRACES_NIVEAU = "NIVEAU";
	public static final String COLONNE_TRACES_LIGNE = "LIGNE";

	private static final String DATABASE_HISTORIQUE_CREATE = "create table "
			+ TABLE_HISTORIQUE + "("
			+ COLONNE_HISTORIQUE_ID + " integer primary key autoincrement, "
			+ COLONNE_HISTORIQUE_DATE + " integer,"
			+ COLONNE_HISTORIQUE_LIGNE + " text not null"
			+ ");";
	private static final String DATABASE_TRACES_CREATE = "create table "
			+ TABLE_TRACES + "("
			+ COLONNE_TRACES_ID + " integer primary key autoincrement, "
			+ COLONNE_TRACES_DATE + " integer,"
			+ COLONNE_TRACES_NIVEAU + " integer,"
			+ COLONNE_TRACES_LIGNE + " text not null"
			+ ");";


	public DatabaseHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	static public int CalendarToSQLiteDate(@Nullable Calendar cal)
	{
		if (cal == null)
			cal = Calendar.getInstance();
		return (int) (cal.getTimeInMillis() / 1000L);
	}

	@NonNull
	static public Calendar SQLiteDateToCalendar(int date)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis((long) date * 1000L);
		return cal;
	}

	//@NonNull
	//public static String getStringFromAnyColumn(@NonNull Cursor cursor, int colonne)
	//{
	//	Object o = getObjectFromAnyColumn(cursor, colonne);
	//	if (o != null)
	//		return o.toString();
	//	else
	//		return "Impossible de lire la colonne " + cursor.getColumnName(colonne);
	//}

	@Nullable
	public static Object getObjectFromAnyColumn(@NonNull Cursor cursor, int colonne)
	{
		try
		{
			return cursor.getInt(colonne);
		} catch (Exception e)
		{
			try
			{
				return cursor.getShort(colonne);
			} catch (Exception e1)
			{
				try
				{
					return cursor.getLong(colonne);
				} catch (Exception e2)
				{
					try
					{
						return cursor.getDouble(colonne);
					} catch (Exception e3)
					{
						try
						{
							return cursor.getFloat(colonne);
						} catch (Exception e4)
						{
							try
							{
								return cursor.getString(colonne);
							} catch (Exception e5)
							{
								Log.e("Dabase", "impossible de lire la colonne " + colonne);
							}
						}
					}
				}
			}
		}

		return null;
	}

	@Override
	public void onCreate(SQLiteDatabase database)
	{
		try
		{
			database.execSQL(DATABASE_HISTORIQUE_CREATE);
			database.execSQL(DATABASE_TRACES_CREATE);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		try
		{
			Log.w(DatabaseHelper.class.getName(),
					"Upgrading database from version " + oldVersion + " to "
							+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORIQUE);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACES);
			onCreate(db);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}


}
