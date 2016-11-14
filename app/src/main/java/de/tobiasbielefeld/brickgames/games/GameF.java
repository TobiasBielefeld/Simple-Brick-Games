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
 * Racing Game 2! But with 3 rows
 */

public class GameF extends Game {

    private int mPlayerPosX;
    private int mBorder;
    private Point mPlayer;
    private int[] mHole = new int[5];
    private int[][] mCar;
    private Point[] mEnemy = new Point[10];

    protected void onStart() {
        super.mScoreLimit=100;
        super.mTimerLimit=20;
        super.mLevelsEnabled=true;

        sObjects.clear();

        mCar= new int[][]{
                {0,1,0,1},
                {1,1,1,0},
                {0,1,0,1}};

        mBorder = 0;
        mPlayerPosX = 2;
        mPlayer = new Point(3,16-sLevel);
        mHole[0] = 3;

        for (int i=0;i<mEnemy.length;i++)
            mEnemy[i] = new Point();

        for (int i = 0; i < 3; i++) {
            if (i > 0)
                mHole[i] = mHole[i - 1];

            if (rand.nextInt(2) == 0 && mHole[i] != 0)
                mHole[i] -= 3;
            else if (mHole[i] != 6)
                mHole[i] += 3;

            switch (mHole[i]) {
                case 0:
                    mEnemy[i].x = 3;
                    mEnemy[i + 3].x = 6;
                    break;
                case 3:
                    mEnemy[i].x = 0;
                    mEnemy[i + 3].x = 6;
                    break;
                case 6:
                    mEnemy[i].x = 0;
                    mEnemy[i + 3].x = 3;
                    break;
            }

            mEnemy[i].y = -5 - 9 * i;
            mEnemy[i + 3].y = mEnemy[i].y;
        }
    }

    @Override
    protected void level() {
        sObjects.clear();
    }

    protected void drawField() {
        for (int j = 0; j < 4; j++)
            for (int i = 0; i < 3; i++)
                if (mCar[i][j] == 1)
                    field[mPlayer.x + i][mPlayer.y + j] = 1;

        for (int k = 0; k < 6; k++)
            for (int j = 0; j < 4; j++)
                for (int i = 0; i < 3; i++)
                    if (mCar[i][j] == 1 && mEnemy[k].y + j >= 0 && mEnemy[k].y + j < FIELD_HEIGHT)
                        field[mEnemy[k].x + i][mEnemy[k].y + j] = 1;
    }

    protected void calculation() {
        sObjects.clear();

        mBorder = (mBorder + 1) % 4;

        for (int i = 19; i >= 0; i--) {
            if (mBorder != 3)
                obj(9, i);

            mBorder = (mBorder + 1) % 4;
        }

        test();

        for (int i = 0; i < 3; i++) {
            mEnemy[i].y++;
            mEnemy[i + 3].y++;

            if (mEnemy[i].y != 20) continue;

            if (i > 0) mHole[i] = mHole[i - 1];
            else mHole[i] = mHole[2];

            if (rand.nextInt(2) == 0 && mHole[i] != 0)
                mHole[i] -= 3;
            else if (mHole[i] != 6)
                mHole[i] += 3;

            switch (mHole[i]) {
                case 0:
                    mEnemy[i].x = 3;
                    mEnemy[i + 3].x = 6;
                    break;
                case 3:
                    mEnemy[i].x = 0;
                    mEnemy[i + 3].x = 6;
                    break;
                case 6:
                    mEnemy[i].x = 0;
                    mEnemy[i + 3].x = 3;
                    break;
            }

            mEnemy[i].y = -7;
            mEnemy[i + 3].y = mEnemy[i].y;
        }
    }

    private void test() {
        for (int i = 0; i < 6; i++) {
            if (i % 2 == 0 && mPlayer.y == mEnemy[i].y)
                nextScore();

            if (mPlayer.x == mEnemy[i].x && ((mPlayer.y >= mEnemy[i].y && mPlayer.y <= mEnemy[i].y + 3) || (mPlayer.y + 3 >= mEnemy[i].y && mPlayer.y <= mEnemy[i].y + 3)))
                explosion(mPlayer.x + 1, mPlayer.y + 2);
        }
    }

    public void input() {
        if (input == 3 && mPlayerPosX > 1) {
            playSound(4);
            mPlayerPosX--;
        } else if (input == 4 && mPlayerPosX < 3) {
            playSound(4);
            mPlayerPosX++;
        } else if (input == 5)
            calculation();

        switch (mPlayerPosX) {
            case 1:
                mPlayer.x = 0;
                break;
            case 2:
                mPlayer.x = 3;
                break;
            case 3:
                mPlayer.x = 6;
                break;
        }
    }
}
