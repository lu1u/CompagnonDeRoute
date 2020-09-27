package com.lpi.compagnonderoute.preferences;

import android.app.Activity;
import android.app.AlertDialog;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
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
			sbVolume.setMax(TTSService.getMaxVolume());
			sbVolume.setProgress(prefs.getVolume());
			sbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
			{
				@Override
				public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser)
				{
					if (fromUser)
					{
						prefs.setVolume(progress);
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
		////////////////////////////////////////////////////////////////////////////////////////////
		// Canal sortie
//		{
//			final RadioGroup rgCanaux = dialogView.findViewById(R.id.radioGroupCanalSortie);
//
//
//			switch (prefs.getCanalSortie())
//			{
//				case AudioManager.STREAM_SYSTEM:
//					rgCanaux.check(R.id.radioButtonSysteme);
//					break;
//				case AudioManager.STREAM_RING:
//					rgCanaux.check(R.id.radioButtonSonnerie);
//					break;
//				case AudioManager.STREAM_NOTIFICATION:
//					rgCanaux.check(R.id.radioButtonNotification);
//					break;
//				case AudioManager.STREAM_MUSIC:
//					rgCanaux.check(R.id.radioButtonMusic);
//					break;
//				case AudioManager.STREAM_DTMF:
//					rgCanaux.check(R.id.radioButtonDTMF);
//					break;
//				case AudioManager.STREAM_ALARM:
//					rgCanaux.check(R.id.radioButtonAlarmes);
//					break;
//				case AudioManager.STREAM_ACCESSIBILITY:
//					rgCanaux.check(R.id.radioButtonAccessibilite);
//					break;
//				default:
//					rgCanaux.check(R.id.radioButtonSysteme);
//					break;
//			}
//
//			rgCanaux.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
//			{
//				@Override public void onCheckedChanged(final RadioGroup radioGroup, final int i)
//				{
//					switch (i)
//					{
//						case R.id.radioButtonSysteme:
//							prefs.setCanalSortie(AudioManager.STREAM_SYSTEM);
//							break;
//						case R.id.radioButtonSonnerie:
//							prefs.setCanalSortie(AudioManager.STREAM_RING);
//							break;
//						case R.id.radioButtonNotification:
//							prefs.setCanalSortie(AudioManager.STREAM_NOTIFICATION);
//							break;
//						case R.id.radioButtonMusic:
//							prefs.setCanalSortie(AudioManager.STREAM_MUSIC);
//							break;
//						case R.id.radioButtonDTMF:
//							prefs.setCanalSortie(AudioManager.STREAM_DTMF);
//							break;
//						case R.id.radioButtonAlarmes:
//							prefs.setCanalSortie(AudioManager.STREAM_ALARM);
//							break;
//						case R.id.radioButtonAccessibilite:
//							prefs.setCanalSortie(AudioManager.STREAM_ACCESSIBILITY);
//							break;
//					}
//
//					AudioManagerHelper.play(context, R.raw.beep, null);
//				}
//			});
//		}

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
