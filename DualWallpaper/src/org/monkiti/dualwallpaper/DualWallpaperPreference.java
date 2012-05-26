package org.monkiti.dualwallpaper;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;

public class DualWallpaperPreference extends PreferenceActivity {
	
	private Preference landContact;
	private Preference portContact;

	private final int REQUEST_LANDSCAPE = 1;
	private final int REQUEST_PORTRAIT = 2;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.pref);
		
		landContact = findPreference("landscape");
		landContact.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				Intent i = new Intent(android.content.Intent.ACTION_PICK);
				i.setType("image/*");
				startActivityForResult(i, REQUEST_LANDSCAPE);
				return true;
			}
		});
		
		portContact = findPreference("portrait");
		portContact.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				Intent i = new Intent(android.content.Intent.ACTION_PICK);
				i.setType("image/*");
				startActivityForResult(i, REQUEST_PORTRAIT);
				return true;
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Uri uri = data.getData();
			
			String[] columns = { MediaStore.Images.Media.DATA };
			ContentResolver cr = getContentResolver();
			Cursor cursor = cr.query(uri, columns, null, null, null);
			cursor.moveToFirst();
			String path = cursor.getString(0);

			SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        	
			if (requestCode == REQUEST_LANDSCAPE) {
				edit.putString("landscape", path);
			} else {
				edit.putString("portrait", path);
			}
			
			edit.commit();
		}
	}
}
