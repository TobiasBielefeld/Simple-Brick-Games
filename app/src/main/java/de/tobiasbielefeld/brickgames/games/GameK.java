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
 * Classic snake game: Eat the blinking dot to grow.
 * Next level is with 22 eaten dots
 */

public class GameK extends Game {

    private int mLength, mDirection;
    private int[] mFood = new int[2];
    private int[][] mSnake = new int[10 * 20][2];

    protected void onStart() {
        super.mScoreLimit=22;
        super.mLevelsEnabled=true;
        super.mTimerLimit=50;

        mDirection = 2;
        mLength = 4;

        for (int i = 1; i < mLength; i++) {
            mSnake[i][0] = 2;
            mSnake[i][1] = 12 - i;
        }

        mSnake[0][0] = 2;
        mSnake[0][1] = 11;

        foodCalculation();
    }

    protected void drawField() {
        for (int i = 2; i < mLength; i++)
            field[mSnake[i][0]][mSnake[i][1]] = 1;

        if (timerCounter2 % 20 < 10 && mSnake[0][0] >= 0 && mSnake[0][0] < FIELD_WIDTH && mSnake[0][1] >= 0 && mSnake[0][1] < FIELD_HEIGHT)
            field[mSnake[0][0]][mSnake[0][1]] = 3;

        if (timerCounter2 % 10 < 5)
            field[mFood[0]][mFood[1]] = 2;
    }

    protected void calculation() {
        if (input == 1 && mDirection != 2) {
            if (mSnake[0][1] > 0) {
                mSnake[0][1] -= 1;
                mDirection = 1;
            } else {
                explosion(mSnake[0][0], mSnake[0][1]);
            }
        } else if (input == 2 && mDirection != 1) {
            if (mSnake[0][1] < FIELD_HEIGHT - 1) {
                mSnake[0][1] += 1;
                mDirection = 2;
            } else {
                explosion(mSnake[0][0], mSnake[0][1]);
            }
        } else if (input == 3 && mDirection != 4) {
            if (mSnake[0][0] > 0) {
                mSnake[0][0] -= 1;
                mDirection = 3;
            } else {
                explosion(mSnake[0][0], mSnake[0][1]);
            }
        } else if (input == 4 && mDirection != 3) {
            if (mSnake[0][0] < FIELD_WIDTH - 1) {
                mSnake[0][0] += 1;
                mDirection = 4;
            } else {
                explosion(mSnake[0][0], mSnake[0][1]);
            }
        } else {
            if (input != 0)
                input = mDirection;

            return;
        }

        playSound(4);

        for (int i = mLength; i > 0; i--) {
            mSnake[i][0] = mSnake[i - 1][0];
            mSnake[i][1] = mSnake[i - 1][1];
        }

        for (Point point : sObjects)
            if (mSnake[0][0] == point.x && mSnake[0][1] == point.y)
                explosion(mSnake[0][0], mSnake[0][1]);

        for (int i = 2; i < mLength; i++)
            if (mSnake[0][0] == mSnake[i][0] && mSnake[0][1] == mSnake[i][1])
                explosion(mSnake[0][0], mSnake[0][1]);

        if (mSnake[0][0] == mFood[0] && mSnake[0][1] == mFood[1]) {
            foodCalculation();
            playSound(2);
            nextScore();
            mLength++;
        }

        timerCounter = 0;
    }

    private void foodCalculation() {
        boolean bError;

        do {
            bError = false;

            mFood[0] = rand.nextInt(FIELD_WIDTH);
            mFood[1] = rand.nextInt(FIELD_HEIGHT);

            for (int i = 0; i < mLength; i++)
                if (mFood[0] == mSnake[i][0] && mFood[1] == mSnake[i][1])
                    bError = true;

            for (Point point : sObjects)
                if (mFood[0] == point.x && mFood[1] == point.y)
                    bError = true;
        }
        while (bError);
    }

    public void input() {
        if (input == 5)
            input = mDirection;

        calculation();
    }

    protected void level() {
        sObjects.clear();
        int[][] array;
        int M = 1;

        switch (sLevel) {
            case 1: default:
                array = new int[][]{{}};
                break;
            case 2:
                array = new int[][]{
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,M,M,0,0,0,0,M,M,0},
                        {0,M,0,0,0,0,0,0,M,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,M,0,0,0,0,0,0,M,0},
                        {0,M,M,0,0,0,0,M,M,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0}};
                break;
            case 3:
                array = new int[][]{
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,M,M,0,0,0,0,M,M,0},
                        {0,M,0,0,0,0,0,0,M,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,M,M,0,0,0,0},
                        {0,0,0,0,M,M,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,M,0,0,0,0,0,0,M,0},
                        {0,M,M,0,0,0,0,M,M,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0}};
                break;
            case 4:
                array = new int[][]{
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,M,M},
                        {0,0,0,0,0,0,0,0,M,M},
                        {0,0,0,0,0,0,0,0,M,M},
                        {0,0,0,0,0,0,0,0,M,M},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,M,0,0,0,0,M,0,0},
                        {0,0,M,M,0,0,M,M,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0}};
                break;
            case 5:
                array = new int[][]{
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,M,M,0,0,M,M,0,0},
                        {0,0,M,0,0,0,0,M,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,M,M},
                        {0,0,0,0,0,0,0,0,M,M},
                        {0,0,0,0,0,0,0,0,M,M},
                        {0,0,0,0,0,0,0,0,M,M},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0}};
                break;
            case 6:
                array = new int[][]{
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,M,M,0,0,0,M,M,0},
                        {0,0,M,0,0,0,0,0,M,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,M,M},
                        {0,0,0,0,0,0,0,0,M,M},
                        {0,0,0,0,0,0,0,0,M,M},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,M,0,0,0,0,0,M,0},
                        {0,0,M,M,0,0,0,M,M,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0}};
                break;
            case 7:
                array = new int[][]{
                        {0,0,0,M,M,M,M,0,0,0},
                        {0,0,0,M,M,M,M,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,M,M,0,0,0,0},
                        {0,0,0,0,M,M,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,M,M,M,M,0,0,0},
                        {0,0,0,M,M,M,M,0,0,0}};
                break;
            case 8:
                array = new int[][]{
                        {M,M,M,M,M,M,M,M,M,M},
                        {M,M,M,M,M,M,M,M,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,M,M,0,0,0,0},
                        {0,0,0,0,M,M,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,M,M,M,M,M,M,M,M,M},
                        {M,M,M,M,M,M,M,M,M,M}};
                break;
            case 9:
                array = new int[][]{
                        {M,M,M,M,M,M,M,M,M,M},
                        {M,M,M,M,M,M,M,M,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,M,0,0,M,M,0,0,M,M},
                        {M,M,0,0,M,M,0,0,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,M,M,M,M,M,M,M,M,M},
                        {M,M,M,M,M,M,M,M,M,M}};
                break;
        }

        for (int i=0; i<array.length;i++)
            for (int j=0; j< array[i].length;j++)
                if (array[i][j]==1)
                    obj(j,i);
    }
}
