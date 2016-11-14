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

import android.graphics.Point;

import de.tobiasbielefeld.brickgames.classes.Game;

import static de.tobiasbielefeld.brickgames.SharedData.*;

/*
 * Another Racing game, but in a tunnel
 */

public class GameG extends Game {
    private Point mPlayer;
    private int mCounter, mCounter2;
    private int mBorder;
    private int[] mRow = new int[20];
    private int[][] mCar = new int[3][4];

    protected void onStart() {
        super.mScoreLimit=100;
        super.mTimerLimit=25;
        super.mLevelsEnabled=true;

        mCar= new int[][]{
                {0,1,0,1},
                {1,1,1,0},
                {0,1,0,1}};

        mPlayer = new Point(3,16-sLevel);
        mCounter = 0;
        mCounter2 = 0;

        mBorder = 11 - sLevel;


        for (int i = 0; i < FIELD_HEIGHT; i++)
            mRow[i] = 2;
    }

    @Override
    protected void level() {
        sObjects.clear();
    }

    protected void drawField() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 4; j++)
                if (mCar[i][j] == 1)
                    field[mPlayer.x + i][mPlayer.y + j] = 1;

        for (int i = 0; i < FIELD_HEIGHT; i++)
            for (int j = 0; j < FIELD_WIDTH; j++)
                if (j < mRow[i] || j > mRow[i] + 4)
                    field[j][i] = 1;
    }

    protected void calculation() {
        System.arraycopy(mRow, 0, mRow, 1, FIELD_HEIGHT - 1);

        if (mCounter < mBorder) {
            mCounter++;
            mRow[0] = mRow[1];
        } else {
            mCounter = 0;
            if (rand.nextBoolean()) {
                mRow[0] = mRow[1] + 1;

                if (mRow[0] > 4)
                    mRow[0] = 4;
            } else {
                mRow[0] = mRow[1] - 1;

                if (mRow[0] < 1)
                    mRow[0] = 1;
            }
        }

        mCounter2 = ++mCounter2 % 10;

        if (mCounter2 == 0) {
            nextScore();
            mBorder = 11 - sLevel;
        }

        test();
    }

    public void input() {
        if (input == 3 && mPlayer.x > 0) {
            playSound(4);
            mPlayer.x--;

        } else if (input == 4 && mPlayer.x < 7) {
            playSound(4);
            mPlayer.x++;
        } else if (input == 5)
            calculation();

        test();
    }

    private void test() {
        if (mRow[mPlayer.y] == mPlayer.x + 2 || mRow[mPlayer.y] + 5 == mPlayer.x + 1)
            explosion(mPlayer.x, mPlayer.y);

        for (int i = 1; i < 4; i++) {
            if (mRow[mPlayer.y + i] > mPlayer.x || mRow[mPlayer.y + i] + 5 < mPlayer.x + 3) {
                explosion(mPlayer.x, mPlayer.y);
            }
        }
    }
}
