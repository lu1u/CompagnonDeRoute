package com.lpi.compagnonderoute.report;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;


import androidx.annotation.NonNull;

import com.lpi.compagnonderoute.R;

import java.util.Calendar;

/**
 * Adapter pour afficher les traces
 * Created by lucien on 06/02/2016.
 */
public class TracesAdapter extends CursorAdapter
{

public TracesAdapter(Context context, Cursor cursor)
{
	super(context, cursor, 0);
}

public static String formatDate(Context context, int date)
{
	Calendar c = DatabaseHelper.SQLiteDateToCalendar(date);

	//return android.text.format.DateFormat.getDateFormat(context).format(c.getTime()) + ' '
	//		+ android.text.format.DateFormat.getTimeFormat(context).format(c.getTime());

	return String.format( "%02d/%02d %02d:%02d:%02d %03d", c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH),
			c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get( Calendar.SECOND), c.get(Calendar.MILLISECOND));
}

/**
 * Makes a new view to hold the data pointed to by cursor.
 *
 * @param context Interface to application's global information
 * @param cursor  The cursor from which to get the data. The cursor is already
 *                moved to the correct position.
 * @param parent  The parent to which the new view is attached to
 * @return the newly created view.
 */
@Override
public View newView(Context context, Cursor cursor, ViewGroup parent)
{
	return LayoutInflater.from(context).inflate(R.layout.ligne_rapport, parent, false);
}

/**
 * Bind an existing view to the data pointed to by cursor
 *
 * @param view    Existing view, returned earlier by newView
 * @param context Interface to application's global information
 * @param cursor  The cursor from which to get the data. The cursor is already
 */
@Override
public void bindView(@NonNull View view, Context context, @NonNull Cursor cursor)
{
	int date = cursor.getInt(cursor.getColumnIndex(ReportDatabaseHelper.COLONNE_TRACES_DATE));
	String ligne = cursor.getString(cursor.getColumnIndex(ReportDatabaseHelper.COLONNE_TRACES_LIGNE));
	int n = cursor.getInt(cursor.getColumnIndex(ReportDatabaseHelper.COLONNE_TRACES_NIVEAU));
	TextView tv = view.findViewById(R.id.textView);
	switch (n)
	{
		case Report.DEBUG:
			tv.setTextColor(Color.rgb(0, 128, 0));
			break;
		case Report.WARNING:
			tv.setTextColor(Color.rgb(0, 0, 128));
			break;
		case Report.ERROR:
			tv.setTextColor(Color.rgb(128, 0, 0));
			break;
	}
	tv.setText(formatDate(context, date) + ":" + ligne);
}
}
