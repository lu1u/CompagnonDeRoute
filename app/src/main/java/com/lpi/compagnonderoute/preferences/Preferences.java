package com.lpi.compagnonderoute.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.lpi.compagnonderoute.R;
import com.lpi.compagnonderoute.tts.TTSService;

public class Preferences
{
	public static final int JAMAIS = 0;
	public static final int TOUJOURS = 1;
	public static final int CONTACTS_SEULS = 2;
	public static final int DELAI_ANNONCE_HEURE_QUART = 3;
	@NonNull private static final String PREFERENCES = Preferences.class.getName();
	@NonNull private static final String PREF_ACTIF = "actif";
	// Horloge
	@NonNull private static final String PREF_ANNONCE_HEURE = "annonceHeure";
	@NonNull private static final String PREF_DELAI_ANNONCE_HEURE = "delaiAnnonceHeure";
	// SMS
	@NonNull private static final String PREF_GERER_SMS = "gererSMS";
	@NonNull private static final String PREF_LIRE_SMS = "lireMessages";
	@NonNull private static final String PREF_REPONDRE_SMS = "repondreSms";
	@NonNull private static final String PREF_REPONSE_SMS = "reponseSms";
	@NonNull private static final String PREF_LIRE_CONTENU_SMS = "lireContenuSMS";
	@NonNull private static final String PREF_LIRE_EXPEDITEUR_SMS = "lireExpediteurSMS";
	// Appels telephoniques
	@NonNull private static final String PREF_ANNONCER_APPELS = "annoncerAppels";
	@NonNull private static final String PREF_REPONDRE_APPELS = "repondreAppels";
	@NonNull private static final String PREF_REPONSE_APPELS = "reponseAppels";
	// EMails
	@NonNull private static final String PREF_GERER_MAILS = "gererMails";
	@NonNull private static final String PREF_EMAIL_ANNONCE_EXPEDITEUR = "eMailsAnnonceExpediteur";
	@NonNull private static final String PREF_EMAIL_ANNONCE_SUJET = "eMailsAnnonceSujet";
	// Messages WhatsApp
	@NonNull private static final String PREF_GERER_TELEPHONE = "gererTelephone";
	@NonNull private static final String PREF_GERER_WHATSAPP = "gererWhatsapp";
	@NonNull private static final String PREF_GERER_APPELWHATSAPP = "gererAppelWhatsapp";
	@NonNull private static final String PREF_WHATSAPP_LIRE_EXPEDITEUR = "whatsAppLireExpediteur";
	@NonNull private static final String PREF_ACTIF_APRES_REBOOT = "actifApresReboot";
	@NonNull private static final String PREF_VOLUME_DEFAUT = "volumeDefaut";
	@NonNull private static final String PREF_VOLUME = "volumef";
	@NonNull private static final String PREF_FORCE_SORTIE = "forceSortie";
	private static Preferences _instance;
	@NonNull final SharedPreferences.Editor editor;
	public PreferenceBoolean actif;
	public PreferenceBoolean actifApresReboot;
	public PreferenceBoolean volumeDefaut;
	public PreferenceInt delaiAnnonceHeure;
	public PreferenceInt telephoneRepondre;
	public PreferenceFloat volume;
	public PreferenceString telephoneReponse;
	public PreferenceInt forceSortie;
	public PreferenceBoolean annonceHeure;
	public PreferenceBoolean gererAppelsWhatsApp;
	// SMS
	public PreferenceInt lireSMS;
	public PreferenceInt lireContenuSms;
	public PreferenceInt repondreSms;
	public PreferenceString reponseSms;
	public PreferenceBoolean gererSMS;
	public PreferenceBoolean lireExpediteurSMS;
	// Appels telephoniques
	public PreferenceBoolean telephoneGerer;
	public PreferenceInt telephoneAnnoncer;
	// EMails
	public PreferenceBoolean gererMails;
	public PreferenceBoolean eMailsAnnonceExpediteur;
	public PreferenceBoolean eMailsAnnonceSujet;
	// Messages WhatsApp
	public PreferenceBoolean messageWhatsAppActif;

	public static final int DELAI_ANNONCE_HEURE_HEURES = 1;
	public static final int DELAI_ANNONCE_HEURE_DEMI = 2;
	public PreferenceBoolean MessageWhatsAppLireExpediteur;

	/***
	 * Constructeur privé du singleton Preferences, on doit passer par getInstance pour obtenir une
	 * instance
	 * @param context
	 */
	private Preferences(@NonNull final Context context)
	{
		final SharedPreferences settings = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		editor = settings.edit();

		actif = new PreferenceBoolean(settings, PREF_ACTIF, false);
		actifApresReboot = new PreferenceBoolean(settings, PREF_ACTIF_APRES_REBOOT, false);
		annonceHeure = new PreferenceBoolean(settings, PREF_ANNONCE_HEURE, false);
		delaiAnnonceHeure = new PreferenceInt(settings, PREF_DELAI_ANNONCE_HEURE, DELAI_ANNONCE_HEURE_QUART);
		volumeDefaut = new PreferenceBoolean(settings, PREF_VOLUME_DEFAUT, true);
		volume = new PreferenceFloat(settings, PREF_VOLUME, TTSService.VOLUME_MAX);
		reponseSms = new PreferenceString(settings, PREF_REPONSE_SMS, context.getString(R.string.sms_answer));
		forceSortie = delaiAnnonceHeure = new PreferenceInt(settings, PREF_FORCE_SORTIE, TTSService.SORTIE_DEFAUT);

		// SMS
		gererSMS = new PreferenceBoolean(settings, PREF_GERER_SMS, false);
		lireSMS = new PreferenceInt(settings, PREF_LIRE_SMS, TOUJOURS);
		lireContenuSms = new PreferenceInt(settings, PREF_LIRE_CONTENU_SMS, CONTACTS_SEULS);
		repondreSms = new PreferenceInt(settings, PREF_REPONDRE_SMS, JAMAIS);
		lireExpediteurSMS = new PreferenceBoolean(settings, PREF_LIRE_EXPEDITEUR_SMS, false);

		// Telephone
		telephoneGerer = new PreferenceBoolean(settings, PREF_GERER_TELEPHONE, false);
		telephoneAnnoncer = new PreferenceInt(settings, PREF_ANNONCER_APPELS, JAMAIS);
		telephoneRepondre = new PreferenceInt(settings, PREF_REPONDRE_APPELS, JAMAIS);
		telephoneReponse = new PreferenceString(settings, PREF_REPONSE_APPELS, context.getString(R.string.call_answer));

		//Emails
		gererMails = new PreferenceBoolean(settings, PREF_GERER_MAILS, false);
		eMailsAnnonceExpediteur = new PreferenceBoolean(settings, PREF_EMAIL_ANNONCE_EXPEDITEUR, true);
		eMailsAnnonceSujet = new PreferenceBoolean(settings, PREF_EMAIL_ANNONCE_SUJET, true);

		// Messages WhatsApp
		messageWhatsAppActif = new PreferenceBoolean(settings, PREF_GERER_WHATSAPP, false);
		MessageWhatsAppLireExpediteur = new PreferenceBoolean(settings, PREF_WHATSAPP_LIRE_EXPEDITEUR, false);

		gererAppelsWhatsApp = new PreferenceBoolean(settings, PREF_GERER_APPELWHATSAPP, false);
	}

	/***
	 * Obtenir l'instance (unique) de Preferences
	 * @param context
	 * @return
	 */
	public static synchronized Preferences getInstance(@NonNull final Context context)
	{
		if (_instance == null)
			_instance = new Preferences(context);

		return _instance;
	}
}
