package com.lpi.compagnonderoute.report;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.lpi.compagnonderoute.ConfirmBox;
import com.lpi.compagnonderoute.R;

public class ReportActivity extends AppCompatActivity
{
	@NonNull
	int _niveau = Report.DEBUG;
	TracesAdapter _adapter;

	/***
	 * Lancer l'activity Report
	 * @param mainActivity
	 */
	public static void start(Activity mainActivity)
	{
		mainActivity.startActivity(new Intent(mainActivity, ReportActivity.class));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Listview qui contient les messages
		ListView lv = findViewById(R.id.idListView);
		lv.setEmptyView(findViewById(R.id.textViewEmpty));
		_adapter = new TracesAdapter(this, TracesDatabase.getInstance(this).getCursor(_niveau));
		lv.setAdapter(_adapter);

		/// Spinner pour le niveau de traces affichées (DEBUG, WARNING, ERROR)
		Spinner spinner = findViewById(R.id.spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.niveauxRapport, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				_niveau = position;
				_adapter.changeCursor(TracesDatabase.getInstance(ReportActivity.this).getCursor(_niveau));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{

			}
		});
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onSupportNavigateUp()
	{
		finish();
		return true;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_report, menu);
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
		int id = item.getItemId();

		switch (id)
		{
			// Vider les traces
			case R.id.menu_report_vider_traces:
			{
				ConfirmBox.show(this, "Effacer les traces?", new ConfirmBox.ConfirmBoxListener()
				{
					@Override public void onPositive()
					{
						TracesDatabase db = TracesDatabase.getInstance(ReportActivity.this);
						db.Vide();
						_adapter.changeCursor(db.getCursor(_niveau));
					}

					@Override public void onNegative()
					{

					}
				});

			}
			break;

			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}
}
