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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
			Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG, Manifest.permission.SET_ALARM};
	//private @Nullable	ToggleButton _tgActif;
	private Button _bActiver, _bDesactiver;
	private @Nullable
	RadioGroup rgAnnonceHeure, rgLireSMS, rgRepondreSMS, rgRepondreAppels, rgAnnoncerAppels;
	private @Nullable
	CheckBox _checkBoxLireContenuSMS;
	private @Nullable
	ImageButton btnReponseSMS, btnReponseAppels;
	private TextView tvMessage;

	// Intercepte les messages du plannificateur pour maj l'interface utilisateur
	@NonNull
	IntentFilter _intentFilter = new IntentFilter(Plannificateur.ACTION_MESSAGE_UI);
	// Broadcast receiver pour recevoir les message de mise a jour envoyes par les services
	@NonNull
	final BroadcastReceiver _receiverMajUI = new BroadcastReceiver()
	{
		@Override
		public void onReceive(final Context context, final Intent intent)
		{
			String action = intent.getAction();
			if (Plannificateur.ACTION_MESSAGE_UI.equals(action))
			{
				if (tvMessage != null)
				{
					String message = intent.getStringExtra(Plannificateur.EXTRA_MESSAGE_UI);
					tvMessage.setText(message);
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

			if (Preferences.getInstance(this).getActif())
				Plannificateur.getInstance(this).plannifieProchaineNotification(this );
			majUI();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	//@Override
	//protected void onDestroy()
	//{
	//	if ( _receiverMajUI !=null)
	//	{
	//		unregisterReceiver(_receiverMajUI);
	//		_receiverMajUI = null;
	//	}
	//	super.onDestroy();
	//}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		if (!Report.isGenererTraces())
		{
			MenuItem item = menu.findItem(R.id.menu_report);
			item.setVisible(false);
		}
		return true;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	/***
	 * Menu principal
	 * @param item
	 * @return
	 */
	////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		switch (id)
		{
			case R.id.menu_synthese_vocale:
				startActivityForResult(new Intent("com.android.settings.TTS_SETTINGS"), 0); // to come back to your activity.
				break;

			case R.id.menu_apropos:
				DialogAPropos.start(this);
				break;

			case R.id.menu_report:
				ReportActivity.start(this);
				break;

			case R.id.menu_parametres:
				PreferencesActivity.start(this);
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private void majUI()
	{
		try
		{
			final Preferences preferences = Preferences.getInstance(this);
			//if (_tgActif != null)
			//	_tgActif.setChecked(preferences.getActif());
			if ( preferences.getActif())
			{
				if ( _bActiver !=null)
					_bActiver.setVisibility(View.GONE);
				if ( _bDesactiver != null)
					_bDesactiver.setVisibility(View.VISIBLE);
			}
			else
			{
				if ( _bActiver !=null)
					_bActiver.setVisibility(View.VISIBLE);
				if ( _bDesactiver != null)
					_bDesactiver.setVisibility(View.GONE);

			}

			if (rgAnnonceHeure != null)
				switch (preferences.getDelaiAnnonceHeure())
				{
					case Preferences.DELAI_ANNONCE_HEURE_HEURES:
						rgAnnonceHeure.check(R.id.radioButtonAnnonceHeure);
						break;
					case Preferences.DELAI_ANNONCE_HEURE_DEMI:
						rgAnnonceHeure.check(R.id.radioButtonAnnonceDemi);
						break;
					case Preferences.DELAI_ANNONCE_HEURE_QUART:
						rgAnnonceHeure.check(R.id.radioButtonAnnonceQuart);
						break;
					default:
						rgAnnonceHeure.check(R.id.radioButtonAnnonceJamais);
						break;
				}

			if (rgLireSMS != null)
				switch (preferences.getLireSms())
				{
					case Preferences.JAMAIS:
						rgLireSMS.check(R.id.radioButtonSMSJamais);
						break;
					case Preferences.TOUJOURS:
						rgLireSMS.check(R.id.radioButtonSMSToujours);
						break;
					case Preferences.CONTACTS_SEULS:
						rgLireSMS.check(R.id.radioButtonSMSContacts);
						break;
				}

			if (rgRepondreSMS != null)
				switch (preferences.getRepondreSms())
				{
					case Preferences.JAMAIS:
						rgRepondreSMS.check(R.id.radioButtonSMSRepondreJamais);
						btnReponseSMS.setVisibility(View.GONE);
						break;
					case Preferences.TOUJOURS:
						rgRepondreSMS.check(R.id.radioButtonSMSRepondreToujours);
						btnReponseSMS.setVisibility(View.VISIBLE);
						break;
					case Preferences.CONTACTS_SEULS:
						rgRepondreSMS.check(R.id.radioButtonSMSRepondreContacts);
						btnReponseSMS.setVisibility(View.VISIBLE);
						break;
				}

			if (rgRepondreAppels != null)
				switch (preferences.getRepondreAppels())
				{
					case Preferences.JAMAIS:
						rgRepondreAppels.check(R.id.radioButtonAppelsJamais);
						btnReponseAppels.setVisibility(View.GONE);
						break;
					case Preferences.TOUJOURS:
						rgRepondreAppels.check(R.id.radioButtonAppelsToujours);
						btnReponseAppels.setVisibility(View.VISIBLE);
						break;
					case Preferences.CONTACTS_SEULS:
						rgRepondreAppels.check(R.id.radioButtonAppelsContacts);
						btnReponseAppels.setVisibility(View.VISIBLE);
						break;
				}

			if (rgAnnoncerAppels != null)
				switch (preferences.getAnnoncerAppels())
				{
					case Preferences.JAMAIS:
						rgAnnoncerAppels.check(R.id.radioAnnoncerAppelsJamais);
						break;
					case Preferences.TOUJOURS:
						rgAnnoncerAppels.check(R.id.radioAnnoncerAppelsToujours);
						break;
					case Preferences.CONTACTS_SEULS:
						rgAnnoncerAppels.check(R.id.radioAnnoncerAppelsContacts);
						break;
				}

			if (_checkBoxLireContenuSMS!=null)
				_checkBoxLireContenuSMS.setChecked(preferences.getLireContenuSms());
			if ( tvMessage != null)
				tvMessage.setText( preferences.getActif()? R.string.actif : R.string.inactif );
		} catch (Exception e)
		{
			Report r = Report.getInstance(this);
			r.log(Report.ERROR, "Erreur dans majUI");
			r.log(Report.ERROR, e);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtient les elements de l'interface
	 */
	private void initControles()
	{
		//_tgActif = findViewById(R.id.toggleButtonActif);
		_bActiver = findViewById(R.id.buttonActiver);
		_bDesactiver = findViewById(R.id.buttonDesactiver);
		rgLireSMS = findViewById(R.id.radiogroupLireSMS);
		rgAnnonceHeure = findViewById(R.id.radiogroupAnnonceHeure);
		rgLireSMS = findViewById(R.id.radiogroupLireSMS);
		rgRepondreSMS = findViewById(R.id.radiogroupRepondreSMS);
		rgAnnoncerAppels = findViewById(R.id.radiogroupAnnoncerAppels);
		rgRepondreAppels = findViewById(R.id.radiogroupRepondreAppels);
		btnReponseSMS = findViewById(R.id.imageButtonReponseSMS);
		btnReponseAppels = findViewById(R.id.imageButtonReponseAppels);
		_checkBoxLireContenuSMS = findViewById(R.id.checkBoxLireContenuSMS) ;

		tvMessage = findViewById(R.id.textViewStatus);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	/***
	 * Met en place les listeners pour l'interaction avec les controles
	 */
	private void initListeners()
	{
		final Preferences preferences = Preferences.getInstance(this);
		//if (_tgActif != null)
		//{
		//	_tgActif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		//	{
		//		@Override
		//		public void onCheckedChanged(final CompoundButton compoundButton, final boolean checked)
		//		{
		//			preferences.setActif(checked);
		//			Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(checked? R.string.actif: R.string.inactif), Toast.LENGTH_SHORT).show();
		//			TTSService.speakFromAnywhere(MainActivity.this, MainActivity.this.getResources().getString(checked? R.string.actif: R.string.inactif));
		//			if ( checked )
		//				Plannificateur.getInstance(MainActivity.this).plannifieProchaineNotification(MainActivity.this);
		//			else
		//				Plannificateur.getInstance(MainActivity.this).arrete(MainActivity.this);
		//		}
		//	});
		//}

		if ( _bActiver!=null && _bDesactiver!=null)
		{
			_bActiver.setOnClickListener(new View.OnClickListener()
			{
				@Override public void onClick(final View view)
				{
					preferences.setActif(true);
					_bActiver.setAnimation( AnimationUtils.loadAnimation(MainActivity.this, R.anim.pop_exit));
					_bActiver.setVisibility(View.GONE);
					_bDesactiver.setAnimation( AnimationUtils.loadAnimation(MainActivity.this, R.anim.pop_enter));
					_bDesactiver.setVisibility(View.VISIBLE);
					Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.actif), Toast.LENGTH_SHORT).show();
					TTSService.speakFromAnywhere(MainActivity.this, MainActivity.this.getResources().getString( R.string.actif));
					Plannificateur.getInstance(MainActivity.this).plannifieProchaineNotification(MainActivity.this);
				}
			});

			_bDesactiver.setOnClickListener(new View.OnClickListener()
			{
				@Override public void onClick(final View view)
				{
					preferences.setActif(false);
					_bActiver.setAnimation( AnimationUtils.loadAnimation(MainActivity.this, R.anim.pop_enter));
					_bActiver.setVisibility(View.VISIBLE);
					_bDesactiver.setAnimation( AnimationUtils.loadAnimation(MainActivity.this, R.anim.pop_exit));
					_bDesactiver.setVisibility(View.GONE);
					Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.inactif), Toast.LENGTH_SHORT).show();
					TTSService.speakFromAnywhere(MainActivity.this, MainActivity.this.getResources().getString( R.string.inactif));
					Plannificateur.getInstance(MainActivity.this).arrete(MainActivity.this);
				}
			});
		}

		if (rgAnnonceHeure != null)
			rgAnnonceHeure.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final RadioGroup group, @IdRes final int checkedId)
				{
					switch (checkedId)
					{
						case R.id.radioButtonAnnonceJamais:
							preferences.setDelaiAnnonceHeure(Preferences.DELAI_ANNONCE_HEURE_JAMAIS);
							break;
						case R.id.radioButtonAnnonceHeure:
							preferences.setDelaiAnnonceHeure(Preferences.DELAI_ANNONCE_HEURE_HEURES);
							break;
						case R.id.radioButtonAnnonceDemi:
							preferences.setDelaiAnnonceHeure(Preferences.DELAI_ANNONCE_HEURE_DEMI);
							break;
						case R.id.radioButtonAnnonceQuart:
							preferences.setDelaiAnnonceHeure(Preferences.DELAI_ANNONCE_HEURE_QUART);
							break;
					}

					Carillon.changeDelai(MainActivity.this);
				}
			});

		if (rgLireSMS != null)
		{
			rgLireSMS.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final RadioGroup group, @IdRes final int checkedId)
				{
					switch (checkedId)
					{
						case R.id.radioButtonSMSJamais:
							preferences.setLireSms(Preferences.JAMAIS);
							break;
						case R.id.radioButtonSMSToujours:
							preferences.setLireSms(Preferences.TOUJOURS);
							break;
						case R.id.radioButtonSMSContacts:
							preferences.setLireSms(Preferences.CONTACTS_SEULS);
							break;
					}
				}
			});
		}

		if (rgRepondreSMS != null)
		{
			rgRepondreSMS.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final RadioGroup radioGroup, final int checkedId)
				{
					switch (checkedId)
					{
						case R.id.radioButtonSMSRepondreJamais:
							preferences.setRepondreSms(Preferences.JAMAIS);
							cacheBouton(btnReponseSMS);
							break;
						case R.id.radioButtonSMSRepondreToujours:
							preferences.setRepondreSms(Preferences.TOUJOURS);
							montreBouton(btnReponseSMS);
							break;
						case R.id.radioButtonSMSRepondreContacts:
							preferences.setRepondreSms(Preferences.CONTACTS_SEULS);
							montreBouton(btnReponseSMS);
							break;
					}
				}
			});
		}

		if (rgAnnoncerAppels != null)
			rgAnnoncerAppels.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final RadioGroup group, @IdRes final int checkedId)
				{
					switch (checkedId)
					{
						case R.id.radioAnnoncerAppelsJamais:
							preferences.setAnnoncerAppels(Preferences.JAMAIS);
							break;
						case R.id.radioAnnoncerAppelsToujours:
							preferences.setAnnoncerAppels(Preferences.TOUJOURS);
							break;
						case R.id.radioAnnoncerAppelsContacts:
							preferences.setAnnoncerAppels(Preferences.CONTACTS_SEULS);
							break;
					}
				}
			});

		if (rgRepondreAppels != null)
			rgRepondreAppels.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final RadioGroup group, @IdRes final int checkedId)
				{
					switch (checkedId)
					{
						case R.id.radioButtonAppelsJamais:
							preferences.setRepondreAppels(Preferences.JAMAIS);
							cacheBouton(btnReponseAppels);
							break;
						case R.id.radioButtonAppelsToujours:
							preferences.setRepondreAppels(Preferences.TOUJOURS);
							montreBouton(btnReponseAppels);
							break;
						case R.id.radioButtonAppelsContacts:
							preferences.setRepondreAppels(Preferences.CONTACTS_SEULS);
							montreBouton(btnReponseAppels);
							break;
					}
				}
			});

		if (btnReponseSMS != null)
			btnReponseSMS.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(final View v)
				{
					ModalEditText.showEditText(MainActivity.this, R.layout.modal_edittext, R.id.textViewTitre, R.id.editText, R.id.buttonOK, "SMS: réponse automatique", preferences.getReponseSms(), new ModalEditText.ModalEditListener()
					{
						@Override
						public void onTextEdited(final String s)
						{
							preferences.setReponseSMS(s);
							getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
						}
					});
				}
			});

		if (btnReponseAppels != null)
			btnReponseAppels.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(final View v)
				{
					ModalEditText.showEditText(MainActivity.this, R.layout.modal_edittext, R.id.textViewTitre, R.id.editText, R.id.buttonOK, "Appels: réponse automatique", preferences.getReponseAppels(), new ModalEditText.ModalEditListener()
					{
						@Override
						public void onTextEdited(final String s)
						{
							preferences.setReponseAppels(s);
							getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
						}
					});
				}
			});

		if ( _checkBoxLireContenuSMS!=null)
			_checkBoxLireContenuSMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final CompoundButton compoundButton, final boolean b)
				{
					preferences.setLireContenuSms(b);
				}
			});
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
