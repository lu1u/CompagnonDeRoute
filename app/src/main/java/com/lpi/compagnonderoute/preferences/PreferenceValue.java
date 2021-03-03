package com.lpi.compagnonderoute.preferences;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import com.lpi.compagnonderoute.database.DatabaseHelper;

class PreferenceValue
{
	private static final String SELECTION_NOM = DatabaseHelper.COLONNE_NOM + "=?";
	private static final String[] COLONNES_VALEUR = {DatabaseHelper.COLONNE_VALEUR};
	protected final SQLiteDatabase _database;
	protected final String _nom;
	protected String _valeur;

	protected PreferenceValue(@NonNull final SQLiteDatabase database, @NonNull final String nom, @NonNull final String defaut)
	{
		_database = database;
		_nom = nom;
		_valeur = getValue(defaut);
	}

	protected @NonNull String getValue(@NonNull final String defaut)
	{
		Cursor cursor = null;
		String valeur = defaut;
		try
		{
			String[] selectionArgs = new String[]{_nom};
			cursor = _database.query(DatabaseHelper.TABLE_PREFERENCES, COLONNES_VALEUR, SELECTION_NOM, selectionArgs, null, null, null);
			if (cursor != null)
			{
				cursor.moveToFirst();
				final int index = cursor.getColumnIndex(DatabaseHelper.COLONNE_VALEUR);
				valeur = cursor.getString(index);
			}
		} catch (Exception e)
		{
			valeur = defaut;
		} finally
		{
			if (cursor != null)
				cursor.close();
		}
		return valeur;
	}

	protected void setValue(@NonNull final String valeur)
	{
		try
		{
			_valeur = valeur;
			ContentValues values = new ContentValues();
			values.put(DatabaseHelper.COLONNE_NOM, _nom);
			values.put(DatabaseHelper.COLONNE_VALEUR, _valeur);

			String[] selectionArgs = new String[]{_nom};

			int affectedRows = _database.update(DatabaseHelper.TABLE_PREFERENCES, values, SELECTION_NOM, selectionArgs);
			if (affectedRows <= 0)
				_database.insertWithOnConflict(DatabaseHelper.TABLE_PREFERENCES, null, values, SQLiteDatabase.CONFLICT_IGNORE);

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
