package com.lpi.compagnonderoute.parametres;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import com.lpi.compagnonderoute.Carillon;
import com.lpi.compagnonderoute.R;
import com.lpi.compagnonderoute.preferences.Preferences;
import com.lpi.compagnonderoute.report.Report;

public class ParametresHorloge
{
	public static void start(@NonNull final Activity context)
	{
		final Report r = Report.getInstance(context);
		r.log(Report.HISTORIQUE, "Parametres horloge");
		final AlertDialog dialogBuilder = new AlertDialog.Builder(context).create();

		LayoutInflater inflater = context.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.parametres_horloge, null);

		final Preferences preferences = Preferences.getInstance(context);
		RadioGroup rgAnnonceHeure = dialogView.findViewById(R.id.radiogroupAnnonceHeure);
		switch (preferences.delaiAnnonceHeure.get())
		{
			case Preferences.DELAI_ANNONCE_HEURE_HEURES:
				rgAnnonceHeure.check(R.id.radioButtonAnnonceHeure);
				break;
			case Preferences.DELAI_ANNONCE_HEURE_DEMI:
				rgAnnonceHeure.check(R.id.radioButtonAnnonceDemi);
				break;
			case Preferences.DELAI_ANNONCE_HEURE_QUART:
				rgAnnonceHeure.check(R.id.radioButtonAnnonceQuart);
				break;
			default:
				break;
		}

		rgAnnonceHeure.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(final RadioGroup group, @IdRes final int checkedId)
			{
				switch (checkedId)
				{
					case R.id.radioButtonAnnonceHeure:
						r.log(Report.HISTORIQUE, "Carillon: heures");
						preferences.delaiAnnonceHeure.set(Preferences.DELAI_ANNONCE_HEURE_HEURES);
						break;
					case R.id.radioButtonAnnonceDemi:
						r.log(Report.HISTORIQUE, "Carillon: demi");
						preferences.delaiAnnonceHeure.set(Preferences.DELAI_ANNONCE_HEURE_DEMI);
						break;
					case R.id.radioButtonAnnonceQuart:
						r.log(Report.HISTORIQUE, "Carillon: quart");
						preferences.delaiAnnonceHeure.set(Preferences.DELAI_ANNONCE_HEURE_QUART);
						break;
				}

				Carillon.changeDelai(context);
			}
		});

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Afficher la fenetre
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		dialogBuilder.setView(dialogView);
		dialogBuilder.show();
	}
}
