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

package de.tobiasbielefeld.brickgames.dialogs;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

import de.tobiasbielefeld.brickgames.R;

import static de.tobiasbielefeld.brickgames.SharedData.*;

/*
 * custom dialog to set the vibration strength for button presses. it can be set from 0 (off) to 100 ms.
 * I added it because i have devices with different vibration strength.
 */

public class VibrationStrengthDialog extends DialogPreference implements SeekBar.OnSeekBarChangeListener{

    private SeekBar mSeekBar;
    private TextView mTextView;

    public VibrationStrengthDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_vibration);
        setDialogIcon(null);
    }

    @Override
    protected void onBindDialogView(View view) {
        mTextView = (TextView) view.findViewById(R.id.textView);
        mSeekBar = (SeekBar) view.findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(this);

        int strength = savedData.getInt("prefKeyVibrationStrength",20);
        mSeekBar.setProgress(strength/10);
        setProgressText(strength);

        super.onBindDialogView(view);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        setProgressText(i*10);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int strength = mSeekBar.getProgress()*10;

        vibrate(strength);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        // When the user selects "OK", persist the new value
        if (positiveResult) {
            saveData("prefKeyVibrationStrength",mSeekBar.getProgress()*10);
        }
    }

    private void setProgressText(int value){
        if (value==0)
            mTextView.setText(mainActivity.getString(R.string.off));
        else
            mTextView.setText(String.format(Locale.getDefault(),"%s ms",value));
    }
}
