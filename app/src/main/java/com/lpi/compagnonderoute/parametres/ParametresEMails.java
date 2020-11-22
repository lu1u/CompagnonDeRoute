package com.lpi.compagnonderoute.parametres;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;

import com.lpi.compagnonderoute.R;
import com.lpi.compagnonderoute.preferences.Preferences;

public class ParametresEMails
{
	public static void start(@NonNull final Activity context)
	{
		final Preferences preferences = Preferences.getInstance(context);
		final AlertDialog dialogBuilder = new AlertDialog.Builder(context).create();

		LayoutInflater inflater = context.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.parametres_email, null);

		// Annoncer l'expediteur?
		{
			CheckBox cbExpediteur = dialogView.findViewById(R.id.checkBoxExpediteur);
			cbExpediteur.setChecked(preferences.eMailsAnnonceExpediteur.get());
			cbExpediteur.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(@NonNull final CompoundButton compoundButton, final boolean b)
				{
					if (compoundButton.isPressed())
						preferences.eMailsAnnonceExpediteur.set(b);
				}
			});
		}

		// Annoncer le sujet?
		{
			CheckBox cbSujet = dialogView.findViewById(R.id.checkBoxSujet);
			cbSujet.setChecked(preferences.eMailsAnnonceSujet.get());
			cbSujet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(@NonNull final CompoundButton compoundButton, final boolean b)
				{
					if (compoundButton.isPressed())
						preferences.eMailsAnnonceSujet.set(b);
				}
			});
		}
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Afficher la fenetre
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		dialogBuilder.setView(dialogView);
		dialogBuilder.show();
	}

}
