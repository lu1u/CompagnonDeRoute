package com.lpi.compagnonderoute.preferences;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.lpi.compagnonderoute.R;
import com.lpi.compagnonderoute.audio.AudioManagerHelper;
import com.lpi.compagnonderoute.tts.TTSService;

/***
 * Fenetre des preferences de l'application
 */
public class PreferencesActivity extends AppCompatActivity
{
	public static void start(@NonNull final Activity context)
	{
		final Preferences prefs = Preferences.getInstance(context);
		final AlertDialog dialogBuilder = new AlertDialog.Builder(context).create();

		LayoutInflater inflater = context.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.activity_preferences, null);

		////////////////////////////////////////////////////////////////////////////////////////////
		// Gerer SMS
		{
			final CheckBox cbSMS = dialogView.findViewById(R.id.checkBoxSMS);
			cbSMS.setChecked(prefs.getGererSMS());
			cbSMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final CompoundButton compoundButton, final boolean b)
				{
					prefs.setGererSMS(b);
				}
			});
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		// Gerer Mails
		{
			final CheckBox cbMails = dialogView.findViewById(R.id.checkBoxMails);
			cbMails.setChecked(prefs.getGererMails());
			cbMails.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final CompoundButton compoundButton, final boolean b)
				{
					prefs.setGererMails(b);
				}
			});
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		// Gerer WhatsApp
		{
			final CheckBox cbMails = dialogView.findViewById(R.id.checkBoxWhatsApp);
			cbMails.setChecked(prefs.getGererWhatsApp());
			cbMails.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final CompoundButton compoundButton, final boolean b)
				{
					if (compoundButton.isPressed())
						if (b)
						{
							NotificationListenerManager.checkNotificationServiceEnabled(context, new NotificationListenerManager.checkNotificationServiceEnabledListener()
							{
								// Deja autorisé, on peut cocher l'option
								@Override public void onEnabled()
								{
									cbMails.setChecked(true);
									prefs.setGererWhatsApp(true);
								}

								// Pas autorisé et l'utilisateur ne veut pas modifier les paramètres, interdire l'option
								@Override public void onCancel()
								{
									cbMails.setChecked(false);
									prefs.setGererWhatsApp(false);
								}

								// Pas autorisé, l'utilisateur a été redirigé vers l'écran de parametrage
								@Override public void onSettings()
								{
									cbMails.setChecked(false);
									prefs.setGererWhatsApp(false);
								}
							});
						}
				}
			});
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		// Reactiver apres reboot
		{
			final CheckBox cbReactiver = dialogView.findViewById(R.id.checkBoxReactiver);
			cbReactiver.setChecked(prefs.getActifApresReboot());
			cbReactiver.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final CompoundButton compoundButton, final boolean b)
				{
					prefs.setActifApresReboot(b);
				}
			});
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		// Volume
		{
			final CheckBox cbVolumeDefaut = dialogView.findViewById(R.id.checkBoxVolumeDefaut);
			final SeekBar sbVolume = dialogView.findViewById(R.id.seekBarVolume);
			cbVolumeDefaut.setChecked(prefs.getVolumeDefaut());
			sbVolume.setEnabled(!prefs.getVolumeDefaut());
			cbVolumeDefaut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final CompoundButton compoundButton, final boolean b)
				{
					prefs.setVolumeDefaut(b);
					// Le slider Volume est désactivé si on choisi le volume par defaut du systeme
					sbVolume.setEnabled(!b);
				}
			});

			sbVolume.setMin(1);
			sbVolume.setMax(10);
			sbVolume.setProgress((int)(prefs.getVolume()*10.0f));
			sbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
			{
				@Override
				public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser)
				{
					if (fromUser)
					{
						prefs.setVolume(((float)progress/10.0f));
						AudioManagerHelper.play(context, R.raw.beep, null);
					}
				}

				@Override public void onStartTrackingTouch(final SeekBar seekBar)
				{

				}

				@Override public void onStopTrackingTouch(final SeekBar seekBar)
				{

				}
			});
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Forcer la sortie vers le haut parleur
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		{
			CheckBox cbForcerSortie = dialogView.findViewById(R.id.checkBoxForceSortie);
			cbForcerSortie.setChecked(prefs.getForceSortie() == TTSService.SORTIE_FORCE_HP);
			cbForcerSortie.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final CompoundButton compoundButton, final boolean checked)
				{
					prefs.setForceSortie(checked ? TTSService.SORTIE_FORCE_HP : TTSService.SORTIE_DEFAUT);
				}
			});
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Afficher la fenetre
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		dialogBuilder.setView(dialogView);
		dialogBuilder.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preferences);
	}

}
