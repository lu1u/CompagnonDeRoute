package com.lpi.compagnonderoute.preferences;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class PreferenceString
{
	private String _name;
	private String _value;
	private SharedPreferences.Editor _editor;

	public PreferenceString(@NonNull SharedPreferences settings, @NonNull final String name, String defaut)
	{
		_editor = settings.edit();
		_name = name;
		_value = settings.getString(name, defaut);
	}

	public String get() { return _value; }

	public void set(String v)
	{
		_value = v;
		_editor.putString(_name, v);
		_editor.apply();
	}
}
