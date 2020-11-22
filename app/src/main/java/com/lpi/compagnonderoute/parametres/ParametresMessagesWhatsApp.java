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

public class ParametresMessagesWhatsApp
{
	public static void start(@NonNull final Activity context)
	{
		final Preferences preferences = Preferences.getInstance(context);
		final AlertDialog dialogBuilder = new AlertDialog.Builder(context).create();

		LayoutInflater inflater = context.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.parametres_message_whatsapp, null);

		// Annoncer l'expediteur du sms
		{
			CheckBox cbAnnoncerExpediteur = dialogView.findViewById(R.id.checkBoxExpediteur);
			cbAnnoncerExpediteur.setChecked(preferences.messageWhatsAppLireExpediteur.get());
			cbAnnoncerExpediteur.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final CompoundButton compoundButton, final boolean b)
				{
					if (compoundButton.isPressed())
						preferences.messageWhatsAppLireExpediteur.set(b);
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
