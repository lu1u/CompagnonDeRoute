package com.lpi.compagnonderoute.parametres;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.lpi.compagnonderoute.report.Report;

class BackgroundTaskWithSpinner
{
	/*******************************************************************************************
	 * Execute une tache en arriere plan, avec une fenetre affichee pendant ce temps (devrait
	 * contenir une progress bar circulaire
	 * @param context
	 * @param layoutId
	 * @param listener
	 *******************************************************************************************/
	public static void execute(@NonNull final Activity context, @LayoutRes int layoutId, @NonNull final TaskListener listener)
	{
		Report r = Report.getInstance(context);
		r.log(Report.DEBUG, "BackgroundTaskWithSpinner.execute");
		final AlertDialog dialogBuilder = new AlertDialog.Builder(context).create();

		LayoutInflater inflater = context.getLayoutInflater();
		final View dialogView = inflater.inflate(layoutId, null);
		dialogBuilder.setView(dialogView);

		new AsyncTask<Void, Void, Void>()
		{
			@Override protected void onPreExecute()
			{
				super.onPreExecute();
				r.log(Report.DEBUG, "AsyncTask.onPreExecute");
				dialogBuilder.show();
			}

			@Override
			protected void onProgressUpdate(final Void... values)
			{
				super.onProgressUpdate(values);
			}

			@Override protected void onPostExecute(final Void aVoid)
			{
				super.onPostExecute(aVoid);
				r.log(Report.DEBUG, "AsyncTask.onPostExecute");
				if (dialogBuilder.isShowing())
					dialogBuilder.dismiss();
				listener.onFinished();
			}

			@Override
			protected Void doInBackground(final Void... voids)
			{
				r.log(Report.DEBUG, "AsyncTask.doInBackground");

				listener.execute();
				return null;
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public interface TaskListener
	{
		void execute();         // Tache a effectuer en arriere plan
		void onFinished();      // Tache terminee
	}
}
