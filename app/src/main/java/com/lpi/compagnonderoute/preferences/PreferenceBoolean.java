package com.lpi.compagnonderoute.preferences;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class PreferenceBoolean
{
	private String _name;
	private boolean _value;
	private SharedPreferences.Editor _editor;

	public PreferenceBoolean(@NonNull SharedPreferences settings, @NonNull final String name, boolean defaut)
	{
		_editor = settings.edit();
		_name = name;
		_value = settings.getBoolean(name, defaut);
	}

	public boolean get() { return _value; }

	public void set(boolean v)
	{
		_value = v;
		_editor.putBoolean(_name, v);
		_editor.apply();
	}
}
