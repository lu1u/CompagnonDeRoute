package com.lpi.compagnonderoute;



import android.app.TaskStackBuilder;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.lpi.compagnonderoute.preferences.Preferences;

/***
 * Affichage et gestion de la fenetre qui permet de choisir un theme pour l'application
 *
 */
class ThemeActivity extends AppCompatActivity
{
	/**
	 * Affichage et gestion de la fenetre Themes
	 * @param context
	 */
	public static void start(final MainActivity context)
	{
		final Preferences prefs = Preferences.getInstance(context);
		final AlertDialog dialogBuilder = new AlertDialog.Builder(context).create();

		LayoutInflater inflater = context.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.theme_choice, null);

		initTheme( context, prefs, dialogView, dialogBuilder, R.id.imageViewTheme1, 1) ;
		initTheme( context, prefs, dialogView, dialogBuilder, R.id.imageViewTheme2, 2) ;
		initTheme( context, prefs, dialogView, dialogBuilder, R.id.imageViewTheme3, 3) ;
		initTheme( context, prefs, dialogView, dialogBuilder, R.id.imageViewTheme4, 4) ;
		initTheme( context, prefs, dialogView, dialogBuilder, R.id.imageViewTheme5, 5) ;
		initTheme( context, prefs, dialogView, dialogBuilder, R.id.imageViewTheme6, 6) ;
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Afficher la fenetre
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		dialogBuilder.setView(dialogView);
		dialogBuilder.show();
	}

	private static void initTheme(@NonNull final MainActivity activity, @NonNull final Preferences prefs, @NonNull final View dialogView, @NonNull final AlertDialog dialogBuilder, @IdRes final int imageViewId, final int noTheme)
	{
		androidx.appcompat.widget.AppCompatImageView b = dialogView.findViewById(imageViewId);
		b.setOnClickListener(new View.OnClickListener()
		{
			@Override public void onClick(final View view)
			{
				if (noTheme != prefs.getTheme())
				{
					prefs.setTheme(noTheme);
					TaskStackBuilder.create(activity)
							.addNextIntent(new Intent(activity, com.lpi.compagnonderoute.MainActivity.class))
							.addNextIntent(activity.getIntent())
							.startActivities();

					dialogBuilder.dismiss();
				}
			}
		});
	}
}
