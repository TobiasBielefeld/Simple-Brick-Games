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

import static de.tobiasbielefeld.brickgames.classes.Game.*;

/**
 * just draws the "game over animation" and contains all related variables
 */

public class DrawGameOver {

    private int x;
    private int y;
    private int mDirection;
    private int mBorderX;
    private int mBorderY;

    public void reset() {
        x=0;
        y=0;
        mDirection=1;
        mBorderX = FIELD_WIDTH - 1;
        mBorderY = FIELD_HEIGHT -1;
    }

    public void draw(){

        if (x==0 && y==0)
            playSound(7);

        field[x][y] = 1;

        switch (mDirection) {
            case 1:
                x++;

                if (x == mBorderX) {
                    mDirection = 2;
                    mBorderX--;
                }
                break;
            case 2:
                y++;

                if (y == mBorderY) {
                    mDirection = 3;
                    mBorderY--;
                }
                break;
            case 3:
                x--;

                if (x <= FIELD_WIDTH - mBorderX - 2)
                    mDirection = 4;
                break;
            case 4:
                y--;

                if (y <= FIELD_HEIGHT - mBorderY - 1)
                    mDirection = 1;

                if (x == 4 && y == 5)
                    Game.reset();
                break;
        }
    }
}
