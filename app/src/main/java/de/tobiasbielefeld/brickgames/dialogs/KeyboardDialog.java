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
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

import de.tobiasbielefeld.brickgames.R;

import static de.tobiasbielefeld.brickgames.SharedData.buttonKeyCodes;
import static de.tobiasbielefeld.brickgames.SharedData.logText;
import static de.tobiasbielefeld.brickgames.SharedData.saveData;
import static de.tobiasbielefeld.brickgames.SharedData.savedData;

/*
 * custom dialog to pick a button color, because i wanted to show the button in it
 */

public class KeyboardDialog extends DialogPreference implements View.OnClickListener, View.OnKeyListener{

    private ArrayList<EditText>  editTexts = new ArrayList<>();
    private int[] keyCodes = new int[8];

    public KeyboardDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_keyboard);
        setDialogIcon(null);
    }

    @Override
    protected void onBindDialogView(View view) {
        String[] keyNames = new String[8];

        editTexts.clear();
        editTexts.add((EditText) view.findViewById(R.id.editText0));
        editTexts.add((EditText) view.findViewById(R.id.editText1));
        editTexts.add((EditText) view.findViewById(R.id.editText2));
        editTexts.add((EditText) view.findViewById(R.id.editText3));
        editTexts.add((EditText) view.findViewById(R.id.editText4));
        editTexts.add((EditText) view.findViewById(R.id.editText5));
        editTexts.add((EditText) view.findViewById(R.id.editText6));
        editTexts.add((EditText) view.findViewById(R.id.editText7));

        keyNames[0] = savedData.getString("buttonUpText","W");
        keyNames[1] = savedData.getString("buttonDownText","S");
        keyNames[2] = savedData.getString("buttonLeftText","A");
        keyNames[3] = savedData.getString("buttonRightText","D");
        keyNames[4] = savedData.getString("buttonActionText","L");
        keyNames[5] = savedData.getString("buttonCloseText","X");
        keyNames[6] = savedData.getString("buttonResetText","R");
        keyNames[7] = savedData.getString("buttonPauseText","P");

        for (int i=0;i<8;i++){
            editTexts.get(i).setOnKeyListener(this);
            editTexts.get(i).setText(keyNames[i]);
            keyCodes[i] = buttonKeyCodes[i];
        }

        super.onBindDialogView(view);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult){
            saveData("buttonUpText",editTexts.get(0).getText().toString());
            saveData("buttonDownText",editTexts.get(1).getText().toString());
            saveData("buttonLeftText",editTexts.get(2).getText().toString());
            saveData("buttonRightText",editTexts.get(3).getText().toString());
            saveData("buttonActionText",editTexts.get(4).getText().toString());
            saveData("buttonCloseText",editTexts.get(5).getText().toString());
            saveData("buttonResetText",editTexts.get(6).getText().toString());
            saveData("buttonPauseText",editTexts.get(7).getText().toString());

            System.arraycopy(keyCodes, 0, buttonKeyCodes, 0, 8);

            saveData("buttonUp",keyCodes[0]);
            saveData("buttonDown",keyCodes[1]);
            saveData("buttonLeft",keyCodes[2]);
            saveData("buttonRight",keyCodes[3]);
            saveData("buttonAction",keyCodes[4]);
            saveData("buttonClose",keyCodes[5]);
            saveData("buttonReset",keyCodes[6]);
            saveData("buttonPause",keyCodes[7]);
        }
    }

    public void onClick(View v) {

        //getDialog().dismiss();
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            String pressedKey = String.valueOf((char) keyEvent.getUnicodeChar());
            EditText pressed = (EditText) view;
            int index = editTexts.indexOf(pressed);

            pressed.setText(pressedKey);
            keyCodes[index]=keyEvent.getKeyCode();
        }

        return true;
    }
}
