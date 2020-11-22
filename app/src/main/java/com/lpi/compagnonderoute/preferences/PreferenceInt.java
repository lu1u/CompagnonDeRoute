package com.lpi.compagnonderoute.preferences;

import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

public class PreferenceInt extends PreferenceValue
{

	public PreferenceInt(@NonNull final SQLiteDatabase database, @NonNull final String nom, final int defaut)
	{
		super(database, nom, Integer.toString(defaut));
	}

	public int get()
	{
		try
		{
			return Integer.parseInt(_valeur);
		} catch (Exception e)
		{
			return 0;
		}
	}

	public void set(int v)
	{
		setValue(Integer.toString(v));
	}
}