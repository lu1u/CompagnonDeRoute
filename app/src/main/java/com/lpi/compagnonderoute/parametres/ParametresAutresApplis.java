package com.lpi.compagnonderoute.parametres;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lpi.compagnonderoute.R;
import com.lpi.compagnonderoute.notificationListener.NotificationListener;
import com.lpi.compagnonderoute.preferences.Preferences;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Parametres pour gerer les notifications de toutes les application non specifiques
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class ParametresAutresApplis
{
	/*******************************************************************************************
	 * Affiche et gere la fenetre de reglage des notifications pour Autres Applications
	 * @param context
	 *******************************************************************************************/
	public static void start(@NonNull final Activity context)
	{
		final AlertDialog dialogBuilder = new AlertDialog.Builder(context).create();

		LayoutInflater inflater = context.getLayoutInflater();
		final View dialogView = inflater.inflate(R.layout.parametres_autres_applis, null);
		final ListView listView = dialogView.findViewById(R.id.listApplications);

		BackgroundTaskWithSpinner.execute(context, R.layout.background_working, new BackgroundTaskWithSpinner.TaskListener()
		{
			@Override public void execute()
			{
				listView.setAdapter(new ApplicationsAdapter(context));
			}

			@Override public void onFinished()
			{
				dialogBuilder.setView(dialogView);
				dialogBuilder.show();
			}
		});
	}

	static private class InfoAppli
	{
		public String packageName;
		public String applicationName;
		public boolean nomAppli;
		public boolean titre;
		public boolean contenu;
	}

	/***
	 * Adapter pour la liste des applications installées
	 */
	static public class ApplicationsAdapter extends ArrayAdapter<InfoAppli>
	{
		public ApplicationsAdapter(Context context)
		{
			super(context, 0, getListApplications(context));
		}

		/*******************************************************************************************
		 * Retrouve la liste des applications installées, triée par ordre de nom d'application
		 * @param context
		 * @return
		 *******************************************************************************************/
		private static @NonNull List<InfoAppli> getListApplications(@NonNull final Context context)
		{
			Preferences prefs = Preferences.getInstance(context);
			final String notreApplication = context.getPackageName();
			ArrayList<InfoAppli> liste = new ArrayList<>();
			List<PackageInfo> packList = context.getPackageManager().getInstalledPackages(0);
			for (int i = 0; i < packList.size(); i++)
			{
				PackageInfo packInfo = packList.get(i);
				if ((packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0              // Ne pas afficher les applications systeme
						&& !notreApplication.equals(packInfo.packageName)                            // Ne pas annoncer notre propre application, sinon boucle infinie
						&& !NotificationListener.GMAIL_PACKAGE.equals(packInfo.packageName)          // Application GMail traitée ailleurs
				)
				{
					InfoAppli a = new InfoAppli();
					a.packageName = packInfo.packageName;
					a.applicationName = getApplicationName(context, packInfo);
					a.titre = prefs.getNotificationTitre(packInfo.packageName);
					a.contenu = prefs.getNotificationContenu(packInfo.packageName);
					a.nomAppli = prefs.getNotificationNomAppli(packInfo.packageName);
					liste.add(a);
				}
			}

			liste.sort(new Comparator<InfoAppli>()
			{
				@Override public int compare(final InfoAppli appli1, final InfoAppli appli2)
				{
					return appli1.applicationName.compareTo(appli2.applicationName);
				}
			});
			return liste;
		}

		/*******************************************************************************************
		 * Retrouve le nom affichable d'une application a partir de son PackageName
		 * @param context
		 * @param packageInfo
		 * @return
		 *******************************************************************************************/
		private static @NonNull
		String getApplicationName(@NonNull final Context context, @NonNull final PackageInfo packageInfo)
		{
			PackageManager packageManager = context.getPackageManager();
			ApplicationInfo applicationInfo = null;
			try
			{
				applicationInfo = packageManager.getApplicationInfo(packageInfo.packageName, 0);
			} catch
			(final PackageManager.NameNotFoundException e)
			{
				e.printStackTrace();
			}
			return (String) ((applicationInfo != null) ? packageManager.getApplicationLabel(applicationInfo) : "???");
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{

			final InfoAppli packageInfo = getItem(position);
			final Preferences preferences = Preferences.getInstance(getContext());

			// Check if an existing view is being reused, otherwise inflate the view
			if (convertView == null)
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.element_liste_applications, parent, false);

			TextView tvName = convertView.findViewById(R.id.textViewNomAppli);
			TextView tvPackage = convertView.findViewById(R.id.textViewPackageName);
			Switch swTitre = convertView.findViewById(R.id.switchTitre);
			Switch swContenu = convertView.findViewById(R.id.switchContenu);
			Switch swNomAppli = convertView.findViewById(R.id.switchNomAppli);

			tvName.setText(packageInfo.applicationName);
			tvPackage.setText(packageInfo.packageName);
			swTitre.setChecked(packageInfo.titre);
			swContenu.setChecked(packageInfo.contenu);
			swNomAppli.setChecked(packageInfo.nomAppli);

			swTitre.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final CompoundButton compoundButton, final boolean b)
				{
					if (compoundButton.isPressed())
					{
						packageInfo.titre = b;
						preferences.setNotificationTitre(packageInfo.packageName, b);
					}
				}
			});

			swContenu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final CompoundButton compoundButton, final boolean b)
				{
					if (compoundButton.isPressed())
					{
						packageInfo.contenu = b;
						preferences.setNotificationContenu(packageInfo.packageName, b);
					}
				}
			});

			swNomAppli.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final CompoundButton compoundButton, final boolean b)
				{
					if (compoundButton.isPressed())
					{
						packageInfo.nomAppli = b;
						preferences.setNotificationNomAppli(packageInfo.packageName, b);
					}
				}
			});

			try
			{
				ImageView imageView = convertView.findViewById(R.id.imageViewIcone);
				Drawable icon = getContext().getPackageManager().getApplicationIcon(packageInfo.packageName);
				imageView.setImageDrawable(icon);
			} catch (PackageManager.NameNotFoundException e)
			{
				e.printStackTrace();
			}
			return convertView;

		}

	}
}
