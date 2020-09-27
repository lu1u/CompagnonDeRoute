package com.lpi.compagnonderoute;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ModalEditText
{
	/***
	 * Edition d'un texte dans une fenetre "modale"
	 * Fournir un ModalEditListener pour etre averti du changement
	 * @param activity
	 * @param idLayout
	 * @param idTitre
	 * @param idEdit
	 * @param idSubmit
	 * @param titre
	 * @param listener
	 */
	public static void showEditText(@NonNull final Activity activity, @LayoutRes int idLayout, @IdRes int idTitre, @IdRes int idEdit, @IdRes int idSubmit, @NonNull String titre, @Nullable String valeur, @NonNull final ModalEditListener listener)
	{
		final AlertDialog dialogBuilder = new AlertDialog.Builder(activity).create();
		LayoutInflater inflater = activity.getLayoutInflater();
		@SuppressLint("ResourceType") View dialogView = inflater.inflate(idLayout, null);

		final EditText editText = dialogView.findViewById(idEdit);
		final Button button1 = dialogView.findViewById(idSubmit);
		final TextView tvTitre = dialogView.findViewById(idTitre);
		tvTitre.setText(titre);

		if (valeur != null)
			editText.setText(valeur);
		button1.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				listener.onTextEdited(editText.getText().toString());
				dialogBuilder.dismiss();
				activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
			}
		});

		dialogBuilder.setView(dialogView);
		dialogBuilder.show();

	}

	public interface ModalEditListener
	{
		void onTextEdited(String s);
	}
}
