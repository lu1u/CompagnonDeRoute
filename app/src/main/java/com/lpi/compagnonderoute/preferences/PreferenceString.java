package com.lpi.compagnonderoute.preferences;

import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

public class PreferenceString extends PreferenceValue
{

	public PreferenceString(@NonNull final SQLiteDatabase database, @NonNull final String nom, final String defaut)
	{
		super(database, nom, defaut);
	}

	public String get()
	{
		return _valeur;
	}

	public void set(String v)
	{
		setValue(v);
	}
}