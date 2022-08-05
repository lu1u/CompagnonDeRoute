package com.lpi.compagnonderoute.preferences;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lpi.compagnonderoute.R;
import com.lpi.compagnonderoute.audio.AudioManagerHelper;

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

		// Choix de la sonnerie
		{
			ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, context.getResources().getStringArray(R.array.sons_notification));
			spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			final Spinner spinner = dialogView.findViewById(R.id.spinnerChoixSon);
			spinner.setAdapter(spinnerArrayAdapter);
			spinner.setSelection(prefs.sonNotification.get(), false);
			final int[] ids = getIntArray(context, R.array.id_sons_notification);
			spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
			{
				MediaPlayer _mp;

				@Override
				public void onItemSelected(final AdapterView<?> adapterView, final View view, final int selected, final long l)
				{
					// Jouer la nouvelle sonnerie
					if (_mp != null)
					{
						_mp.stop();
						_mp.release();
					}

					if (ids != null)
					{
						_mp = MediaPlayer.create(context, ids[selected]);
						_mp.start();
					}

					prefs.sonNotification.set(selected);
				}

				@Override public void onNothingSelected(final AdapterView<?> adapterView)
				{

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

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
			{
				sbVolume.setMin(1);
			}
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
						AudioManagerHelper.play(context, prefs.getSoundId(context), null);
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
		// Afficher la fenetre
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		dialogBuilder.setView(dialogView);
		dialogBuilder.show();
	}


	/***
	 * Charge un tableau d'entiers depuis les ressources arrays.xml
	 * @param context
	 * @param arrayId
	 * @return
	 */
	public static @Nullable
	int[] getIntArray(final @NonNull Context context, final @ArrayRes int arrayId)
	{
		TypedArray ar = context.getResources().obtainTypedArray(arrayId);
		if (ar == null)
			return null;

		int len = ar.length();

		int[] ints = new int[len];

		for (int i = 0; i < len; i++)
			ints[i] = ar.getResourceId(i, 0);

		ar.recycle();

		return ints;
	}
}
