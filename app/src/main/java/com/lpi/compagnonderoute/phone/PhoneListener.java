package com.lpi.compagnonderoute.phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;

import com.lpi.compagnonderoute.ContactUtils;
import com.lpi.compagnonderoute.preferences.Preferences;
import com.lpi.compagnonderoute.R;
import com.lpi.compagnonderoute.report.Report;
import com.lpi.compagnonderoute.sms.SMSUtils;
import com.lpi.compagnonderoute.tts.TTSService;

public class PhoneListener extends BroadcastReceiver
{
	protected void onIncomingCallStarted(@NonNull final Context context, String number, long subId)
	{
		if (number == null)
			return;

		Preferences preferences = Preferences.getInstance(context);
		String contact = ContactUtils.getContactFromNumber(context, number);

		if (contact == null)
		{
			if (preferences.getLireSms() == Preferences.CONTACTS_SEULS)
			{
				// N'afficher que les sms provenant d'un contact enregistré
				return;
			}
			else
				contact = number;
		}

		String message = context.getResources().getString(R.string.phone_call_format, contact);
		TTSService.speakFromAnywhere(context, R.raw.beep, preferences.getVolumeDefaut()? preferences.getVolume():-1, message);

		// Repondre a l'appel
		if (preferences.getRepondreAppels() != Preferences.JAMAIS)
		{
			String appelant = number;
			if (preferences.getRepondreAppels() == Preferences.CONTACTS_SEULS)
				appelant = contact;

			if (appelant != null)
			{
				SMSUtils.send(context, number, Preferences.getInstance(context).getReponseSms() + "\n(Message envoyé automatiquement par l'application Compagnon de Route (c)2019 Lucien Pilloni)", 0);
			}
		}

		//PhoneUtils.rejectCall(context);
	}

	//Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
	//Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
	public void onCallStateChanged(@NonNull final Context context, int state, String number, long subId)
	{
		Preferences prefs = Preferences.getInstance(context);
		if (!prefs.getActif())
			return;

		if (prefs.getAnnoncerAppels() == Preferences.JAMAIS)
			return;

		if (lastState == state)
			//No change, debounce extras
			return;

		if ( state == TelephonyManager.CALL_STATE_RINGING)
			onIncomingCallStarted(context, number, subId);

		lastState = state;
	}

	private static int lastState = TelephonyManager.CALL_STATE_IDLE;
	/***
	 * Reception de l'evenement de changement d'etat du telephone
	 * @param context
	 * @param intent
	 */
	@Override
	public void onReceive(@NonNull Context context, Intent intent)
	{
		if (! TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(intent.getAction()))
			return;
		Preferences preferences = Preferences.getInstance(context);
		if (!preferences.getActif())
			// Pas actif
			return;

		if (preferences.getAnnoncerAppels() == Preferences.JAMAIS)
			// Ne pas annoncer les appels
			return;

		try
		{
			Bundle b = intent.getExtras();
			if ( b!=null)
			{
				String stateStr = b.getString(TelephonyManager.EXTRA_STATE);
				if ( stateStr==null)
					return;

				String number = b.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
				if (number==null)
					return; // Important car on est appelé deux fois (!?) avec state = RINGING, la premiere fois avec number = null

				long subId = intent.getLongExtra("subscription", 1);

				int state = 0;
				if(stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE))
				{
					state = TelephonyManager.CALL_STATE_IDLE;
				}
				else if(stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
				{
					state = TelephonyManager.CALL_STATE_OFFHOOK;
				}
				else if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING))
				{
					state = TelephonyManager.CALL_STATE_RINGING;
				}

				onCallStateChanged(context, state, number, subId);
			}
		} catch (Exception e)
		{
			Report r = Report.getInstance(context);
			r.log(Report.ERROR, "Erreur dans IncomingCallReceiver.onReceive");
			r.log(Report.ERROR, e);
		}
	}
}
