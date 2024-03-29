package com.lpi.compagnonderoute.tts;
/***
 * Service pour permettre de faire du TextToSpeech en arriere plan ou depuis un BroadcastReceiver,
 * encore une restriction d'Android
 *
 * Appel par speakFromAnywhere
 */

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.telephony.PhoneNumberUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;

import com.lpi.compagnonderoute.MainActivity;
import com.lpi.compagnonderoute.R;
import com.lpi.compagnonderoute.report.Report;

public class TTSService extends Service //implements AudioManager.OnAudioFocusChangeListener
{
	private static final String SOUNDID_EXTRA = "TTSService.soundId";
	public static final String MESSAGE_EXTRA = "TTSService.message";
	public static final String VOLUME_EXTRA = "TTSService.volume";
	public static final float VOLUME_MAX = 1.0f;
	private MediaPlayer _mediaPlayer;
	private TextToSpeech _textToSpeech;

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
				int idSon = intent.getIntExtra(SOUNDID_EXTRA, 0);

				// Android: un foreground service doit obligatoirement afficher une notification
				startForeground((int) System.currentTimeMillis(), getNotification(this, message, message, idSon));

				if (message != null)
				{
					float volume = intent.getFloatExtra(VOLUME_EXTRA, -1);
					int soundId = intent.getIntExtra(SOUNDID_EXTRA, 0);

					TTSService t = new TTSService();
					t.parler(this, soundId, volume, message, new TTSServiceListener()
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
				}
			} catch (Exception e)
			{
				r.log(Report.ERROR, "Erreur dans TTSService.onStartCommand");
				r.log(Report.ERROR, e);
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	/***
	 * Prononcer un texte parametré, avec un format provenant directement des resources
	 * @param ctx
	 * @param soundID id du son a jouer
	 * @param volume volume sonore entre 0 et 1.0
	 * @param resId Id de la resource String
	 * @param args Liste variable de parametres
	 */
	public static void speakFromAnywhere(@NonNull Context ctx, int soundID, float volume, @StringRes int resId, Object... args)
	{
		String format = ctx.getResources().getString(resId);
		speakFromAnywhere(ctx, soundID, volume, String.format(format, args));
	}

	/***
	 * Prononcer un texte
	 * @param context
	 * @param soundID id du son a jouer avant de dire le texte
	 * @param volume: volume de 0.0 à 1.0, valeur negative pour garder le niveau sonore du systeme
	 * @param message message à prononcer
	 */
	public static void speakFromAnywhere(@NonNull Context context, int soundID, float volume, @NonNull final String message)
	{
		Report r = Report.getInstance(context);
		try
		{
			r.log(Report.DEBUG, "TTS: \"" + message + "\"");
			Intent speechIntent = new Intent(context, TTSService.class);
			speechIntent.putExtra(MESSAGE_EXTRA, message);
			speechIntent.putExtra(VOLUME_EXTRA, volume);
			speechIntent.putExtra(SOUNDID_EXTRA, soundID);
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

	/***
	 * Obtient une Notification (obligatoire sous Android pour un foregroundService)
	 * @param context
	 * @param texte
	 * @param texteResume
	 * @return
	 */
	public static @NonNull
	android.app.Notification getNotification(@NonNull Context context, @NonNull final String texte, @NonNull final String texteResume, int idSon)
	{
		Intent ii = new Intent(context.getApplicationContext(), MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, ii, 0);

		final String appName = context.getString(R.string.app_name);
		final NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
		bigText.bigText(texte).setBigContentTitle(appName).setSummaryText(texteResume);

		final NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), appName + System.currentTimeMillis());
		builder.setDefaults(Notification.FLAG_FOREGROUND_SERVICE | Notification.BADGE_ICON_SMALL)
				.setContentIntent(pendingIntent)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(appName)
				.setContentText(texteResume)
				.setStyle(bigText)
				.setLights(0, 1, 1)
				.setPriority(NotificationCompat.PRIORITY_MIN)
				.setAutoCancel(true);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
		{
			final String CHANNEL_ID = appName + System.currentTimeMillis();

			final AudioAttributes att = new AudioAttributes.Builder()
					.setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
					.setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
					.build();

			final NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
			NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, appName, NotificationManager.IMPORTANCE_DEFAULT);
			notificationChannel.setSound(null, null);
			notificationChannel.setShowBadge(false);
			notificationChannel.setImportance(NotificationManager.IMPORTANCE_DEFAULT);
			notificationChannel.enableLights(false);
			notificationChannel.enableVibration(false);
			notificationChannel.setImportance(NotificationManager.IMPORTANCE_LOW);
			nm.createNotificationChannel(notificationChannel);
			builder.setChannelId(CHANNEL_ID);
		}
		return builder.build();
	}

	/***
	 * Prononce un texte par synthese vocale, précédé d'une sonnerie
	 * @param context
	 * @param message
	 * @param listener
	 */
	private void parler(@NonNull final Context context, final int soundId, final float volume, @NonNull final String message, @Nullable final TTSServiceListener listener)
	{
		@NonNull final Report r = Report.getInstance(context);
		try
		{
			_textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener()
			{
				/***
				 * TTS Initialisé (ou erreur)
				 * @param ttsStatus
				 */
				@Override public void onInit(final int ttsStatus)
				{
					if (ttsStatus != TextToSpeech.SUCCESS)
					{
						r.log(Report.ERROR, "Erreur TTS.init " + ttsStatus);
						if (listener != null)
							listener.result(ttsStatus);
						return;
					}

					_textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener()
					{
						@Override public void onStart(final String s){}

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
								r.log(Report.ERROR, "Erreur dans TTSService.parler.onDone");
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

					final Bundle params = new Bundle();
					params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "lpi.TTSService");
					if (volume >= 0.0f)
						params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, volume);

					// Sonnerie avant le discours
					_mediaPlayer = MediaPlayer.create(context, soundId);
					_mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
					{
						@Override public void onCompletion(final MediaPlayer mediaPlayer)
						{
							_mediaPlayer.release();
						}
					});

					_mediaPlayer.seekTo(0);
					_mediaPlayer.start();

					// Et enfin... prononcer le discours
					_textToSpeech.speak(message, TextToSpeech.QUEUE_ADD, params, "lpi.TTSService");

				}
			});
		} catch (Exception e)
		{
			r.log(Report.ERROR, "Erreur dans TTSService.parler");
			r.log(Report.ERROR, e);
		}
	}

//	private void demandeFocuseRequest(final Context context, MediaPlayer mMediaPlayer)
//	{
//		// initialization of the audio attributes and focus request
//		AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//		AudioAttributes  mPlaybackAttributes = new AudioAttributes.Builder()
//				.setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
//				.setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
//				.build();
//
//
//		AudioFocusRequest mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
//				.setAudioAttributes(mPlaybackAttributes)
//				.setAcceptsDelayedFocusGain(true)
//				.setWillPauseWhenDucked(true)
//				.setOnAudioFocusChangeListener(this,new Handler())
//				.build();
//		mMediaPlayer = new MediaPlayer();
//		mMediaPlayer.setAudioAttributes(mPlaybackAttributes);
//		final Object mFocusLock = new Object();
//
//		boolean mPlaybackDelayed = false;
//
//		// requesting audio focus
//		int res = mAudioManager.requestAudioFocus(mFocusRequest);
//		synchronized (mFocusLock)
//		{
//			if (res == AudioManager.AUDIOFOCUS_REQUEST_FAILED)
//			{
//				mPlaybackDelayed = false;
//			}
//			else if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
//			{
//				mPlaybackDelayed = false;
//				//playbackNow();
//			}
//			else if (res == AudioManager.AUDIOFOCUS_REQUEST_DELAYED)
//			{
//				mPlaybackDelayed = true;
//			}
//		}
//
//
//	}

	/***
	 * Fermer le service quand le message a été prononcé
	 */
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

//	private static Uri getUri(@NonNull final Context context, final int soundId)
//	{
//		return Uri.parse("android.resource://" + context.getApplicationContext().getPackageName() + "/" + soundId);
//	}
//
//	@Override public void onAudioFocusChange( int focusChange)
//	{
//		if ( mFocusLock==null)
//			mFocusLock = new Object();
//		switch (focusChange)
//		{
//			case AudioManager.AUDIOFOCUS_GAIN:
//				if (mPlaybackDelayed || mResumeOnFocusGain)
//				{
//					synchronized (mFocusLock)
//					{
//						mPlaybackDelayed = false;
//						mResumeOnFocusGain = false;
//					}
//					//playbackNow();
//				}
//				break;
//			case AudioManager.AUDIOFOCUS_LOSS:
//				synchronized (mFocusLock)
//				{
//					// this is not a transient loss, we shouldn't automatically resume for now
//					mResumeOnFocusGain = false;
//					mPlaybackDelayed = false;
//				}
//				//pausePlayback();
//				break;
//			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
//			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
//				// we handle all transient losses the same way because we never duck audio books
//				synchronized (mFocusLock)
//				{
//					// we should only resume if playback was interrupted
//					mResumeOnFocusGain = _mediaPlayer.isPlaying();
//					mPlaybackDelayed = false;
//				}
//				//pausePlayback();
//				break;
//		}
//	}

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
