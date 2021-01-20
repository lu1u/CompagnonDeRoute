package com.lpi.compagnonderoute.preferences;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.lpi.compagnonderoute.R;
import com.lpi.compagnonderoute.tts.TTSService;

public class Preferences
{
	// Base de donnees
	public static final String TABLE_PREFERENCES = "PREFERENCES";
	public static final String COLONNE_NOM = "NOM";
	public static final String COLONNE_VALEUR = "VALEUR";

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
	// Messages WhatsApp
	@NonNull private static final String PREF_GERER_TELEPHONE = "gererTelephone";
	@NonNull private static final String PREF_GERER_WHATSAPP = "gererWhatsapp";
	@NonNull private static final String PREF_GERER_APPELWHATSAPP = "gererAppelWhatsapp";
	@NonNull private static final String PREF_WHATSAPP_LIRE_EXPEDITEUR = "whatsAppLireExpediteur";
	@NonNull private static final String PREF_ACTIF_APRES_REBOOT = "actifApresReboot";
	@NonNull private static final String PREF_VOLUME_DEFAUT = "volumeDefaut";
	@NonNull private static final String PREF_VOLUME = "volumef";
	@NonNull private static final String PREF_FORCE_SORTIE = "forceSortie";
	private static final String PREF_SON_NOTIFICATION = "sonNotification";
	private static final String PREF_AUTRES_APPLIS = "autresApplications";
	private static final String PREF_NOTIFICATION_TITRE = "notificationTitre ";
	private static final String PREF_NOTIFICATION_CONTENU = "notificationContenu ";

	private static Preferences _instance;
	public PreferenceBoolean actif;
	public PreferenceBoolean actifApresReboot;
	public PreferenceInt horlogeDelai;
	public PreferenceInt telephoneRepondre;
	public PreferenceString telephoneReponse;
	public PreferenceInt forceSortie;
	public PreferenceBoolean horlogeAnnoncer;
	public PreferenceBoolean appelsWhatsAppGerer;
	// SMS
	public PreferenceInt smsAnnoncer;
	public PreferenceInt smsRepondre;
	public PreferenceString smsReponse;
	public PreferenceBoolean smsGerer;
	public PreferenceBoolean smsLireExpediteur;
	public PreferenceBoolean smsLireContenu;

	// Appels telephoniques
	public PreferenceBoolean telephoneGerer;
	public PreferenceInt telephoneAnnoncer;
	// EMails
	public PreferenceBoolean eMailsGerer;
	public PreferenceBoolean eMailsAnnonceExpediteur;
	public PreferenceBoolean eMailsAnnonceSujet;
	// Messages WhatsApp
	public PreferenceBoolean messageWhatsAppActif;

	// Autres applications
	public PreferenceBoolean autresApplisActif;

	public static final int DELAI_ANNONCE_HEURE_HEURES = 1;
	public static final int DELAI_ANNONCE_HEURE_DEMI = 2;
	public PreferenceBoolean messageWhatsAppLireExpediteur;

	// Audio
	public PreferenceInt sonNotification;
	public PreferenceFloat volume;
	public PreferenceBoolean volumeDefaut;

	private SQLiteDatabase database;

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

		// Messages WhatsApp
		messageWhatsAppActif = new PreferenceBoolean(database, PREF_GERER_WHATSAPP, false);
		messageWhatsAppLireExpediteur = new PreferenceBoolean(database, PREF_WHATSAPP_LIRE_EXPEDITEUR, false);

		// Appels WhatsApp
		appelsWhatsAppGerer = new PreferenceBoolean(database, PREF_GERER_APPELWHATSAPP, false);

		// Configuration audio
		volumeDefaut = new PreferenceBoolean(database, PREF_VOLUME_DEFAUT, true);
		volume = new PreferenceFloat(database, PREF_VOLUME, TTSService.VOLUME_MAX);
		forceSortie = new PreferenceInt(database, PREF_FORCE_SORTIE, TTSService.SORTIE_DEFAUT);
		sonNotification = new PreferenceInt(database, PREF_SON_NOTIFICATION, 0);

		// Autres applications
		autresApplisActif = new PreferenceBoolean(database, PREF_AUTRES_APPLIS, false);
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

	private class DatabaseHelper extends SQLiteOpenHelper
	{
		public static final int DATABASE_VERSION = 1;
		public static final String DATABASE_NAME = "preferences.db";

		public DatabaseHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			try
			{
				Log.w(DatabaseHelper.class.getName(), "Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
				db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREFERENCES);
				onCreate(db);
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}

		@Override public void onCreate(final SQLiteDatabase database)
		{
			try
			{
				String DATABASE_PREFERENCES_CREATE = "create table IF NOT EXISTS "
						+ TABLE_PREFERENCES + "("
						+ COLONNE_NOM + " TEXT NOT NULL, "
						+ COLONNE_VALEUR + " text"
						+ ");";
				database.execSQL(DATABASE_PREFERENCES_CREATE);
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
}
