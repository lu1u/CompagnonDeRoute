package com.lpi.compagnonderoute.preferences;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class PreferenceInt
{
	private String _name;
	private int _value;
	private SharedPreferences.Editor _editor;

	public PreferenceInt(@NonNull SharedPreferences settings, @NonNull final String name, int defaut)
	{
		_editor = settings.edit();
		_name = name;
		_value = settings.getInt(name, defaut);
	}

	public int get() { return _value; }

	public void set(int v)
	{
		_value = v;
		_editor.putInt(_name, v);
		_editor.apply();
	}
}
