package com.lpi.compagnonderoute;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.lpi.compagnonderoute.database.NotificationAdapter;
import com.lpi.compagnonderoute.database.NotificationDatabase;

/***
 * Activité pour afficher le journal des notifications, rien de compliqué, le principal est
 * fait dans la class NotificationAdapter
 */
public class NotificationLogActivity extends AppCompatActivity
{
	NotificationAdapter _adapter;

	/***
	 * Lancer l'activity Report
	 * @param mainActivity
	 */
	public static void start(Activity mainActivity)
	{
		mainActivity.startActivity(new Intent(mainActivity, NotificationLogActivity.class));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notification_log);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Listview qui contient les messages
		ListView lv = findViewById(R.id.idListView);
		lv.setEmptyView(findViewById(R.id.textViewEmpty));
		_adapter = new NotificationAdapter(this, NotificationDatabase.getInstance(this).getCursor());
		lv.setAdapter(_adapter);
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
				ConfirmBox.show(this, R.string.effacer_notifications, new ConfirmBox.ConfirmBoxListener()
				{
					@Override public void onPositive()
					{
						NotificationDatabase db = NotificationDatabase.getInstance(NotificationLogActivity.this);
						db.Vide();
						_adapter.changeCursor(db.getCursor());
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
