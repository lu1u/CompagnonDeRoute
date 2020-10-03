package com.lpi.compagnonderoute.themes;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.lpi.compagnonderoute.R;
import com.lpi.compagnonderoute.preferences.Preferences;

/***
 * Fonctionnalités en relation avec les Themes
 */

public class ThemesUtils
{

	public static void setTheme(@NonNull Activity a)
	{
		Preferences p = Preferences.getInstance(a);
		switch (p.getTheme())
		{

			case 1:
				a.setTheme(R.style.Theme1);
				break;

			case 2:
				a.setTheme(R.style.Theme2);
				break;

			case 3:
				a.setTheme(R.style.Theme3);
				break;

			case 4:
				a.setTheme(R.style.Theme4);
				break;

			case 5:
				a.setTheme(R.style.Theme5);
				break;
			case 6:
				a.setTheme(R.style.Theme6);
				break;

			default:
				a.setTheme(R.style.AppTheme);

		}
	}
}
