package com.lpi.compagnonderoute.preferences;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class PreferenceFloat
{
	private String _name;
	private float _value;
	private SharedPreferences.Editor _editor;

	public PreferenceFloat(@NonNull SharedPreferences settings, @NonNull final String name, float defaut)
	{
		_editor = settings.edit();
		_name = name;
		_value = settings.getFloat(name, defaut);
	}

	public float get() { return _value; }

	public void set(float v)
	{
		_value = v;
		_editor.putFloat(_name, v);
		_editor.apply();
	}
}
