package com.lpi.compagnonderoute.tts;
/***
 * Service pour permettre de faire du TextToSpeech en arriere plan ou depuis un BroadcastReceiver,
 * encore une restriction d'Android
 *
 * Appel par speakFromAnywhere
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.telephony.PhoneNumberUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.lpi.compagnonderoute.R;
import com.lpi.compagnonderoute.audio.AudioManagerHelper;
import com.lpi.compagnonderoute.preferences.Preferences;
import com.lpi.compagnonderoute.report.Report;

public class TTSService extends Service
{
	public static final String MESSAGE_EXTRA = "TTSService.message";
	private TextToSpeech _textToSpeech;
	private boolean _eventTTSInitialise;
	private boolean _eventSonJoue;

	////////////////////////////////////////////////////////////////////////////////////////////////

	/***
	 * Prononcer un texte parametré, avec un format provenant directement des resources
	 * @param ctx
	 * @param resId Id de la resource String
	 * @param args Liste variable de parametres
	 */
	public static void speakFromAnywhere(@NonNull Context ctx, @StringRes int resId, Object... args)
	{
		String format = ctx.getResources().getString(resId);
		speakFromAnywhere(ctx, String.format(format, args));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/***
	 * Prononcer un texte
	 * @param context
	 * @param message message à prononcer
	 */
	public static void speakFromAnywhere(@NonNull Context context, @NonNull final String message)
	{
		Report r = Report.getInstance(context);
		try
		{
			r.log(Report.DEBUG, "TTS: " + message);
			Intent speechIntent = new Intent(context, TTSService.class);
			speechIntent.putExtra(MESSAGE_EXTRA, message);

			// Lancer le service qui se chargera de faire la synthese locale, car on ne peut pas le
			// faire avec le context recu par un BroadcastReceiver (limitation d'Android)
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
				context.getApplicationContext().startForegroundService(speechIntent);
			else
				context.getApplicationContext().startService(speechIntent);

		} catch (Exception e)
		{
			r.log(Report.ERROR, "Erreur dans TTSHelper.speakFromAnywhere");
			r.log(Report.ERROR, e);
		}
	}

	/***
	 * Formatte un numero de telephone pour qu'il soit intelligible en TTS
	 * exemple: si on a 6611223344, le TTS dira "Six milliard..."
	 * -> on le remplace par 66 11 22 33 44
	 * @param numero
	 * @return
	 */
	public static String formatNumeroTelephone(final @NonNull String numero)
	{
		if (PhoneNumberUtils.isGlobalPhoneNumber(numero))
		{
			// Supprimer tous les espaces
			String n = numero.replaceAll("\\s", "");

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < n.length() - 1; i += 2)
				sb.append(n.charAt(i)).append(n.charAt(i + 1)).append(" ");

			if (n.length() % 2 != 0)
				sb.append(n.charAt(n.length() - 1));

			return sb.toString();
		}
		else
			return numero;
	}

	public static int getMaxVolume()
	{
		return 10;
	}

	/***
	 * Prononce un text par synthese vocale
	 * @param context
	 * @param message
	 * @param listener
	 */
	private static void speak(@NonNull final Context context, @NonNull final String message, @Nullable final TTSServiceListener listener)
	{
		try
		{
			new TTSService().doSpeak(context, message, listener);
		} catch (Exception e)
		{
			Report r = Report.getInstance(context);
			r.log(Report.ERROR, "Erreur dans TTSHelper.speakFromAnywhere");
			r.log(Report.ERROR, e);
		}
	}

	/***
	 * Prononce un texte par synthese vocale, précédé d'une sonnerie
	 * @param context
	 * @param message
	 * @param listener
	 */
	private void doSpeak(@NonNull final Context context, @NonNull final String message, @Nullable final TTSServiceListener listener)
	{
		final Report r = Report.getInstance(context);

		try
		{
			_eventTTSInitialise = false;
			_eventSonJoue = false;

			// Emettre un son avant la synthese vocale
			AudioManagerHelper.play(context, R.raw.beep, new AudioManagerHelper.AudioManagerHelperListener()
			{
				//Sonnerie terminee, commencer a parler quand le tts est initialisé
				@Override public void onFinished()
				{
					_eventSonJoue = true;
					discours(context, message, listener);
				}
			});

			// Pendant que le son se joue, initialiser le TTS
			_textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener()
			{
				////////////////////////////////////////////////////////////////////////////////////

				/**
				 * Le tts est initialisé
				 * @param ttsStatus
				 */
				@Override public void onInit(final int ttsStatus)
				{
					if (listener != null)
						listener.onInit(ttsStatus);

					if (ttsStatus != TextToSpeech.SUCCESS)
					{
						r.log(Report.ERROR, "Erreur TTS.init " + ttsStatus);
						return;
					}

					_eventTTSInitialise = true;
					discours(context, message, listener);
				}
			});


		} catch (Exception e)
		{
			r.log(Report.ERROR, "Erreur dans TTSService.doSpeak");
			r.log(Report.ERROR, e);
		}
	}

	/***
	 * Methode appellée à la fin de l'initialisation du TTS ET à la fin du son joué avant l'annonce,
	 * dans un ordre quelconque
	 * Quand les deux evenements sont arrivés, prononcer le message
	 * @param context
	 * @param message
	 * @param listener
	 */
	private void discours(@NonNull final Context context, @NonNull final String message, @Nullable final TTSServiceListener listener)
	{
		final Report r = Report.getInstance(context);
		if (!_eventTTSInitialise)
		{
			r.log(Report.DEBUG, "TTSService.discours: TTS pas encore initialisé");
			return;
		}

		if (!_eventSonJoue)
		{
			r.log(Report.DEBUG, "TTSService.discours: Son pas encore joué");
			return;
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		// Le son est joué ET le TTS est initialisé
		////////////////////////////////////////////////////////////////////////////////////////////
		r.log(Report.DEBUG, "TTSService.discours: Son joué et TTS initialisé");
		try
		{
			final Preferences preferences = Preferences.getInstance(context);
			final Bundle params = new Bundle();
			params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "lpi.TTSHelper");
			if (!preferences.getVolumeDefaut())
			{
				final float volume = (float) preferences.getVolume() / (float) TTSService.getMaxVolume();
				params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, volume);
			}

			_textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener()
			{
				@Override public void onStart(final String s)
				{
				}

				@Override public void onDone(final String s)
				{
					try
					{
						_textToSpeech.shutdown();
						_textToSpeech = null;
						if (listener != null)
							listener.result(TextToSpeech.SUCCESS);
					} catch (Exception e)
					{
						r.log(Report.ERROR, "Erreur dans TTSService.discours.onDone");
						r.log(Report.ERROR, e);
					}
				}

				@Override public void onError(final String s)
				{
					r.log(Report.ERROR, "TTS error " + s);
					if (listener != null)
						listener.result(TextToSpeech.ERROR);
				}
			});

			_textToSpeech.speak(message, TextToSpeech.QUEUE_ADD, params, "lpi.TTSService");
		} catch (Exception e)
		{
			r.log(Report.ERROR, "Erreur dans TTSService.speak");
			r.log(Report.ERROR, e);
		}

		// Attention! quand on sort de cette fonction, le message n'est pas encore prononcé
		// (d'ou le UtteranceProgressListener)
	}

	/***
	 * Demarrage du service
	 * @param intent
	 * @param flags
	 * @param startId
	 * @return
	 */
	@Override public int onStartCommand(final Intent intent, final int flags, final int startId)
	{
		final Report r = Report.getInstance(this);
		r.log(Report.DEBUG, "TTSService.onStartCommand");
		if (intent != null)
		{
			try
			{
				String message = intent.getStringExtra(MESSAGE_EXTRA);
				if (message != null)
					speak(this, message, new TTSServiceListener()
					{
						@Override public void result(final int ttsStatus)
						{
							r.log(Report.DEBUG, "speak status: " + ttsStatus);
							fermerService();
						}

						@Override public void onInit(final int status)
						{
							r.log(Report.DEBUG, "TTS initialisé");
							if (status != TextToSpeech.SUCCESS)
								// Impossible d'initialiser le TTS, fermer ce service
								fermerService();
						}
					});
			} catch (Exception e)
			{
				r.log(Report.ERROR, "Erreur dans TTSService.onStartCommand");
				r.log(Report.ERROR, e);
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void fermerService()
	{
		try
		{
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
				stopForeground(true);
			else
				stopSelf();
		} catch (Exception e)
		{
			Report r = Report.getInstance(this);
			r.log(Report.ERROR, "Erreur dans TTSService.fermerService");
			r.log(Report.ERROR, e);
		}
	}

	/***
	 * Permet de fournir un listener pour etre mis au courant de l'evolution du TTS
	 */
	private interface TTSServiceListener
	{
		void result(final int ttsStatus);
		void onInit(final int status);
	}

	// Suite: de la pure cuisine Android
	public TTSService()
	{
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}


}
