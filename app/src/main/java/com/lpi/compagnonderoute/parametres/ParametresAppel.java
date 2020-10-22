package com.lpi.compagnonderoute.parametres;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import com.lpi.compagnonderoute.R;
import com.lpi.compagnonderoute.preferences.Preferences;
import com.lpi.compagnonderoute.report.Report;

public class ParametresAppel
{
	public static void start(@NonNull final Activity context)
	{
		final Report r = Report.getInstance(context);
		final Preferences preferences = Preferences.getInstance(context);
		final AlertDialog dialogBuilder = new AlertDialog.Builder(context).create();

		LayoutInflater inflater = context.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.parametres_telephone, null);

		// Option annoncer appel
		{
			RadioGroup rgAnnoncerAppel = dialogView.findViewById(R.id.radiogroupAnnoncerAppels);
			switch (preferences.telephoneAnnoncer.get())
			{
				case Preferences.TOUJOURS:
					rgAnnoncerAppel.check(R.id.radioButtonAppelsToujours);
					break;
				case Preferences.CONTACTS_SEULS:
					rgAnnoncerAppel.check(R.id.radioButtonAppelsContacts);
					break;
			}

			rgAnnoncerAppel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final RadioGroup group, @IdRes final int checkedId)
				{
					switch (checkedId)
					{
						case R.id.radioButtonAppelsToujours:
							r.log(Report.HISTORIQUE, "appels: toujours");
							preferences.telephoneAnnoncer.set(Preferences.TOUJOURS);
							break;
						case R.id.radioButtonAppelsContacts:
							r.log(Report.HISTORIQUE, "appels: contacts");
							preferences.telephoneAnnoncer.set(Preferences.CONTACTS_SEULS);
							break;
					}
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
