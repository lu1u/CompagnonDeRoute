package com.lpi.compagnonderoute.database;

/*
  Utilitaire de gestion de la base de donnees
 */

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;

public class DatabaseHelper extends SQLiteOpenHelper
{
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "notifications.db";

	public static final String TABLE_NOTIFICATIONS = "NOTIFICATIONS";

	////////////////////////////////////////////////////////////////////////////////////////////////////
	// Table traces
	public static final String COLONNE_NOTIFICATION_ID = "_id";
	public static final String COLONNE_NOTIFICATION_DATE = "DATE";
	public static final String COLONNE_NOTIFICATION_LIGNE = "LIGNE";
	////////////////////////////////////////////////////////////////////////////////////////////////////
	// Preferences
	public static final String TABLE_PREFERENCES = "PREFERENCES";
	public static final String COLONNE_NOM = "NOM";
	public static final String COLONNE_VALEUR = "VALEUR";
	private static final String DATABASE_NOTIFICATIONS_CREATE = "create table "
			+ TABLE_NOTIFICATIONS + "("
			+ COLONNE_NOTIFICATION_ID + " integer primary key autoincrement, "
			+ COLONNE_NOTIFICATION_DATE + " integer,"
			+ COLONNE_NOTIFICATION_LIGNE + " text not null"
			+ ");";
	private String DATABASE_PREFERENCES_CREATE = "create table IF NOT EXISTS "
			+ TABLE_PREFERENCES + "("
			+ COLONNE_NOM + " TEXT NOT NULL, "
			+ COLONNE_VALEUR + " text"
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

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		try
		{
			Log.w(DatabaseHelper.class.getName(),
					"Upgrading database from version " + oldVersion + " to "
							+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREFERENCES);
			onCreate(db);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate(SQLiteDatabase database)
	{
		try
		{
			database.execSQL(DATABASE_NOTIFICATIONS_CREATE);
			database.execSQL(DATABASE_PREFERENCES_CREATE);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

}
