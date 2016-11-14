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

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Locale;

import de.tobiasbielefeld.brickgames.*;

import static de.tobiasbielefeld.brickgames.SharedData.savedData;

/*
 *  shows information about the game, license and changelog
 */

public class About extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.about);
        showOrHideStatusBar();

        /* set a nice back arrow in the actionBar */
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)                                                                      //set a nice back arrow in the actionBar
            actionBar.setDisplayHomeAsUpEnabled(true);

        TabHost host = (TabHost)findViewById(R.id.tabHost);
        TextView about_text_build = (TextView) findViewById(R.id.about_text_build);
        TextView about_text_version = (TextView) findViewById(R.id.about_text_version);

        TextView aboutTextPulseBoyLicense = (TextView) findViewById(R.id.aboutTextPulseBoyLicense);
        TextView aboutTextBFXRLicense = (TextView) findViewById(R.id.aboutTextBFXRLicense);
        aboutTextPulseBoyLicense.setMovementMethod(LinkMovementMethod.getInstance());
        aboutTextBFXRLicense.setMovementMethod(LinkMovementMethod.getInstance());

        if (host!=null) {
            host.setup();
            //Tab 1
            TabHost.TabSpec spec = host.newTabSpec("Tab One");
            spec.setContent(R.id.tab1);
            spec.setIndicator(getString(R.string.about_tab_1));
            host.addTab(spec);

            //Tab 2
            spec = host.newTabSpec("Tab Two");
            spec.setContent(R.id.tab2);
            spec.setIndicator(getString(R.string.about_tab_2));
            host.addTab(spec);

            //Tab 3
            spec = host.newTabSpec("Tab Three");
            spec.setContent(R.id.tab3);
            spec.setIndicator(getString(R.string.about_tab_3));
            host.addTab(spec);
        }

        SimpleDateFormat simpleDate =  new SimpleDateFormat("yyyy-MM-dd");
        String strDt = simpleDate.format(BuildConfig.TIMESTAMP);

        if (about_text_build!=null && about_text_version!=null) {
            about_text_version.setText(String.format(Locale.getDefault(),"%s: %s",
                    getString(R.string.app_version),BuildConfig.VERSION_NAME));
            about_text_build.setText(String.format(Locale.getDefault(), "%s: %s",
                    getResources().getString(R.string.about_build_date), strDt));
        }


        TextView aboutTextViewGitHubLink = (TextView) findViewById(R.id.aboutTextViewGitHubLink);
        TextView aboutLicenseText = (TextView) findViewById(R.id.aboutLicenseText);

        aboutTextViewGitHubLink.setMovementMethod(LinkMovementMethod.getInstance());

        try {                                                                                       //show the gpl license from the license.html in the assets folder
            InputStream is = getAssets().open("license.html");
            aboutLicenseText.setText(Html.fromHtml(new String(getStringFromInputStream(is))));
        } catch (IOException ignored) {}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    private void showOrHideStatusBar() {
        if (savedData.getBoolean(getString(R.string.prefKeyHideStatusBar), false))
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private byte[] getStringFromInputStream(InputStream is) {                                       //Solution from StackOverflow, found here: https://stackoverflow.com/questions/2436385
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        byte[] bReturn = new byte[0];
        String line;

        try {
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            while ((line = br.readLine()) != null) {
                sb.append(line).append(" ");
            }
            String sContent = sb.toString();
            bReturn = sContent.getBytes();
        } catch (IOException ignored) {

        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignored) {}
            }
        }
        return bReturn;
    }
}
