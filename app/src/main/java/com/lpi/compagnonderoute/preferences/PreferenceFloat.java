package com.lpi.compagnonderoute.preferences;

import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

public class PreferenceFloat extends PreferenceValue
{

	public PreferenceFloat(@NonNull final SQLiteDatabase database, @NonNull final String nom, final float defaut)
	{
		super(database, nom, Float.toString(defaut));
	}

	public float get()
	{
		try
		{
			return Float.parseFloat(_valeur);
		} catch (Exception e)
		{
			return 0.0f;
		}
	}

	public void set(float v)
	{
		setValue(Float.toString(v));
	}
}