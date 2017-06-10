/*
* Copyright (C) 2016  Tobias Bielefeld
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*
* If you want to contact me, send me an e-mail at tobias.bielefeld@gmail.com
*/

package de.tobiasbielefeld.brickgames.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.Locale;

import de.tobiasbielefeld.brickgames.*;

import static de.tobiasbielefeld.brickgames.SharedData.*;

 /*
  * Settings activity created from "New Settings Activity" Tool from Android Studio.
  * But i removed the multi pane fragment stuff because i had some problems with it and i think it's
  * not necessary for my 6 settings. So i add the preferences in onCreate and use a
  * onSharedPreferenceChanged Listener for updating the game.
  *
  * I use 2 custom dialogs for the card drawables and background drawables, therefore they have
  * custom preference dialog classes with onClicks and the Summary is updated here
  */

@SuppressWarnings("deprecation")
public class Settings extends AppCompatPreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Preference textureDialog, backgroundDialog, mButtonDialog, mVibrationDialog;                                          //my custom preferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //((ViewGroup) getListView().getParent()).setPadding(0, 0, 0, 0);                             //remove huge padding in landscape
        addPreferencesFromResource(R.xml.pref_settings);
        showOrHideStatusBar();

         /* set a nice back arrow in the actionBar */
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)                                                                      //set a nice back arrow in the actionBar
            actionBar.setDisplayHomeAsUpEnabled(true);

        /* initialize the custom preferences */
        textureDialog = findPreference(getString(R.string.prefKeyTextures));
        backgroundDialog = findPreference(getString(R.string.prefKeyBackground));
        mButtonDialog = findPreference(getString(R.string.prefKeyButtons));
        mVibrationDialog = findPreference(getString(R.string.prefKeyVibrationStrength));

        /* set default values for summary of custom preferences*/
        setPreferenceTextures();
        setPreferenceBackground();
        setPreferenceButton();
        setVibrationStrength();
    }

    void setVibrationStrength() {
        int strength = savedData.getInt(getString(R.string.prefKeyVibrationStrength),20);

        if (strength==0)
            mVibrationDialog.setSummary(getString(R.string.off));
        else
            mVibrationDialog.setSummary(String.format(Locale.getDefault(),"%s ms",strength));
    }

    void  setPreferenceTextures() {
        textureDialog.setSummary(String.format(Locale.getDefault(), "%s %s",getString(R.string.number),
                savedData.getInt(getString(R.string.prefKeyTextures), 2)));
    }

    void setPreferenceBackground() {
        String text;

        switch (savedData.getInt(getString(R.string.prefKeyBackground), 1)) {
            case 1:default:
                text = getString(R.string.colour_blue);
                break;
            case 2:
                text = getString(R.string.colour_green);
                break;
            case 3:
                text = getString(R.string.colour_red);
                break;
            case 4:
                text = getString(R.string.colour_yellow);
                break;
            case 5:
                text = getString(R.string.colour_gray);
                break;
        }

        backgroundDialog.setSummary(text);
    }

    void setPreferenceButton() {
        String text;

        switch (savedData.getInt(getString(R.string.prefKeyButtons), 1)) {
            case 1:default:
                text = getString(R.string.colour_blue);
                break;
            case 2:
                text = getString(R.string.colour_green);
                break;
            case 3:
                text = getString(R.string.colour_red);
                break;
            case 4:
                text = getString(R.string.colour_yellow);
                break;
            case 5:
                text = getString(R.string.colour_gray);
                break;
        }

        mButtonDialog.setSummary(text);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {                                           //only menu item is the back mButton in the action bar
        finish();                                                                                   //so finish this activity
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this); // Set up a listener whenever a key changes
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);//unregister the listener
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case "prefKeyTextures":
                setPreferenceTextures();
                break;
            case "prefKeyBackground":
                setPreferenceBackground();
                getIntent().putExtra(getString(R.string.prefKeyBackground), 1);
                break;
            case "prefKeyButtons":
                setPreferenceButton();
                getIntent().putExtra(getString(R.string.prefKeyButtons), 1);
                break;
            case "prefKeyHideStatusBar":
                showOrHideStatusBar();
                getIntent().putExtra(getString(R.string.prefKeyHideStatusBar), 1);
                getIntent().putExtra(getString(R.string.prefKeyEnableKeyboardInput), 1);            //to resize the ui layout
                break;
            case "prefKeyVibrationStrength":
                setVibrationStrength();
                break;
            case "pref_key_language":
                setLocale();
                break;
            case "prefKeyEnableKeyboardInput":
                getIntent().putExtra(getString(R.string.prefKeyEnableKeyboardInput), 1);
                break;
        }

        setResult(RESULT_OK, getIntent());
    }

    private void setLocale() {
        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void showOrHideStatusBar() {
        if (savedData.getBoolean(getString(R.string.prefKeyHideStatusBar), false))
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        else
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
