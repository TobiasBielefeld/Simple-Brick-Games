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

package de.tobiasbielefeld.brickgames.games;

import de.tobiasbielefeld.brickgames.classes.Game;

import static de.tobiasbielefeld.brickgames.SharedData.*;

/*
 * Shoot Game: Blocks are randomly generated on top and just shoot them all.
 * If one block hit the player y coordinate, the game is lost
 */

public class GameH extends Game {
    private int mPlayer;
    private int[] iShoot = new int[2];
    private int[][] fieldS = new int[FIELD_WIDTH][FIELD_HEIGHT];

    protected void onStart() {
        super.mScoreLimit=100;
        super.mTimerLimit=130;
        super.mFastShootEnabled=true;

        mPlayer = 4;

        for (int j = 0; j < FIELD_HEIGHT; j++)
            for (int i = 0; i < FIELD_WIDTH; i++)
                fieldS[i][j] = 0;

        for (int j = 0; j < sLevel; j++)
            for (int i = 0; i < 10; i++)
                if (rand.nextBoolean())
                    fieldS[i][j] = 1;
    }

    protected void drawField() {
        for (int j = 0; j < FIELD_HEIGHT; j++)
            for (int i = 0; i < FIELD_WIDTH; i++)
                field[i][j] = fieldS[i][j];

        for (int i = 0; i < 3; i++)
            if (mPlayer - 1 + i >= 0 && mPlayer - 1 + i < 10)
                field[mPlayer - 1 + i][19] = 1;

        field[mPlayer][18] = 1;

        if (sShoot)
            field[iShoot[0]][iShoot[1]] = 1;
    }

    protected void calculation() {
        for (int j = FIELD_HEIGHT - 1; j > 0; j--)
            for (int i = 0; i < FIELD_WIDTH; i++)
                fieldS[i][j] = fieldS[i][j - 1];

        for (int i = 0; i < 10; i++)
            if (rand.nextBoolean())
                fieldS[i][0] = 1;
            else
                fieldS[i][0] = 0;

        for (int i = 0; i < 10; i++)
            if (fieldS[i][18] == 1)
                explosion(i, 18);
    }

    public void shootCalculation() {

        if (iShoot[1] < 1)
            sShoot = false;

        if (fieldS[iShoot[0]][iShoot[1]] == 1) {
            playSound(5);
            fieldS[iShoot[0]][iShoot[1]] = 0;
            sShoot = false;
            nextScore();
        }

        iShoot[1]--;
    }

    public void input() {
        switch (input) {
            case 3:
                if (mPlayer > 0)
                    mPlayer--;
                break;
            case 4:
                if (mPlayer < 9)
                    mPlayer++;
                break;
            case 5:
                if (!sShoot) {
                    sShoot = true;
                    iShoot[0] = mPlayer;
                    iShoot[1] = 17;
                }
                break;
        }
    }
}

