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

import java.util.ArrayList;

import de.tobiasbielefeld.brickgames.classes.*;
import static de.tobiasbielefeld.brickgames.SharedData.*;

/*
 * This is also like Breakout (Game B) but with two pads to control
 */

public class GameC extends Game {

    private int mPlayer;
    private ArrayList<Point> mBlockAway = new ArrayList<>();
    private int[] mBall = new int[2];
    private boolean[] mDirection = new boolean[3];
    private boolean mPlaySound;

	protected void onStart() {
        super.mLevelsEnabled=true;
        super.mTimerLimit=25;

        mPlaySound = false;
        mPlayer = 3;
        mBall[0] = 4;
        mBall[1] = 18;
        mDirection[0] = false;
        mDirection[1] = false;
        mDirection[2] = false;
        mBlockAway.clear();
	}

	protected void drawField() {
        for (int i = mPlayer; i < mPlayer + 4; i++)
            field[i][19] = 1;

        if (mBall[0]>=0 && mBall[1]>=0 && mBall[0]<FIELD_WIDTH && mBall[1]<FIELD_HEIGHT)
            field[mBall[0]][mBall[1]] = 1;

        for (int i=mPlayer;i < mPlayer + 4;i++)
            field[i][0] = 1;
	}

    protected void calculation() {
        if (!mDirection[0]) return;


        if (!mBlockAway.isEmpty()) {
            for (Point point : mBlockAway) {
                sObjects.remove(point);
                nextScore();
            }

            playSound(5);
            mBlockAway.clear();
        }

        if (mPlaySound) {
            playSound(4);
            mPlaySound=false;
        }

        if (sObjects.size()==0)
            nextLevel();

        if (mDirection[1])
            mBall[0]++;
        else
            mBall[0]--;

        if (mDirection[2])
            mBall[1]++;
        else
            mBall[1]--;

        collision();

        playerCollision();

        if (mBall[1] == 19 || mBall[1] == 0)
            explosion(mBall[0] - 1, mBall[1]);

        testcollision();
	}

    public void input() {
        if (input == 3 && mPlayer > 0) {
            if (mDirection[0] && mBall[1] == 18 && mBall[0] >= mPlayer && mBall[0] <= mPlayer + 3) {
                if (mBall[0] > 0)
                    mBall[0]--;

                mDirection[1] = false;
                collision();
            }

            mPlayer--;

            if (!mDirection[0]) {
                mBall[0]--;
                mDirection[1] = false;
            }
        } else if (input == 4 && mPlayer < 6) {
            if (mDirection[0] && mBall[1] == 18 && mBall[0] >= mPlayer && mBall[0] <= mPlayer + 3) {
                if (mBall[0] != 9)
                    mBall[0]++;

                mDirection[1] = true;
                collision();
            }

            mPlayer++;

            if (!mDirection[0]) {
                mBall[0]++;
                mDirection[1] = true;
            }
        } else if (input == 5) {
            if (!mDirection[0])
                mDirection[0] = true;


            calculation();
        }

        playerCollision();
	}

    private void testcollision() {

        for (Point point : sObjects) {
            if (point.x == mBall[0] && point.y == mBall[1] - 1 && !mDirection[2]) {
                mBlockAway.add(point);
                mDirection[2] = !mDirection[2];
                testcollision();
            }
            if (point.x == mBall[0] - 1 && point.y == mBall[1] && !mDirection[1]) {
                mBlockAway.add(point);
                mDirection[1] = !mDirection[1];
                testcollision();
            }
            if (point.x == mBall[0] && point.y == mBall[1] + 1 && mDirection[2]) {
                mBlockAway.add(point);
                mDirection[2] = !mDirection[2];
                testcollision();
            }
            if (point.x == mBall[0] + 1 && point.y == mBall[1] && mDirection[1]) {
                mBlockAway.add(point);
                mDirection[1] = !mDirection[1];
                testcollision();
            }
        }

        for (Point point : sObjects) {
            if ((!mDirection[1] && !mDirection[2] && point.x == mBall[0] - 1 && point.y == mBall[1] - 1)
                    || (!mDirection[1] && mDirection[2] && point.x == mBall[0] - 1 && point.y == mBall[1] + 1)
                    || (mDirection[1] && !mDirection[2] && point.x == mBall[0] + 1 && point.y == mBall[1] - 1)
                    || (mDirection[1] && mDirection[2] && point.x == mBall[0] + 1 && point.y == mBall[1] + 1)) {
                mBlockAway.add(point);
                mDirection[1] = !mDirection[1];
                mDirection[2] = !mDirection[2];
                collision();
                testcollision();
            }
        }
    }

    private void collision() {
        if (mBall[0] == 0 || mBall[0] == 9) {
            mDirection[1] = !mDirection[1];
            mPlaySound=true;
        }

        if (mBall[1] == 0) {
            mDirection[2] = !mDirection[2];
            mPlaySound=true;
        }
    }

    private void playerCollision() {
        if ((mBall[1] == 18 && mDirection[2]) || (mBall[1] == 1 && !mDirection[2])) {
            mPlaySound=true;
            if (mBall[0] >= mPlayer && mBall[0] <= mPlayer + 3)
                mDirection[2] = !mDirection[2];
            else if ((mDirection[1] && mBall[0] + 1 == mPlayer) || (!mDirection[1] && mBall[0] - 1 == mPlayer + 3)) {
                mDirection[1] = !mDirection[1];
                mDirection[2] = !mDirection[2];
                collision();
            }
        }
    }

    protected void level() {
        sObjects.clear();
        int[][] array;
        int M = 1;

        switch (sLevel) {
            case 1:default:
                array = new int[][]{
                        {0,M,M,0,0,0,M,M,0,0},
                        {M,0,0,M,0,M,0,0,M,0},
                        {M,0,0,0,M,0,0,0,M,0},
                        {0,M,0,0,0,0,0,M,0,0},
                        {0,0,M,0,0,0,M,0,0,0},
                        {0,0,0,M,M,M,0,0,0,0}};
                break;
            case 2:
                array = new int[][]{
                        {0,M,M,0,0,0,0,M,M,0},
                        {M,M,M,0,0,0,0,M,M,M},
                        {M,M,M,M,M,M,M,M,M,M},
                        {M,M,M,M,M,M,M,M,M,M},
                        {M,M,M,0,0,0,0,M,M,M},
                        {0,M,M,0,0,0,0,M,M,0}};
                break;
            case 3:
                array = new int[][]{
                        {M,M,M,M,M,M,M,M,0,0},
                        {M,0,0,0,0,0,0,M,M,M},
                        {M,0,0,0,0,0,0,M,0,M},
                        {M,M,0,0,0,M,M,M,0,M},
                        {M,M,0,0,0,M,M,M,M,M},
                        {0,M,M,M,M,0,0,0,0,0}};
                break;
            case 4:
                array = new int[][]{
                        {M,M,M,M,0,0,0,0,0,0},
                        {0,0,0,M,M,0,0,0,0,0},
                        {M,M,M,M,M,M,M,M,M,M},
                        {0,0,0,M,M,0,0,0,0,0},
                        {M,M,M,M,0,0,0,0,0,0}};
                break;
            case 5:
                array = new int[][]{
                        {0,0,M,M,0,0,0,M,M,0},
                        {0,M,0,0,M,0,M,0,0,M},
                        {M,0,0,M,0,M,0,M,0,0},
                        {M,0,0,0,0,M,0,0,0,0},
                        {0,M,0,0,M,0,M,0,0,M},
                        {0,0,M,M,0,0,0,M,M,0}};
                break;
            case 6:
                array = new int[][]{
                        {0,0,0,M,M,M,M,0,0,0},
                        {M,M,M,0,M,0,0,M,M,M},
                        {M,M,M,0,0,M,0,M,M,M},
                        {M,M,M,0,0,M,0,M,M,M},
                        {0,0,0,M,M,M,M,0,0,0},
                        {0,0,0,0,M,M,0,0,0,0}};
                break;
            case 7:
                array = new int[][]{
                        {0,M,M,M,M,M,M,M,M,M},
                        {M,M,0,M,0,0,0,M,0,M},
                        {M,M,0,M,M,M,M,M,0,M},
                        {M,M,0,0,0,0,0,0,0,M},
                        {M,M,0,0,0,0,0,0,0,M},
                        {0,M,M,M,M,M,M,M,M,M}};
                break;
            case 8:
                array = new int[][]{
                        {0,M,M,M,M,M,M,M,M,0},
                        {0,0,M,M,M,M,M,M,0,0},
                        {0,0,0,M,M,M,M,0,0,0},
                        {0,0,M,M,M,M,M,M,0,0},
                        {0,M,M,0,0,0,0,M,M,0},
                        {M,M,0,0,0,0,0,0,M,M}};
                break;
            case 9:
                array = new int[][]{
                        {0,0,M,M,M,M,M,M,0,0},
                        {0,M,M,0,M,M,0,M,M,0},
                        {M,M,M,M,M,M,M,M,M,M},
                        {M,M,M,M,0,0,M,M,M,M},
                        {0,0,M,M,M,M,M,M,0,0},
                        {0,M,0,M,0,0,M,0,M,0}};
                break;
        }

        for (int i=0; i<array.length;i++)
            for (int j=0; j< array[i].length;j++)
                if (array[i][j]==1)
                    obj(j,6+i);
    }
}
