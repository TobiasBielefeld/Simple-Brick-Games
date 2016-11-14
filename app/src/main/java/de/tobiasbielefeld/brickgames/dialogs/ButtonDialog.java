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

import de.tobiasbielefeld.brickgames.R;
import static de.tobiasbielefeld.brickgames.SharedData.*;

/*
 * custom dialog to pick a button color, because i wanted to show the button in it
 */

public class ButtonDialog extends DialogPreference implements View.OnClickListener{

    public ButtonDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_buttons);
        setDialogIcon(null);
    }

    @Override
    protected void onBindDialogView(View view) {
        view.findViewById(R.id.button1).setOnClickListener(this);
        view.findViewById(R.id.button2).setOnClickListener(this);
        view.findViewById(R.id.button3).setOnClickListener(this);
        view.findViewById(R.id.button4).setOnClickListener(this);
        view.findViewById(R.id.button5).setOnClickListener(this);

        super.onBindDialogView(view);
    }

    public void onClick(View v) {
        int choice=1;

        switch (v.getId()) {
            case R.id.button1:
                choice=1;
                break;
            case R.id.button2:
                choice=2;
                break;
            case R.id.button3:
                choice=3;
                break;
            case R.id.button4:
                choice=4;
                break;
            case R.id.button5:
                choice=5;
                break;
        }
        saveData("prefKeyButtons",choice);
        getDialog().dismiss();
    }
}
