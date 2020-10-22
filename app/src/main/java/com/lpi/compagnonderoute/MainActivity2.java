package com.lpi.compagnonderoute;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.lpi.compagnonderoute.notificationListener.NotificationListenerManager;
import com.lpi.compagnonderoute.parametres.ParametresAppel;
import com.lpi.compagnonderoute.parametres.ParametresAppelWhatsApp;
import com.lpi.compagnonderoute.parametres.ParametresEMails;
import com.lpi.compagnonderoute.parametres.ParametresHorloge;
import com.lpi.compagnonderoute.parametres.ParametresMessagesWhatsApp;
import com.lpi.compagnonderoute.parametres.ParametresSMS;
import com.lpi.compagnonderoute.plannificateur.Plannificateur;
import com.lpi.compagnonderoute.preferences.Preferences;
import com.lpi.compagnonderoute.preferences.PreferencesActivity;
import com.lpi.compagnonderoute.report.Report;
import com.lpi.compagnonderoute.report.ReportActivity;
import com.lpi.compagnonderoute.tts.TTSService;

public class MainActivity2 extends AppCompatActivity
{
	private static final int PERMISSION_REQUEST_ACTIVER_SMS = 0;
	private static final int PERMISSION_REQUEST_ACTIVER_TELEPHONE = 1;
	// Intercepte les messages du plannificateur pour maj l'interface utilisateur
	@NonNull final IntentFilter _intentFilter = new IntentFilter(Plannificateur.ACTION_MESSAGE_UI);
	private Switch _swSMS, _swHorloge, _swEMails, _swMessageWhatsApp, _swAppelTelephone, _swAppelWhatsApp;
	private ImageButton _bActiver, _bDesactiver;
	private ImageButton _bSettingsHorloge, _bSettingsSMS, _bSettingsEMails, _bSettingsMessageWhatsApp, _bSettingsTelephone, _bSettingsAppelWhatsApp;
	private TextView _tvMessage;
	// Broadcast receiver pour recevoir les message de mise a jour envoyes par les services
	@NonNull final BroadcastReceiver _receiverMajUI = new BroadcastReceiver()
	{
		@Override
		public void onReceive(final Context context, final Intent intent)
		{
			String action = intent.getAction();
			if (Plannificateur.ACTION_MESSAGE_UI.equals(action))
			{
				if (_tvMessage != null)
				{
					String message = intent.getStringExtra(Plannificateur.EXTRA_MESSAGE_UI);
					_tvMessage.setText(message);
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);
		initControles();
		initListeners();
	}

	/***
	 * Initialisation des objets qui vont nous permettre de controler l'interface utilisateur
	 */
	private void initControles()
	{
		_bActiver = findViewById(R.id.imageButtonActiver);
		_bDesactiver = findViewById(R.id.imageButtonDesactiver);

		_swSMS = findViewById(R.id.switchSMS);
		_swHorloge = findViewById(R.id.switchHorloge);
		_swEMails = findViewById(R.id.switchEMails);
		_swMessageWhatsApp = findViewById(R.id.switchWhatsApp);
		_swAppelTelephone = findViewById(R.id.switchTelephone);
		_swAppelWhatsApp = findViewById(R.id.switchAppelWhatsApp);

		_bSettingsHorloge = findViewById(R.id.imageButtonSettingsHorloge);
		_bSettingsSMS = findViewById(R.id.imageButtonSettingsSMS);
		_bSettingsEMails = findViewById(R.id.imageButtonSettingsEMails);
		_bSettingsMessageWhatsApp = findViewById(R.id.imageButtonSettingsWhatsAppMessages);
		_bSettingsTelephone = findViewById(R.id.imageButtonSettingsTelephone);
		_bSettingsAppelWhatsApp = findViewById(R.id.imageButtonSettingsWhatsAppAppel);

		_tvMessage = findViewById(R.id.textViewMessage);
	}

	/***
	 * Initialisation de la gestion de l'interface utilisateur
	 */
	private void initListeners()
	{
		final Report r = Report.getInstance(this);

		final Preferences preferences = Preferences.getInstance(this);

		if (_bActiver != null && _bDesactiver != null)
		{
			_bActiver.setOnClickListener(new View.OnClickListener()
			{
				@Override public void onClick(final View view)
				{
					r.log(Report.HISTORIQUE, "Activé");
					preferences.actif.set(true);
					_bActiver.setAnimation(AnimationUtils.loadAnimation(MainActivity2.this, R.anim.pop_exit));
					_bActiver.setVisibility(View.GONE);
					_bDesactiver.setAnimation(AnimationUtils.loadAnimation(MainActivity2.this, R.anim.pop_enter));
					_bDesactiver.setVisibility(View.VISIBLE);
					Toast.makeText(MainActivity2.this, MainActivity2.this.getResources().getString(R.string.enabled), Toast.LENGTH_SHORT).show();
					TTSService.speakFromAnywhere(MainActivity2.this, R.raw.beep,
							preferences.volumeDefaut.get() ? preferences.volume.get() : -1,
							MainActivity2.this.getResources().getString(R.string.enabled));
					Plannificateur.getInstance(MainActivity2.this).plannifieProchaineNotification(MainActivity2.this);
				}
			});

			_bDesactiver.setOnClickListener(new View.OnClickListener()
			{
				@Override public void onClick(final View view)
				{
					r.log(Report.HISTORIQUE, "Désactivé");

					preferences.actif.set(false);
					_bActiver.setAnimation(AnimationUtils.loadAnimation(MainActivity2.this, R.anim.pop_enter));
					_bActiver.setVisibility(View.VISIBLE);
					_bDesactiver.setAnimation(AnimationUtils.loadAnimation(MainActivity2.this, R.anim.pop_exit));
					_bDesactiver.setVisibility(View.GONE);
					Toast.makeText(MainActivity2.this, MainActivity2.this.getResources().getString(R.string.disabled), Toast.LENGTH_SHORT).show();
					TTSService.speakFromAnywhere(MainActivity2.this, R.raw.beep, preferences.volumeDefaut.get() ? preferences.volume.get() : -1, MainActivity2.this.getResources().getString(R.string.disabled));
					Plannificateur.getInstance(MainActivity2.this).arrete(MainActivity2.this);
				}
			});
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		// Annoncer l'heure
		////////////////////////////////////////////////////////////////////////////////////////////
		if (_swHorloge != null)
		{
			_swHorloge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final CompoundButton compoundButton, final boolean checked)
				{
					if (compoundButton.isPressed())
					{
						preferences.annonceHeure.set(checked);
						Carillon.changeDelai(MainActivity2.this);
					}
				}
			});
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		// Intercepter SMS
		////////////////////////////////////////////////////////////////////////////////////////////
		if (_swSMS != null)
		{
			_swSMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final CompoundButton compoundButton, final boolean checked)
				{
					if (compoundButton.isPressed())
					{
						if (checked)
							activerSMS();
						else
							preferences.gererSMS.set(false);
					}
				}
			});
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		// Intercepter EMails
		////////////////////////////////////////////////////////////////////////////////////////////
		if (_swEMails != null)
		{
			_swEMails.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final CompoundButton compoundButton, final boolean checked)
				{
					if (compoundButton.isPressed())
					{
						if (checked)
							activerEMails();
						else
							preferences.gererMails.set(false);
					}
				}
			});
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		// Intercepter Messages WhatsApp
		////////////////////////////////////////////////////////////////////////////////////////////
		if (_swMessageWhatsApp != null)
		{
			_swMessageWhatsApp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final CompoundButton compoundButton, final boolean checked)
				{
					if (compoundButton.isPressed())
					{
						if (checked)
							activerMessageWhatsApp();
						else
							preferences.messageWhatsAppActif.set(false);
					}
				}
			});
		}
		////////////////////////////////////////////////////////////////////////////////////////////
		// Intercepter Appels WhatsApp
		////////////////////////////////////////////////////////////////////////////////////////////
		if (_swAppelWhatsApp != null)
		{
			_swAppelWhatsApp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final CompoundButton compoundButton, final boolean checked)
				{
					if (compoundButton.isPressed())
					{
						if (checked)
							activerAppelsWhatsApp();
						else
							preferences.gererAppelsWhatsApp.set(false);
					}
				}
			});
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		// Intercepter Appels
		////////////////////////////////////////////////////////////////////////////////////////////
		if (_swAppelTelephone != null)
		{
			_swAppelTelephone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final CompoundButton compoundButton, final boolean checked)
				{
					if (compoundButton.isPressed())
					{
						if (checked)
							activerTelephone();
						else
							preferences.telephoneGerer.set(false);
					}
				}
			});
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		// Bouton Parametres Horloge
		////////////////////////////////////////////////////////////////////////////////////////////
		if (_bSettingsHorloge != null)
		{
			_bSettingsHorloge.setOnClickListener(new View.OnClickListener()
			{
				@Override public void onClick(final View view)
				{
					ParametresHorloge.start(MainActivity2.this);
				}
			});
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		// Bouton Parametres SMS
		////////////////////////////////////////////////////////////////////////////////////////////
		if (_bSettingsSMS != null)
		{
			_bSettingsSMS.setOnClickListener(new View.OnClickListener()
			{
				@Override public void onClick(final View view)
				{
					ParametresSMS.start(MainActivity2.this);
				}
			});
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		// Bouton Parametres EMails
		////////////////////////////////////////////////////////////////////////////////////////////
		if (_bSettingsEMails != null)
		{
			_bSettingsEMails.setOnClickListener(new View.OnClickListener()
			{
				@Override public void onClick(final View view)
				{
					ParametresEMails.start(MainActivity2.this);
				}
			});
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		// Bouton Parametres Messages WhatsApp
		////////////////////////////////////////////////////////////////////////////////////////////
		if (_bSettingsMessageWhatsApp != null)
		{
			_bSettingsMessageWhatsApp.setOnClickListener(new View.OnClickListener()
			{
				@Override public void onClick(final View view)
				{
					ParametresMessagesWhatsApp.start(MainActivity2.this);
				}
			});
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		// Bouton Parametres SMS
		////////////////////////////////////////////////////////////////////////////////////////////
		if (_bSettingsMessageWhatsApp != null)
		{
			_bSettingsMessageWhatsApp.setOnClickListener(new View.OnClickListener()
			{
				@Override public void onClick(final View view)
				{
					ParametresMessagesWhatsApp.start(MainActivity2.this);
				}
			});
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		// Bouton Parametres Telephone
		////////////////////////////////////////////////////////////////////////////////////////////
		if (_bSettingsTelephone != null)
		{
			_bSettingsTelephone.setOnClickListener(new View.OnClickListener()
			{
				@Override public void onClick(final View view)
				{
					ParametresAppel.start(MainActivity2.this);
				}
			});
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		// Bouton Parametres Telephone
		////////////////////////////////////////////////////////////////////////////////////////////
		if (_bSettingsAppelWhatsApp != null)
		{
			_bSettingsAppelWhatsApp.setOnClickListener(new View.OnClickListener()
			{
				@Override public void onClick(final View view)
				{
					ParametresAppelWhatsApp.start(MainActivity2.this);
				}
			});
		}
	}

	/***
	 * Activer la gestion du telephone, apres verification des droits
	 */
	private void activerSMS()
	{
		final String[] permissions = {Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS,
				Manifest.permission.READ_CONTACTS};
		if (activerSiDroits(permissions, PERMISSION_REQUEST_ACTIVER_SMS, R.string.permissions_telephone))
			Preferences.getInstance(this).gererSMS.set(true);
	}

	/***
	 * Activer la gestion des mails, apres verification des droits
	 */
	private void activerEMails()
	{
		final Preferences prefs = Preferences.getInstance(this);
		NotificationListenerManager.checkNotificationServiceEnabled(this, R.string.besoin_accord_notification_listener, new NotificationListenerManager.checkNotificationServiceEnabledListener()
		{
			// Deja autorisé, on peut cocher l'option
			@Override public void onEnabled()
			{
				_swEMails.setChecked(true);
				prefs.gererMails.set(true);
			}

			// Pas autorisé et l'utilisateur ne veut pas modifier les paramètres, interdire l'option
			@Override public void onCancel()
			{
				_swEMails.setChecked(false);
				prefs.gererMails.set(false);
			}

			// Pas autorisé, l'utilisateur a été redirigé vers l'écran de parametrage
			@Override public void onSettings()
			{
				_swEMails.setChecked(false);
				prefs.gererMails.set(false);
			}
		});
	}

	/***
	 * Activer la gestion des messages WhatsApp, apres verification des droits
	 */
	private void activerMessageWhatsApp()
	{
		final Preferences prefs = Preferences.getInstance(this);
		NotificationListenerManager.checkNotificationServiceEnabled(this, R.string.besoin_accord_notification_listener, new NotificationListenerManager.checkNotificationServiceEnabledListener()
		{
			// Deja autorisé, on peut cocher l'option
			@Override public void onEnabled()
			{
				_swMessageWhatsApp.setChecked(true);
				prefs.messageWhatsAppActif.set(true);
			}

			// Pas autorisé et l'utilisateur ne veut pas modifier les paramètres, interdire l'option
			@Override public void onCancel()
			{
				_swMessageWhatsApp.setChecked(false);
				prefs.messageWhatsAppActif.set(false);
			}

			// Pas autorisé, l'utilisateur a été redirigé vers l'écran de parametrage
			@Override public void onSettings()
			{
				_swMessageWhatsApp.setChecked(false);
				prefs.messageWhatsAppActif.set(false);
			}
		});
	}

	/***
	 * Activer la gestion des messages WhatsApp, apres verification des droits
	 */
	private void activerAppelsWhatsApp()
	{
		final Preferences prefs = Preferences.getInstance(this);
		NotificationListenerManager.checkNotificationServiceEnabled(this, R.string.besoin_accord_notification_listener, new NotificationListenerManager.checkNotificationServiceEnabledListener()
		{
			// Deja autorisé, on peut cocher l'option
			@Override public void onEnabled()
			{
				_swAppelWhatsApp.setChecked(true);
				prefs.gererAppelsWhatsApp.set(true);
			}

			// Pas autorisé et l'utilisateur ne veut pas modifier les paramètres, interdire l'option
			@Override public void onCancel()
			{
				_swAppelWhatsApp.setChecked(false);
				prefs.gererAppelsWhatsApp.set(false);
			}

			// Pas autorisé, l'utilisateur a été redirigé vers l'écran de parametrage
			@Override public void onSettings()
			{
				_swAppelWhatsApp.setChecked(false);
				prefs.gererAppelsWhatsApp.set(false);
			}
		});
	}

	/***
	 * Activer la gestion des SMS, apres verification des droits
	 */
	private void activerTelephone()
	{
		final String[] permissions = {Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG};
		if (activerSiDroits(permissions, PERMISSION_REQUEST_ACTIVER_TELEPHONE, R.string.permissions_sms))
			Preferences.getInstance(this).telephoneGerer.set(true);
	}

	private boolean activerSiDroits(final String[] permissions, final int requestCode, @StringRes final int idMessage)
	{
		if (Permissions.verifiePermissions(this, permissions))
			return true;

		// Demander à l'utilisateur d'accorder les droits
		ConfirmBox.show(this, idMessage, new ConfirmBox.ConfirmBoxListener()
		{
			@Override public void onPositive()
			{
				requestPermissions(permissions, requestCode);
			}

			@Override public void onNegative()
			{

			}
		});

		return false;
	}

	@Override
	public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		for (int result : grantResults)
			if (result != PackageManager.PERMISSION_GRANTED)
				return;

		switch (requestCode)
		{
			case PERMISSION_REQUEST_ACTIVER_SMS:
				// Autorisation pour gerer les SMS
				Preferences.getInstance(this).gererSMS.set(true);
				_swSMS.setChecked(true);
				break;

			case PERMISSION_REQUEST_ACTIVER_TELEPHONE:
				// Autorisation pour gerer le telephone
				Preferences.getInstance(this).telephoneGerer.set(true);
				_swAppelTelephone.setChecked(true);
				break;
		}
	}

	@Override protected void onPause()
	{
		super.onPause();
		unregisterReceiver(_receiverMajUI);
	}

	@Override
	protected void onResume()
	{
		try
		{
			super.onResume();
			registerReceiver(_receiverMajUI, _intentFilter);
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

			if (Preferences.getInstance(this).actif.get())
				Plannificateur.getInstance(this).plannifieProchaineNotification(this);
			majUI();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/***
	 * Mise a jour de l'interface utilisateur en fonction de l'état de l'application
	 */
	private void majUI()
	{
		final Preferences preferences = Preferences.getInstance(this);

		if (preferences.actif.get())
		{
			if (_bActiver != null)
				_bActiver.setVisibility(View.GONE);
			if (_bDesactiver != null)
				_bDesactiver.setVisibility(View.VISIBLE);
		}
		else
		{
			if (_bActiver != null)
				_bActiver.setVisibility(View.VISIBLE);
			if (_bDesactiver != null)
				_bDesactiver.setVisibility(View.GONE);

		}

		_swHorloge.setChecked(preferences.annonceHeure.get());
		_swSMS.setChecked(preferences.gererSMS.get());
		_swEMails.setChecked(preferences.gererMails.get());
		_swMessageWhatsApp.setChecked(preferences.messageWhatsAppActif.get());
		_swAppelTelephone.setChecked(preferences.telephoneGerer.get());
		_swAppelWhatsApp.setChecked(preferences.gererAppelsWhatsApp.get());
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		if (Report.GENERER_TRACES)
		{
			MenuItem item = menu.findItem(R.id.menu_report);
			if (item != null)
				item.setVisible(true);
		}
		return true;
	}

	/***
	 * Menu principal
	 * @param item
	 * @return
	 */
	////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item)
	{
		int id = item.getItemId();

		switch (id)
		{
			case R.id.menu_synthese_vocale:
				// Ouvre la fenetre de proprietes Text To Speech d'Android
				startActivityForResult(new Intent("com.android.settings.TTS_SETTINGS"), 0);
				break;

			case R.id.menu_apropos:
				DialogAPropos.start(this);
				break;

			case R.id.menu_report:
				ReportActivity.start(this);
				break;

			case R.id.menu_parametres:
				if (Report.GENERER_TRACES)
					PreferencesActivity.start(this);
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}
}