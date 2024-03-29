package com.lpi.compagnonderoute.preferences;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import com.lpi.compagnonderoute.R;
import com.lpi.compagnonderoute.database.DatabaseHelper;
import com.lpi.compagnonderoute.tts.TTSService;

public class Preferences
{
	public static final int JAMAIS = 0;
	public static final int TOUJOURS = 1;
	public static final int CONTACTS_SEULS = 2;
	public static final int DELAI_ANNONCE_HEURE_QUART = 3;
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
	@NonNull private static final String PREF_GERER_TELEPHONE = "gererTelephone";
	@NonNull private static final String PREF_ACTIF_APRES_REBOOT = "actifApresReboot";
	@NonNull private static final String PREF_VOLUME_DEFAUT = "volumeDefaut";
	@NonNull private static final String PREF_VOLUME = "volumef";

	@NonNull private static final String PREF_MESSAGE_WHATSAPPACTIF = "messageWhatsAppActif";


	private static final String PREF_SON_NOTIFICATION = "sonNotification";
	private static final String PREF_AUTRES_APPLIS = "autresApplications";
	private static final String PREF_NOTIFICATION_TITRE = "notificationTitre ";
	private static final String PREF_NOTIFICATION_CONTENU = "notificationContenu ";
	private static final String PREF_NOTIFICATION_NOM_APPLI = "notificationNomAppli";

	private static Preferences _instance;
	public final PreferenceBoolean actif;
	public final PreferenceBoolean actifApresReboot;
	public final PreferenceInt horlogeDelai;
	public final PreferenceInt telephoneRepondre;
	public final PreferenceString telephoneReponse;
	public final PreferenceBoolean horlogeAnnoncer;
	// SMS
	public final PreferenceInt smsAnnoncer;
	public final PreferenceInt smsRepondre;
	public final PreferenceString smsReponse;
	public final PreferenceBoolean smsGerer;
	public final PreferenceBoolean smsLireExpediteur;
	public final PreferenceBoolean smsLireContenu;

	// Appels telephoniques
	public final PreferenceBoolean telephoneGerer;
	public final PreferenceInt telephoneAnnoncer;
	// EMails
	public final PreferenceBoolean eMailsGerer;
	public final PreferenceBoolean eMailsAnnonceExpediteur;
	public final PreferenceBoolean eMailsAnnonceSujet;

	// Autres applications
	public final PreferenceBoolean autresApplisActif;

	public static final int DELAI_ANNONCE_HEURE_HEURES = 1;
	public static final int DELAI_ANNONCE_HEURE_DEMI = 2;
	public final PreferenceBoolean messageWhatsAppActif;

	// Audio
	public PreferenceInt sonNotification;
	public PreferenceFloat volume;
	public PreferenceBoolean volumeDefaut;

	private final SQLiteDatabase database;

	/***
	 * Constructeur privé du singleton Preferences, on doit passer par getInstance pour obtenir une
	 * instance
	 * @param context
	 */
	private Preferences(@NonNull final Context context)
	{
		database = getDatabase(context);

		// Activation
		actif = new PreferenceBoolean(database, PREF_ACTIF, false);
		actifApresReboot = new PreferenceBoolean(database, PREF_ACTIF_APRES_REBOOT, false);

		// Horloge
		horlogeAnnoncer = new PreferenceBoolean(database, PREF_ANNONCE_HEURE, false);
		horlogeDelai = new PreferenceInt(database, PREF_DELAI_ANNONCE_HEURE, DELAI_ANNONCE_HEURE_QUART);

		// SMS
		smsGerer = new PreferenceBoolean(database, PREF_GERER_SMS, false);
		smsAnnoncer = new PreferenceInt(database, PREF_LIRE_SMS, TOUJOURS);
		smsLireContenu = new PreferenceBoolean(database, PREF_LIRE_CONTENU_SMS, false);
		smsLireExpediteur = new PreferenceBoolean(database, PREF_LIRE_EXPEDITEUR_SMS, true);
		smsRepondre = new PreferenceInt(database, PREF_REPONDRE_SMS, JAMAIS);
		smsReponse = new PreferenceString(database, PREF_REPONSE_SMS, context.getString(R.string.sms_answer));

		// Telephone
		telephoneGerer = new PreferenceBoolean(database, PREF_GERER_TELEPHONE, false);
		telephoneAnnoncer = new PreferenceInt(database, PREF_ANNONCER_APPELS, JAMAIS);
		telephoneRepondre = new PreferenceInt(database, PREF_REPONDRE_APPELS, JAMAIS);
		telephoneReponse = new PreferenceString(database, PREF_REPONSE_APPELS, context.getString(R.string.call_answer));

		//Emails
		eMailsGerer = new PreferenceBoolean(database, PREF_GERER_MAILS, false);
		eMailsAnnonceExpediteur = new PreferenceBoolean(database, PREF_EMAIL_ANNONCE_EXPEDITEUR, true);
		eMailsAnnonceSujet = new PreferenceBoolean(database, PREF_EMAIL_ANNONCE_SUJET, true);


		// Configuration audio
		volumeDefaut = new PreferenceBoolean(database, PREF_VOLUME_DEFAUT, true);
		volume = new PreferenceFloat(database, PREF_VOLUME, TTSService.VOLUME_MAX);
		sonNotification = new PreferenceInt(database, PREF_SON_NOTIFICATION, 0);

		// Autres applications
		autresApplisActif = new PreferenceBoolean(database, PREF_AUTRES_APPLIS, false);
		messageWhatsAppActif = new PreferenceBoolean(database, PREF_MESSAGE_WHATSAPPACTIF, false);
	}

	private SQLiteDatabase getDatabase(final Context context)
	{
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		return dbHelper.getWritableDatabase();
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

	public int getSoundId(final Context context)
	{
		final int[] ids = PreferencesActivity.getIntArray(context, R.array.id_sons_notification);
		if (ids == null)
			return R.raw.beep;
		int i = sonNotification.get();
		if (i < 0 || i >= ids.length)
			return R.raw.beep;

		return ids[i];
	}

	public boolean getNotificationTitre(final String packageName)
	{
		PreferenceBoolean pref = new PreferenceBoolean(database, PREF_NOTIFICATION_TITRE + packageName, false);
		return pref.get();
	}

	public boolean getNotificationContenu(final String packageName)
	{
		PreferenceBoolean pref = new PreferenceBoolean(database, PREF_NOTIFICATION_CONTENU + packageName, false);
		return pref.get();
	}

	public boolean getNotificationNomAppli(final String packageName)
	{
		PreferenceBoolean pref = new PreferenceBoolean(database, PREF_NOTIFICATION_NOM_APPLI + packageName, false);

		return pref.get();
	}

	public void setNotificationTitre(final String packageName, boolean b)
	{
		PreferenceBoolean pref = new PreferenceBoolean(database, PREF_NOTIFICATION_TITRE + packageName, false);
		pref.set(b);
	}

	public void setNotificationContenu(final String packageName, boolean b)
	{
		PreferenceBoolean pref = new PreferenceBoolean(database, PREF_NOTIFICATION_CONTENU + packageName, false);
		pref.set(b);
	}

	public void setNotificationNomAppli(final String packageName, boolean b)
	{
		PreferenceBoolean pref = new PreferenceBoolean(database, PREF_NOTIFICATION_NOM_APPLI + packageName, false);
		pref.set(b);
	}
}
