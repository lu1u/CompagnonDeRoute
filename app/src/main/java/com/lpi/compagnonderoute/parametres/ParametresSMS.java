package com.lpi.compagnonderoute.parametres;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import com.lpi.compagnonderoute.ModalEditText;
import com.lpi.compagnonderoute.R;
import com.lpi.compagnonderoute.preferences.Preferences;
import com.lpi.compagnonderoute.report.Report;

public class ParametresSMS
{
	public static void start(@NonNull final Activity context)
	{
		final Report r = Report.getInstance(context);
		final Preferences preferences = Preferences.getInstance(context);

		r.log(Report.HISTORIQUE, "Parametres horloge");
		final AlertDialog dialogBuilder = new AlertDialog.Builder(context).create();

		LayoutInflater inflater = context.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.parametres_sms, null);

		// Option lire SMS
		{
			RadioGroup rgLireSMS = dialogView.findViewById(R.id.radiogroupLireSMS);
			switch (preferences.smsAnnoncer.get())
			{
//				case Preferences.JAMAIS:
//					rgLireSMS.check(R.id.radioButtonSMSJamais);
//					break;
				case Preferences.TOUJOURS:
					rgLireSMS.check(R.id.radioButtonSMSToujours);
					break;
				case Preferences.CONTACTS_SEULS:
					rgLireSMS.check(R.id.radioButtonSMSContacts);
					break;
			}

			rgLireSMS.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final RadioGroup group, @IdRes final int checkedId)
				{
					switch (checkedId)
					{
						case R.id.radioButtonSMSToujours:
							r.log(Report.HISTORIQUE, "Messages: toujours");
							preferences.smsAnnoncer.set(Preferences.TOUJOURS);
							break;
						case R.id.radioButtonSMSContacts:
							r.log(Report.HISTORIQUE, "Messages: contacts");
							preferences.smsAnnoncer.set(Preferences.CONTACTS_SEULS);
							break;
					}
				}
			});
		}

		// Annoncer l'expediteur du sms
		{
			CheckBox cbAnnoncerExpediteur = dialogView.findViewById(R.id.checkBoxExpediteur);
			cbAnnoncerExpediteur.setChecked(preferences.smsLireExpediteur.get());
			cbAnnoncerExpediteur.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final CompoundButton compoundButton, final boolean b)
				{
					if (compoundButton.isPressed())
						preferences.smsLireExpediteur.set(b);
				}
			});
		}

		// Annoncer le contenu du sms
		{
			CheckBox cbContenuSMS = dialogView.findViewById(R.id.checkBoxContenu);
			cbContenuSMS.setChecked(preferences.smsLireContenu.get());
			cbContenuSMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final CompoundButton compoundButton, final boolean b)
				{
					if (compoundButton.isPressed())
						preferences.smsLireContenu.set(b);
				}
			});
		}

		// Reponse automatique
		{
			RadioGroup rgReponseAuto = dialogView.findViewById(R.id.radiogroupReponseAutoSMS);
			switch (preferences.smsRepondre.get())
			{
				case Preferences.JAMAIS:
					rgReponseAuto.check(R.id.radioButtonJamais);
					break;
				case Preferences.TOUJOURS:
					rgReponseAuto.check(R.id.radioButtonToujours);
					break;
				case Preferences.CONTACTS_SEULS:
					rgReponseAuto.check(R.id.radioButtonContacts);
					break;
			}

			rgReponseAuto.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final RadioGroup group, @IdRes final int checkedId)
				{
					switch (checkedId)
					{
						case R.id.radioButtonJamais:
							r.log(Report.HISTORIQUE, "Reponse: jamais");
							preferences.smsRepondre.set(Preferences.JAMAIS);
							break;
						case R.id.radioButtonToujours:
							r.log(Report.HISTORIQUE, "Reponse: toujours");
							preferences.smsRepondre.set(Preferences.TOUJOURS);
							break;
						case R.id.radioButtonContacts:
							r.log(Report.HISTORIQUE, "Reponse: contacts");
							preferences.smsRepondre.set(Preferences.CONTACTS_SEULS);
							break;
					}
				}
			});
		}

		// Texte de la reponse automatique
		{
			final TextView tvReponse = dialogView.findViewById(R.id.textViewReponse);
			tvReponse.setText(preferences.smsReponse.get());
			tvReponse.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(final View v)
				{
					ModalEditText.showEditText(context, R.layout.modal_edittext, R.id.textViewTitre, R.id.editText, R.id.buttonOK,
							context.getResources().getString(R.string.ui_sms_reponse_auto), preferences.smsReponse.get(),
							new ModalEditText.ModalEditListener()
							{
								@Override
								public void onTextEdited(final String s)
								{
									preferences.smsReponse.set(s);
									context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
									tvReponse.setText(s);
								}
							});
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
