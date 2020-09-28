package com.lpi.compagnonderoute.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.lpi.compagnonderoute.R;
import com.lpi.compagnonderoute.tts.TTSService;

public class Preferences
{
	private static final String PREFERENCES = Preferences.class.getName();
	private static final String PREF_ACTIF = "actif";
	private static final String PREF_LIRE_SMS = "lireSms";
	private static final String PREF_REPONDRE_SMS = "repondreSms";
	private static final String PREF_DELAI_ANNONCE_HEURE = "delaiAnnonceHeure";
	private static final String PREF_ANNONCER_APPELS = "annoncerAppels";
	private static final String PREF_REPONDRE_APPELS = "repondreAppels";
	private static final String PREF_REPONSE_SMS = "reponseSms";
	private static final String PREF_REPONSE_APPELS = "reponseAppels";
	private static final String PREF_ACTIF_APRES_REBOOT = "actifApresReboot";
	private static final String PREF_VOLUME_DEFAUT = "volumeDefaut";
	//private static final String PREF_CANAL_SORTIE = "canalSortie";
	private static final String PREF_VOLUME = "volume";
	private static final String PREF_LIRE_CONTENU_SMS = "lireContenuSMS";

	@NonNull	final SharedPreferences settings;
	@NonNull 	final SharedPreferences.Editor editor;

	private boolean _actif;
	private boolean _actifApresReboot;
	private boolean _volumeDefaut;
	private int _lireSms;
	private boolean _lireContenuSms;
	private int _delaiAnnonceHeure;
	private int _repondreSms;
	private int _annoncerAppels;
	private int _repondreAppels;
	private int _volume;
	private String _reponseSms;
	private String _reponseAppels;
	//private int _canalSortie ;

	private static Preferences _instance;

	public static final int JAMAIS =   0;
	public static final int TOUJOURS =   1;
	public static final int CONTACTS_SEULS =   2;

	public static final int DELAI_ANNONCE_HEURE_JAMAIS = 0;
	public static final int DELAI_ANNONCE_HEURE_HEURES = 1;
	public static final int DELAI_ANNONCE_HEURE_DEMI = 2;
	public static final int DELAI_ANNONCE_HEURE_QUART = 3 ;

	/***
	 * Obtenir l'instance (unique) de Preferences
	 * @param context
	 * @return
	 */
	public static synchronized Preferences getInstance(@NonNull final Context context)
	{
		if ( _instance == null)
			_instance = new Preferences(context);

		return _instance;

	}

	/***
	 * Constructeur privé du singleton Preferences, on doit passer par getInstance pour obtenir une
	 * instance
	 * @param context
	 */
	private Preferences(@NonNull final Context context)
	{
		settings = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		editor = settings.edit();

		_actif = settings.getBoolean(PREF_ACTIF, false);
		_actifApresReboot = settings.getBoolean(PREF_ACTIF_APRES_REBOOT, false);
		_delaiAnnonceHeure = settings.getInt(PREF_DELAI_ANNONCE_HEURE, DELAI_ANNONCE_HEURE_QUART );
		_lireSms = settings.getInt(PREF_LIRE_SMS, CONTACTS_SEULS);
		_lireContenuSms = settings.getBoolean(PREF_LIRE_CONTENU_SMS, true) ;
		_repondreSms = settings.getInt(PREF_REPONDRE_SMS, CONTACTS_SEULS);
		_volumeDefaut = settings.getBoolean(PREF_VOLUME_DEFAUT, true);
		_volume = settings.getInt(PREF_VOLUME, TTSService.getMaxVolume());
		//_canalSortie = settings.getInt(PREF_CANAL_SORTIE, AudioManager.STREAM_SYSTEM);
		_annoncerAppels = settings.getInt(PREF_ANNONCER_APPELS, CONTACTS_SEULS);
		_repondreAppels = settings.getInt(PREF_REPONDRE_APPELS, CONTACTS_SEULS);
		_reponseSms = settings.getString(PREF_REPONSE_SMS, context.getString(R.string.reponse_sms));
		_reponseAppels = settings.getString(PREF_REPONSE_APPELS, context.getString(R.string.reponse_appel));
	}


	public int getLireSms()
	{
		return _lireSms;
	}
	public synchronized void setLireSms(int v)
	{
		_lireSms = v;
		editor.putInt(PREF_LIRE_SMS, v);
		editor.apply();
	}
	public int getRepondreSms()
	{
		return _repondreSms;
	}
	public synchronized void setRepondreSms(int v)
	{
		_repondreSms = v;
		editor.putInt(PREF_REPONDRE_SMS, v);
		editor.apply();
	}

	public int getAnnoncerAppels()
	{
		return _annoncerAppels;
	}
	public synchronized void setAnnoncerAppels(int v)
	{
		_annoncerAppels = v;
		editor.putInt(PREF_ANNONCER_APPELS, v);
		editor.apply();
	}

	public int getRepondreAppels()
	{
		return _repondreAppels;
	}
	public synchronized void setRepondreAppels(int v)
	{
		_repondreAppels = v;
		editor.putInt(PREF_REPONDRE_APPELS, v);
		editor.apply();
	}

	public int getDelaiAnnonceHeure()
	{
		return _delaiAnnonceHeure;
	}
	public synchronized void setDelaiAnnonceHeure(int v)
	{
		_delaiAnnonceHeure = v;
		editor.putInt(PREF_DELAI_ANNONCE_HEURE, v);
		editor.apply();
	}


	public boolean getActif()
	{
		return _actif;
	}
	public synchronized void setActif(boolean v)
	{
		_actif = v;
		editor.putBoolean(PREF_ACTIF, v);
		editor.apply();
	}


	public boolean getActifApresReboot()
	{
		return _actifApresReboot;
	}
	public synchronized void setActifApresReboot(boolean v)
	{
		_actifApresReboot = v;
		editor.putBoolean(PREF_ACTIF_APRES_REBOOT, v);
		editor.apply();
	}



	public boolean getVolumeDefaut()
	{
		return _volumeDefaut;
	}
	public synchronized void setVolumeDefaut(boolean v)
	{
		_volumeDefaut= v;
		editor.putBoolean(PREF_VOLUME_DEFAUT, v);
		editor.apply();
	}

	public @NonNull String getReponseSms() { return _reponseSms; }
	public void setReponseSMS(final String s)
	{
		_reponseSms = s;
		editor.putString( PREF_REPONSE_SMS, s);
		editor.apply();
	}

	public @NonNull String getReponseAppels() { return _reponseAppels; }
	public void setReponseAppels(final String s)
	{
		_reponseAppels = s;
		editor.putString( PREF_REPONSE_APPELS, s);
		editor.apply();
	}


//	public int getCanalSortie(){ return _canalSortie;}
//	public void setCanalSortie(final int v)
//	{
//		_canalSortie = v;
//		editor.putInt(PREF_CANAL_SORTIE, v);
//		editor.apply();
//	}


	public int getVolume(){ return _volume;}
	public void setVolume(final int v)
	{
		_volume = v;
		editor.putInt(PREF_VOLUME, v);
		editor.apply();
	}

	public boolean getLireContenuSms()
	{
		return _lireContenuSms ;
	}
	public synchronized void setLireContenuSms(boolean v)
	{
		_lireContenuSms= v;
		editor.putBoolean(PREF_LIRE_CONTENU_SMS, v);
		editor.apply();
	}
}
