package com.lpi.compagnonderoute.preferences;

import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

public class PreferenceBoolean extends PreferenceValue
{

	public PreferenceBoolean(@NonNull final SQLiteDatabase database, @NonNull final String nom, final boolean defaut)
	{
		super(database, nom, Boolean.toString(defaut));
	}

	public boolean get()
	{
		try
		{
			return isBoolean(_valeur);
		} catch (Exception e)
		{
			return false;
		}
	}

	private static boolean isBoolean(@NonNull final String valeur)
	{
		switch (valeur.toLowerCase())
		{
			case "true":
			case "vrai":
			case "1":
				return true;

			default:
				return false;
		}
	}

	public void set(boolean v)
	{
		setValue(booleanString(v));
	}

	private @NonNull String booleanString(final boolean v)
	{
		return v ? "1" : "0";
	}
}
