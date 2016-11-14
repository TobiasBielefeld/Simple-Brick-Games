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

package de.tobiasbielefeld.brickgames.helper;

import de.tobiasbielefeld.brickgames.classes.Game;

import static de.tobiasbielefeld.brickgames.SharedData.*;
import static de.tobiasbielefeld.brickgames.classes.Game.*;

/*
 * Draws fading between games and levels
 */

public class DrawFading {

    private boolean mPhase;
    private int mCounter;

    public void draw() {

        if (mPhase) {
            if (nextLevel)
                getCurrentGame().nextLevel();

            for (int i=0;i < FIELD_WIDTH;i++)
                field[i][mCounter] = 1;

            if (--mCounter < 0) {
                mCounter=0;
                mPhase=false;
            }

        } else {
            for (int i=0;i < FIELD_WIDTH;i++)
                field[i][mCounter] = 0;

            if (++mCounter >= FIELD_HEIGHT) {
                Game.initialisation();
                reset();
            }
        }
    }

    public void reset() {
        mCounter=FIELD_HEIGHT-1;
        mPhase=true;
    }
}
