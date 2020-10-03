package com.lpi.compagnonderoute.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import com.lpi.compagnonderoute.ContactUtils;
import com.lpi.compagnonderoute.preferences.Preferences;
import com.lpi.compagnonderoute.R;
import com.lpi.compagnonderoute.report.Report;
import com.lpi.compagnonderoute.tts.TTSService;

public class SmsListener extends BroadcastReceiver
{
	static public final String TAG = "SmsListener";


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
			if (!preferences.getActif())
				// Pas actif
				return;

			if (preferences.getLireSms() == Preferences.JAMAIS)
				// Ne pas lire les sms
				return;

			// Parcourir les SMS recus
			final Bundle bundle = intent.getExtras();
			if (bundle != null)
			{
				Object[] pdus = (Object[]) bundle.get("pdus");

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
	 * Reponse automatique par SMS
	 * @param context
	 * @param message
	 * @param subscriptionId
	 */
	private void repondreSms(final Context context, final SmsMessage message, int subscriptionId)
	{
		Preferences preferences = Preferences.getInstance(context);
		if (preferences.getRepondreSms() == Preferences.JAMAIS)
			return;

		String contact = ContactUtils.getContactFromNumber(context, message.getOriginatingAddress());
		if (contact == null)
		{
			if (preferences.getRepondreSms() == Preferences.CONTACTS_SEULS)
			{
				// N'afficher que les sms provenant d'un contact enregistré
				return;
			}
			else
				contact = message.getDisplayOriginatingAddress();
		}
		// Renvoyer un SMS
		SMSUtils.send(context, contact,
				Preferences.getInstance(context).getReponseSms() + "\n(Message envoyé automatiquement par l'application Compagnon de Route (c)2019 Lucien Pilloni)",
				subscriptionId);
	}

	/***
	 * Annonce l'arrivee d'un sms
	 * @param context
	 * @param message
	 */
	private void annonceSms(final Context context, final SmsMessage message)
	{
		Preferences preferences = Preferences.getInstance(context);
		String contact = ContactUtils.getContactFromNumber(context, message.getOriginatingAddress());

		if (contact == null)
			if (preferences.getLireSms() == Preferences.CONTACTS_SEULS)
			{
				// N'afficher que les sms provenant d'un contact enregistré
				return;
			}
			else
				contact = TTSService.formatNumeroTelephone( message.getOriginatingAddress());

			if ( preferences.getLireContenuSms())
				TTSService.speakFromAnywhere(context, R.raw.beep, preferences.getVolumeDefaut()? preferences.getVolume():-1, R.string.received_message_with_body, contact, message.getDisplayMessageBody());
			else
				TTSService.speakFromAnywhere(context, R.raw.beep, preferences.getVolumeDefaut()? preferences.getVolume():-1, R.string.received_message, contact);

	}

}
