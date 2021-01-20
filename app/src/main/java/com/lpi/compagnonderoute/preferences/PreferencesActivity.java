package com.lpi.compagnonderoute.preferences;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
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

		// Choix de la sonnerie
		{
			ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, context.getResources().getStringArray(R.array.sons_notification));
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
