package com.lpi.compagnonderoute.audio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lpi.compagnonderoute.preferences.Preferences;
import com.lpi.compagnonderoute.report.Report;

public class AudioManagerHelper
{
	public interface AudioManagerHelperListener
	{
		void onFinished();
	}

	/***
	 * Jouer un son, parametré par les preferences de l'application et eventuellement être averti
	 * quand c'est fini
	 * @param context
	 * @param idSon
	 * @param listener
	 */
	@SuppressLint("WrongConstant")
	public static void play(@NonNull final Context context, int idSon, @Nullable final AudioManagerHelperListener listener)
	{
		try
		{
			final Preferences preferences = Preferences.getInstance(context);
			MediaPlayer mediaPlayer = MediaPlayer.create(context, idSon);

			// Volume, si pas volume par defaut
			if (!preferences.volumeDefaut.get())
			{
				mediaPlayer.setVolume(preferences.volume.get(), preferences.volume.get());
			}

			if (listener != null)
				// Listener pour notification de la fin du son
				mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
				{
					@Override public void onCompletion(final MediaPlayer mediaPlayer)
					{
						listener.onFinished();
					}
				});

			mediaPlayer.seekTo(0);
			mediaPlayer.start();
		} catch( Exception e)
		{
			Report r = Report.getInstance(context);
			r.log(Report.ERROR, "Erreur dans AudioManagerHelper.play");
			r.log(Report.ERROR, e);
		}
	}
}
