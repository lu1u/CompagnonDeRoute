package com.lpi.compagnonderoute.preferences;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import com.lpi.compagnonderoute.R;
import com.lpi.compagnonderoute.audio.AudioManagerHelper;
import com.lpi.compagnonderoute.notificationListener.NotificationListenerManager;
import com.lpi.compagnonderoute.tts.TTSService;

/***
 * Fenetre des preferences de l'application
 */
public class PreferencesActivity //extends AppCompatActivity
{
	public static void start(@NonNull final Activity context)
	{
		final Preferences prefs = Preferences.getInstance(context);
		final AlertDialog dialogBuilder = new AlertDialog.Builder(context).create();

		LayoutInflater inflater = context.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.activity_preferences, null);

		////////////////////////////////////////////////////////////////////////////////////////////
		// Raccourcis vers l'ecran de configuration de l'interception des notifications
		{
			final Button bSettings = dialogView.findViewById(R.id.buttonNotificationSettings);
			bSettings.setOnClickListener(new View.OnClickListener()
			{
				@Override public void onClick(final View view)
				{
					NotificationListenerManager.displayNotificationSettings(context);
				}
			});
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		// Reactiver apres reboot
		{
			final CheckBox cbReactiver = dialogView.findViewById(R.id.checkBoxReactiver);
			cbReactiver.setChecked(prefs.actifApresReboot.get());
			cbReactiver.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final CompoundButton compoundButton, final boolean b)
				{
					prefs.actifApresReboot.set(b);
				}
			});
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		// Volume
		{
			final CheckBox cbVolumeDefaut = dialogView.findViewById(R.id.checkBoxVolumeDefaut);
			final SeekBar sbVolume = dialogView.findViewById(R.id.seekBarVolume);
			cbVolumeDefaut.setChecked(prefs.volumeDefaut.get());
			sbVolume.setEnabled(!prefs.volumeDefaut.get());
			cbVolumeDefaut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final CompoundButton compoundButton, final boolean b)
				{
					prefs.volumeDefaut.set(b);
					// Le slider Volume est désactivé si on choisi le volume par defaut du systeme
					sbVolume.setEnabled(!b);
				}
			});

			sbVolume.setMin(1);
			sbVolume.setMax(10);
			sbVolume.setProgress((int) (prefs.volume.get() * 10.0f));
			sbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
			{
				@Override
				public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser)
				{
					if (fromUser)
					{
						prefs.volume.set(((float) progress / 10.0f));
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
			cbForcerSortie.setChecked(prefs.forceSortie.get() == TTSService.SORTIE_FORCE_HP);
			cbForcerSortie.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final CompoundButton compoundButton, final boolean checked)
				{
					prefs.forceSortie.set(checked ? TTSService.SORTIE_FORCE_HP : TTSService.SORTIE_DEFAUT);
				}
			});
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Afficher la fenetre
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		dialogBuilder.setView(dialogView);
		dialogBuilder.show();
	}

//	@Override
//	protected void onCreate(Bundle savedInstanceState)
//	{
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_preferences);
//	}

}
