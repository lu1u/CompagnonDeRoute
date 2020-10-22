package com.lpi.compagnonderoute;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lpi.compagnonderoute.plannificateur.Plannificateur;
import com.lpi.compagnonderoute.preferences.Preferences;
import com.lpi.compagnonderoute.preferences.PreferencesActivity;
import com.lpi.compagnonderoute.report.Report;
import com.lpi.compagnonderoute.report.ReportActivity;
import com.lpi.compagnonderoute.tts.TTSService;

public class MainActivity extends AppCompatActivity
{
	static final String[] permissions = {Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS,
			Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG,
			Manifest.permission.SET_ALARM, Manifest.permission.MODIFY_AUDIO_SETTINGS};
	private ImageButton _bActiver, _bDesactiver;
	private @Nullable
	RadioGroup _rgAnnonceHeure, _rgLireSMS, _rgRepondreSMS, _rgRepondreAppels, _rgAnnoncerAppels;

	private @Nullable
	ImageButton _btnReponseSMS, _btnReponseAppels;
	private TextView _tvMessage;

	// Intercepte les messages du plannificateur pour maj l'interface utilisateur
	@NonNull final IntentFilter _intentFilter = new IntentFilter(Plannificateur.ACTION_MESSAGE_UI);
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
		setContentView(R.layout.activity_main);
		initControles();
		initListeners();
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
			Permissions.demandePermissionsSiBesoin(this, permissions);
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
	 * Mise a jour de l'interface en fonction de l'etat de l'application
	 */
	private void majUI()
	{
		try
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

			if (_rgAnnonceHeure != null)
				switch (preferences.delaiAnnonceHeure.get())
				{
					case Preferences.DELAI_ANNONCE_HEURE_HEURES:
						_rgAnnonceHeure.check(R.id.radioButtonAnnonceHeure);
						break;
					case Preferences.DELAI_ANNONCE_HEURE_DEMI:
						_rgAnnonceHeure.check(R.id.radioButtonAnnonceDemi);
						break;
					case Preferences.DELAI_ANNONCE_HEURE_QUART:
						_rgAnnonceHeure.check(R.id.radioButtonAnnonceQuart);
						break;
					default:
						_rgAnnonceHeure.check(R.id.radioButtonAnnonceJamais);
						break;
				}

			if (_rgLireSMS != null)
				switch (preferences.lireSMS.get())
				{
					case Preferences.JAMAIS:
						_rgLireSMS.check(R.id.radioButtonSMSJamais);
						break;
					case Preferences.TOUJOURS:
						_rgLireSMS.check(R.id.radioButtonSMSToujours);
						break;
					case Preferences.CONTACTS_SEULS:
						_rgLireSMS.check(R.id.radioButtonSMSContacts);
						break;
				}

			if (_rgRepondreSMS != null)
				switch (preferences.repondreSms.get())
				{
					case Preferences.JAMAIS:
						_rgRepondreSMS.check(R.id.radioButtonSMSRepondreJamais);
						_btnReponseSMS.setVisibility(View.GONE);
						break;
					case Preferences.TOUJOURS:
						_rgRepondreSMS.check(R.id.radioButtonSMSRepondreToujours);
						_btnReponseSMS.setVisibility(View.VISIBLE);
						break;
					case Preferences.CONTACTS_SEULS:
						_rgRepondreSMS.check(R.id.radioButtonSMSRepondreContacts);
						_btnReponseSMS.setVisibility(View.VISIBLE);
						break;
				}

			if (_rgRepondreAppels != null)
				switch (preferences.telephoneRepondre.get())
				{
					case Preferences.JAMAIS:
						_rgRepondreAppels.check(R.id.radioButtonAppelsJamais);
						_btnReponseAppels.setVisibility(View.GONE);
						break;
					case Preferences.TOUJOURS:
						_rgRepondreAppels.check(R.id.radioButtonAppelsToujours);
						_btnReponseAppels.setVisibility(View.VISIBLE);
						break;
					case Preferences.CONTACTS_SEULS:
						_rgRepondreAppels.check(R.id.radioButtonAppelsContacts);
						_btnReponseAppels.setVisibility(View.VISIBLE);
						break;
				}

			if (_rgAnnoncerAppels != null)
				switch (preferences.telephoneAnnoncer.get())
				{
					case Preferences.JAMAIS:
						_rgAnnoncerAppels.check(R.id.radioAnnoncerAppelsJamais);
						break;
					case Preferences.TOUJOURS:
						_rgAnnoncerAppels.check(R.id.radioAnnoncerAppelsToujours);
						break;
					case Preferences.CONTACTS_SEULS:
						_rgAnnoncerAppels.check(R.id.radioAnnoncerAppelsContacts);
						break;
				}

//			if (_cbLireContenuSMS != null)
//				_cbLireContenuSMS.setChecked(preferences.getLireContenuSms());

			if (_tvMessage != null)
				_tvMessage.setText(preferences.actif.get() ? R.string.enabled : R.string.disabled);
		} catch (Exception e)
		{
			Report r = Report.getInstance(this);
			r.log(Report.ERROR, "Erreur dans majUI");
			r.log(Report.ERROR, e);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

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

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Obtient les elements de l'interface
	 */
	private void initControles()
	{
		_bActiver = findViewById(R.id.imageButtonActiver);
		_bDesactiver = findViewById(R.id.imageButtonDesactiver);
		_rgLireSMS = findViewById(R.id.radiogroupLireSMS);
		_rgAnnonceHeure = findViewById(R.id.radiogroupAnnonceHeure);
		_rgRepondreSMS = findViewById(R.id.radiogroupRepondreSms);
		_rgAnnoncerAppels = findViewById(R.id.radiogroupAnnoncerAppels);
		_rgRepondreAppels = findViewById(R.id.radiogroupRepondreAppels);
		_btnReponseSMS = findViewById(R.id.imageButtonReponseSMS);
		_btnReponseAppels = findViewById(R.id.imageButtonReponseAppels);
//		_cbLireContenuSMS = findViewById(R.id.checkBoxLireContenuSMS);
		_tvMessage = findViewById(R.id.textViewStatus);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/***
	 * Met en place les listeners pour l'interaction avec les controles
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
					_bActiver.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.pop_exit));
					_bActiver.setVisibility(View.GONE);
					_bDesactiver.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.pop_enter));
					_bDesactiver.setVisibility(View.VISIBLE);
					Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.enabled), Toast.LENGTH_SHORT).show();
					TTSService.speakFromAnywhere(MainActivity.this, R.raw.beep,
							preferences.volumeDefaut.get() ? preferences.volume.get() : -1,
							MainActivity.this.getResources().getString(R.string.enabled));
					Plannificateur.getInstance(MainActivity.this).plannifieProchaineNotification(MainActivity.this);
				}
			});

			_bDesactiver.setOnClickListener(new View.OnClickListener()
			{
				@Override public void onClick(final View view)
				{
					r.log(Report.HISTORIQUE, "Désactivé");

					preferences.actif.set(false);
					_bActiver.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.pop_enter));
					_bActiver.setVisibility(View.VISIBLE);
					_bDesactiver.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.pop_exit));
					_bDesactiver.setVisibility(View.GONE);
					Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.disabled), Toast.LENGTH_SHORT).show();
					TTSService.speakFromAnywhere(MainActivity.this, R.raw.beep, preferences.volumeDefaut.get() ? preferences.volume.get() : -1, MainActivity.this.getResources().getString(R.string.disabled));
					Plannificateur.getInstance(MainActivity.this).arrete(MainActivity.this);
				}
			});
		}

		if (_rgAnnonceHeure != null)
			_rgAnnonceHeure.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final RadioGroup group, @IdRes final int checkedId)
				{
					switch (checkedId)
					{
//						case R.id.radioButtonAnnonceJamais:
//							r.log(Report.HISTORIQUE, "Carillon: jamais");
//							preferences.setDelaiAnnonceHeure(Preferences.DELAI_ANNONCE_HEURE_JAMAIS);
//							break;
						case R.id.radioButtonAnnonceHeure:
							r.log(Report.HISTORIQUE, "Carillon: heures");
							preferences.delaiAnnonceHeure.set(Preferences.DELAI_ANNONCE_HEURE_HEURES);
							break;
						case R.id.radioButtonAnnonceDemi:
							r.log(Report.HISTORIQUE, "Carillon: demi");
							preferences.delaiAnnonceHeure.set(Preferences.DELAI_ANNONCE_HEURE_DEMI);
							break;
						case R.id.radioButtonAnnonceQuart:
							r.log(Report.HISTORIQUE, "Carillon: quart");
							preferences.delaiAnnonceHeure.set(Preferences.DELAI_ANNONCE_HEURE_QUART);
							break;
					}

					Carillon.changeDelai(MainActivity.this);
				}
			});

		if (_rgLireSMS != null)
		{
			_rgLireSMS.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final RadioGroup group, @IdRes final int checkedId)
				{
					switch (checkedId)
					{
						case R.id.radioButtonSMSJamais:
							r.log(Report.HISTORIQUE, "Messages: jamais");
							preferences.lireSMS.set(Preferences.JAMAIS);
							break;
						case R.id.radioButtonSMSToujours:
							r.log(Report.HISTORIQUE, "Messages: toujours");
							preferences.lireSMS.set(Preferences.TOUJOURS);
							break;
						case R.id.radioButtonSMSContacts:
							r.log(Report.HISTORIQUE, "Messages: contacts");
							preferences.lireSMS.set(Preferences.CONTACTS_SEULS);
							break;
					}
				}
			});
		}

		if (_rgRepondreSMS != null)
		{
			_rgRepondreSMS.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final RadioGroup radioGroup, final int checkedId)
				{
					switch (checkedId)
					{
						case R.id.radioButtonSMSRepondreJamais:
							r.log(Report.HISTORIQUE, "reponse messages: jamais");
							preferences.repondreSms.set(Preferences.JAMAIS);
							cacheBouton(_btnReponseSMS);
							break;
						case R.id.radioButtonSMSRepondreToujours:
							r.log(Report.HISTORIQUE, "reponse messages: toujours");
							preferences.repondreSms.set(Preferences.TOUJOURS);
							montreBouton(_btnReponseSMS);
							break;
						case R.id.radioButtonSMSRepondreContacts:
							r.log(Report.HISTORIQUE, "reponse messages: contacts");
							preferences.repondreSms.set(Preferences.CONTACTS_SEULS);
							montreBouton(_btnReponseSMS);
							break;
					}
				}
			});
		}

		if (_rgAnnoncerAppels != null)
			_rgAnnoncerAppels.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final RadioGroup group, @IdRes final int checkedId)
				{
					switch (checkedId)
					{
						case R.id.radioAnnoncerAppelsJamais:
							r.log(Report.HISTORIQUE, "appels: jamais");
							preferences.telephoneAnnoncer.set(Preferences.JAMAIS);
							break;
						case R.id.radioAnnoncerAppelsToujours:
							r.log(Report.HISTORIQUE, "appels: toujours");
							preferences.telephoneAnnoncer.set(Preferences.TOUJOURS);
							break;
						case R.id.radioAnnoncerAppelsContacts:
							r.log(Report.HISTORIQUE, "appels: contacts");
							preferences.telephoneAnnoncer.set(Preferences.CONTACTS_SEULS);
							break;
					}
				}
			});

		if (_rgRepondreAppels != null)
			_rgRepondreAppels.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final RadioGroup group, @IdRes final int checkedId)
				{
					switch (checkedId)
					{
						case R.id.radioButtonAppelsJamais:
							r.log(Report.HISTORIQUE, "repondre appels: jamais");
							preferences.telephoneRepondre.set(Preferences.JAMAIS);
							cacheBouton(_btnReponseAppels);
							break;
						case R.id.radioButtonAppelsToujours:
							r.log(Report.HISTORIQUE, "repondre appels: toujours");
							preferences.telephoneRepondre.set(Preferences.TOUJOURS);
							montreBouton(_btnReponseAppels);
							break;
						case R.id.radioButtonAppelsContacts:
							r.log(Report.HISTORIQUE, "repondre appels: contacts");
							preferences.telephoneRepondre.set(Preferences.CONTACTS_SEULS);
							montreBouton(_btnReponseAppels);
							break;
					}
				}
			});

		if (_btnReponseSMS != null)
			_btnReponseSMS.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(final View v)
				{
					ModalEditText.showEditText(MainActivity.this, R.layout.modal_edittext, R.id.textViewTitre, R.id.editText, R.id.buttonOK,
							"SMS: réponse automatique", preferences.reponseSms.get(), new ModalEditText.ModalEditListener()
							{
								@Override
								public void onTextEdited(final String s)
								{
									preferences.reponseSms.set(s);
									getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
								}
							});
				}
			});

		if (_btnReponseAppels != null)
			_btnReponseAppels.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(final View v)
				{
					ModalEditText.showEditText(MainActivity.this, R.layout.modal_edittext, R.id.textViewTitre, R.id.editText, R.id.buttonOK,
							"Appels: réponse automatique", preferences.telephoneReponse.get(), new ModalEditText.ModalEditListener()
							{
								@Override
								public void onTextEdited(final String s)
								{
									preferences.telephoneReponse.set(s);
									getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
								}
							});
				}
			});

//		if (_cbLireContenuSMS != null)
//			_cbLireContenuSMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//			{
//				@Override
//				public void onCheckedChanged(final CompoundButton compoundButton, final boolean b)
//				{
//					preferences.setLireContenuSms(b);
//				}
//			});

	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////
	private void cacheBouton(final @NonNull View view)
	{
		view.setAnimation(AnimationUtils.loadAnimation(this, R.anim.exit_top));
		view.setVisibility(View.GONE);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////
	private void montreBouton(final @NonNull View view)
	{
		view.setAnimation(AnimationUtils.loadAnimation(this, R.anim.enter_top));
		view.setVisibility(View.VISIBLE);
	}
}
