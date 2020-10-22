package com.lpi.compagnonderoute.sms;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.lpi.compagnonderoute.R;
import com.lpi.compagnonderoute.phone.ContactUtils;
import com.lpi.compagnonderoute.preferences.Preferences;
import com.lpi.compagnonderoute.report.Report;
import com.lpi.compagnonderoute.tts.TTSService;

public class SmsListener extends BroadcastReceiver
{
	/***
	 * Reception d'un SMS
	 * @param context
	 * @param intent
	 */
	@Override
	public void onReceive(Context context, Intent intent)
	{
		Report r = Report.getInstance(context);
		try
		{
			String action = intent.getAction();
			r.log(Report.DEBUG, "smsListener.onReceive " + action);
			if (!Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(action))
				// L'action ne nous concerne pas
				return;

			Preferences preferences = Preferences.getInstance(context);
			if (!preferences.actif.get())
				// Pas actif
				return;

			if (!preferences.gererSMS.get())
				// Ne pas lire les SMS
				return;

			// Parcourir les SMS recus
			final Bundle bundle = intent.getExtras();
			if (bundle != null)
			{
				Object[] pdus = (Object[]) bundle.get("pdus");
				if (pdus != null)
					for (int i = 0; i < pdus.length; i++)
					{
						SmsMessage message;
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
						{
							String format = bundle.getString("format");
							message = SmsMessage.createFromPdu((byte[]) pdus[i], format);
						}
						else
						{
							message = SmsMessage.createFromPdu((byte[]) pdus[i]);
						}

						annonceSms(context, message);

						int subscriptionId = bundle.getInt("subscription", -1);
						repondreSms(context, message, subscriptionId);
					}
			}
		} catch (Exception e)
		{
			r.log(Report.ERROR, "Erreur dans SmsListener.onreceive");
			r.log(Report.ERROR, e);
		}
	}

	/***
	 * Annonce l'arrivee d'un sms
	 * @param context
	 * @param message
	 */
	private void annonceSms(final Context context, final SmsMessage message)
	{
		Preferences preferences = Preferences.getInstance(context);
		final boolean expediteur = preferences.lireExpediteurSMS.get();
		final int contenu = preferences.lireContenuSms.get();

		String contact = ContactUtils.getContactFromNumber(context, message.getOriginatingAddress());
		if (contact == null)
		{
			if (preferences.lireSMS.get() == Preferences.CONTACTS_SEULS)
			{
				// N'afficher que les sms provenant d'un contact enregistré
				return;
			}
			else
				contact = TTSService.formatNumeroTelephone(message.getOriginatingAddress());
		}

		int messageId = R.string.received_sms;

		if (expediteur && contenu != Preferences.JAMAIS)
			messageId = R.string.received_sms_sender_and_body;
		else
		{
			if (expediteur)
				messageId = R.string.received_sms_sender;
			else if (contenu != Preferences.JAMAIS)
				messageId = R.string.received_sms_body;
		}

		TTSService.speakFromAnywhere(context, R.raw.beep, preferences.volumeDefaut.get() ? preferences.volume.get() : -1, messageId, contact, message.getDisplayMessageBody());
	}

	/***
	 * Reponse automatique par SMS
	 * @param context
	 * @param message
	 * @param subscriptionId
	 */
	private void repondreSms(final Context context, final SmsMessage message, int subscriptionId)
	{
		Preferences preferences = Preferences.getInstance(context);
		if (preferences.repondreSms.get() == Preferences.JAMAIS)
			return;

		String contact = ContactUtils.getContactFromNumber(context, message.getOriginatingAddress());
		if (contact == null)
		{
			if (preferences.repondreSms.get() == Preferences.CONTACTS_SEULS)
			{
				// N'afficher que les sms provenant d'un contact enregistré
				return;
			}
			else
				contact = message.getDisplayOriginatingAddress();
		}
		// Renvoyer un SMS
		send(context, contact,
				Preferences.getInstance(context).reponseSms.get() + "\n(Message envoyé automatiquement par l'application Compagnon de Route (c)2019 Lucien Pilloni)",
				subscriptionId);
	}

	/***
	 * Envoyer un SMS
	 * @param context
	 * @param adress    Destinataire
	 * @param message   Contenu du message
	 * @param subscriptionId pour le cas d'un telephone DualSIM
	 */
	public static void send(@NonNull final Context context, @NonNull final String adress, @NonNull final String message, int subscriptionId)
	{
		Report r = Report.getInstance(context);
		r.log(Report.DEBUG, "Envoi d'un SMS a " + adress);
		try
		{
			if (Build.VERSION.SDK_INT >= 22)
			{
				SmsManager smsManager;
				SubscriptionManager subscriptionManager = (context).getSystemService(SubscriptionManager.class);
				if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
				{
					// TODO: Consider calling
					//    ActivityCompat#requestPermissions
					// here to request the missing permissions, and then overriding
					//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
					//                                          int[] grantResults)
					// to handle the case where the user grants the permission. See the documentation
					// for ActivityCompat#requestPermissions for more details.
					return;
				}
				@Nullable SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfo(subscriptionId);
				if (subscriptionInfo == null)
					smsManager = SmsManager.getDefault();
				else
					smsManager = SmsManager.getSmsManagerForSubscriptionId(subscriptionInfo.getSubscriptionId());

				if (smsManager != null)
				{
					PendingIntent i1 = PendingIntent.getBroadcast(context, 0, new Intent("sent"), 0);
					PendingIntent i2 = PendingIntent.getBroadcast(context, 0, new Intent("received"), 0);
					smsManager.sendTextMessage(adress, null, message, i1, i2);
				}
			}
		} catch (Exception e)
		{
			r.log(Report.ERROR, "Erreur lors de l'envoi d'un SMS");
			r.log(Report.ERROR, e);
		}
	}
}
