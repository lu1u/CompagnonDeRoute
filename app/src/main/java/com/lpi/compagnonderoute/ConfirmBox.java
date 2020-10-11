package com.lpi.compagnonderoute;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;

/**
 * Helper pour afficher une fenetre de confirmation, fournir un listener pour etre prevenu du
 * resultat
 */
public class ConfirmBox
{
	/***
	 *  Afficher la fenetre de confirmation
	 * @param context
	 * @param message
	 * @param listener
	 */
	public static void show(@NonNull final Context context, @NonNull final String message, @NonNull final ConfirmBoxListener listener)
	{
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				switch (which)
				{
					case DialogInterface.BUTTON_POSITIVE:
						//Yes button clicked
						listener.onPositive();
						break;

					case DialogInterface.BUTTON_NEGATIVE:
						//No button clicked
						listener.onNegative();
						break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message)
				.setPositiveButton(context.getResources().getString(android.R.string.ok), dialogClickListener)
				.setNegativeButton(context.getResources().getString(android.R.string.cancel), dialogClickListener)
				.setOnCancelListener(new DialogInterface.OnCancelListener()
				{
					@Override public void onCancel(final DialogInterface dialogInterface)
					{
						listener.onNegative();
					}
				})
				.setOnDismissListener(new DialogInterface.OnDismissListener()
				{
					@Override public void onDismiss(final DialogInterface dialogInterface)
					{
						listener.onNegative();
					}
				})
				.show();
	}

	// Le listener a fournir par l'appelant, lui permettra de savoir quelle option a ete choisie
	public interface ConfirmBoxListener
	{
		void onPositive();
		void onNegative();
	}
}
