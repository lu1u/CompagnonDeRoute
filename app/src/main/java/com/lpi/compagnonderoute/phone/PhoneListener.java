package com.lpi.compagnonderoute.phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lpi.compagnonderoute.R;
import com.lpi.compagnonderoute.database.NotificationDatabase;
import com.lpi.compagnonderoute.preferences.Preferences;
import com.lpi.compagnonderoute.report.Report;
import com.lpi.compagnonderoute.tts.TTSService;

public class PhoneListener extends BroadcastReceiver
{
	private static int lastState = TelephonyManager.CALL_STATE_IDLE;

	/***
	 * Reception de l'evenement de changement d'etat du telephone
	 * @param context
	 * @param intent
	 */
	@Override
	public void onReceive(@NonNull Context context, Intent intent)
	{
		Report r = Report.getInstance(context);
		try
		{
		if (!TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(intent.getAction()))
			return;

		Preferences preferences = Preferences.getInstance(context);
		if (!preferences.actif.get())
			// Pas actif
			return;

		if (!preferences.telephoneGerer.get())
			// Ne pas annoncer les appels
			return;


			Bundle b = intent.getExtras();
			if (b != null)
			{
				String stateStr = b.getString(TelephonyManager.EXTRA_STATE);
				if (stateStr == null)
					return;

				String number = b.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
				if (number == null)
					return; // Important car on est appelé deux fois (!?) avec state = RINGING, la premiere fois avec number = null

				int subId = (int) intent.getLongExtra("subscription", 1);

				int state = 0;
				if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE))
				{
					state = TelephonyManager.CALL_STATE_IDLE;
				}
				else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
				{
					state = TelephonyManager.CALL_STATE_OFFHOOK;
				}
				else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING))
				{
					state = TelephonyManager.CALL_STATE_RINGING;
				}

				if (lastState == state)
					//No change, debounce extras
					return;

				if (state == TelephonyManager.CALL_STATE_RINGING)
					onIncomingCallStarted(context, number, subId);

				lastState = state;
			}
		} catch (Exception e)
		{
			r.log(Report.ERROR, "Erreur dans IncomingCallReceiver.onReceive");
			r.log(Report.ERROR, e);
		}
	}

	protected void onIncomingCallStarted(@NonNull final Context context, String number, int subId)
	{
		Report r = Report.getInstance(context);
		try
		{
			if (number == null)
				return;

			Preferences preferences = Preferences.getInstance(context);
			@Nullable String contact = ContactUtils.getContactFromNumber(context, number);

			if (contact == null)
			{
				if (preferences.telephoneAnnoncer.get() == Preferences.CONTACTS_SEULS)
				{
					// N'afficher que les sms provenant d'un contact enregistré
					return;
				}
				else
					contact = number;
			}

			String message = context.getResources().getString(R.string.phone_call_format, contact);
			TTSService.speakFromAnywhere(context, preferences.getSoundId(context), preferences.volumeDefaut.get() ? preferences.volume.get() : -1, message);
			NotificationDatabase.getInstance(context).ajoute(message);

			// Repondre a l'appel
//		if (preferences.telephoneRepondre.get() != Preferences.JAMAIS)
//		{
//			String appelant = number;
//			if (preferences.telephoneRepondre.get() == Preferences.CONTACTS_SEULS)
//				appelant = contact;
//
//			if (appelant != null)
//			{
//				SmsListener.send(context, number,
//						Preferences.getInstance(context).smsReponse.get() + "\n(Message envoyé automatiquement par l'application Compagnon de Route (c)2019 Lucien Pilloni)", subId);
//			}
//		}

			//PhoneUtils.rejectCall(context);
		}catch (Exception e)
		{
			r.log(Report.ERROR, "Erreur dans IncomingCallReceiver.onIncomingCallStarted");
			r.log(Report.ERROR, e);
		}
	}
}
