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
 * This is some what like Breakout, but the player has to pass the blocking enemy
 * and get the ball to the top
 */

public class GameD extends Game {

    private int mPlayer;
    private int[] mBall = new int[2];
    private boolean[] mDirection = new boolean[3];
    private boolean mPlaySound;

    private int mEnemy;
    private int mEnemyDirection;

	protected void onStart() {
        super.mLevelsEnabled=true;
        super.mScoreLimit=10;
        super.mTimerLimit=25;

        mPlaySound = false;
        mPlayer = 3;
        mBall[0] = 4;
        mBall[1] = 18;
        mDirection[0] = false;
        mDirection[1] = false;
        mDirection[2] = false;
		mEnemyDirection=1;
	}

    protected void level() {
        sObjects.clear();
        int[][] array;
        int M = 1;

        switch (sLevel) {
            case 1:default:
                array = new int[][]{
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,0,0,0,0,0,0,0,0,M}};
                break;
            case 2:
                array = new int[][]{
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,0,0,0,0,0,0,0,0,M}};
                break;
            case 3:
                array = new int[][]{
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,0,0,0,0,0,0,0,0,M},
                        {M,0,0,0,0,0,0,0,0,M}};
                break;
            case 4:
                array = new int[][]{
                        {M,M,M,0,0,0,0,M,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,0,0,0,0,0,0,0,0,M}};
                break;
            case 5:
                array = new int[][]{
                        {M,M,M,0,0,0,0,M,M,M},
                        {M,M,M,0,0,0,0,M,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,0,0,0,0,0,0,0,0,M}};
                break;
            case 6:
                array = new int[][]{
                        {M,M,M,0,0,0,0,M,M,M},
                        {M,M,M,0,0,0,0,M,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,0,0,0,0,0,0,0,0,M}};
                break;
            case 7:
                array = new int[][]{
                        {M,M,M,0,0,0,0,M,M,M},
                        {M,M,M,0,0,0,0,M,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,0,0,0,0,0,0,0,0,M},
                        {M,0,0,0,0,0,0,0,0,M}};
                break;
            case 8:
                array = new int[][]{
                        {M,M,M,M,0,0,M,M,M,M},
                        {M,M,M,0,0,0,0,M,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,0,0,0,0,0,0,0,0,M}};
                break;
            case 9:
                array = new int[][]{
                        {M,M,M,M,0,0,M,M,M,M},
                        {M,M,M,M,0,0,M,M,M,M},
                        {M,M,M,0,0,0,0,M,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,0,0,0,0,0,0,0,0,M},
                        {M,0,0,0,0,0,0,0,0,M}};
                break;
        }

        for (int i=0; i<array.length;i++)
            for (int j=0; j< array[i].length;j++)
                if (array[i][j]==1)
                    obj(j,i);
	}

    protected void drawField() {
        for (int i = mPlayer; i < mPlayer + 4; i++)
            field[i][19] = 1;

        if (mBall[0]>=0 && mBall[1]>=0 && mBall[0]<FIELD_WIDTH && mBall[1]<FIELD_HEIGHT)
            field[mBall[0]][mBall[1]] = 1;

		for (int i=mEnemy;i < mEnemy + 3;i++)
			field[i][8] = 1;
	}

    protected void calculation() {

        if (mPlaySound) {
            playSound(4);
            mPlaySound=false;
        }

		if (mEnemyDirection==1) {
            if (mEnemy>2)
                mEnemy-=1;
            else {
                mEnemyDirection=2;
                mEnemy+=1;
            }
        } else {
            if (mEnemy<5)
                mEnemy+=1;
            else {
                mEnemyDirection=1;
                mEnemy-=1;
            }
        }

        if (!mDirection[0]) return;


        if (mBall[1] == 0) {
            nextScore();
            playSound(6);
            sEvent = 1;
        }

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

        if (mBall[1] == 19)
            explosion(mBall[0] - 1, mBall[1]);

        testCollision();
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

    private void testCollision() {
        ArrayList<Point> points = new ArrayList<>();

        for (Point point : sObjects)
            points.add(point);

        for (int i = mEnemy; i < mEnemy + 3; i++)
            points.add(new Point(i,8));

        for (Point point : points) {
            if (point.x == mBall[0] && point.y == mBall[1] - 1 && !mDirection[2]) {
                mPlaySound=true;
                mDirection[2] = !mDirection[2];
                testCollision();
            }
            if (point.x == mBall[0] - 1 && point.y == mBall[1] && !mDirection[1]) {
                mPlaySound=true;
                mDirection[1] = !mDirection[1];
                testCollision();
            }
            if (point.x == mBall[0] && point.y == mBall[1] + 1 && mDirection[2]) {
                mPlaySound=true;
                mDirection[2] = !mDirection[2];
                testCollision();
            }
            if (point.x == mBall[0] + 1 && point.y == mBall[1] && mDirection[1]) {
                mPlaySound=true;
                mDirection[1] = !mDirection[1];
                testCollision();
            }
        }

        for (Point point : points) {
            if ((!mDirection[1] && !mDirection[2] && point.x == mBall[0] - 1 && point.y == mBall[1] - 1)
                    || (!mDirection[1] && mDirection[2] && point.x == mBall[0] - 1 && point.y == mBall[1] + 1)
                    || (mDirection[1] && !mDirection[2] && point.x == mBall[0] + 1 && point.y == mBall[1] - 1)
                    || (mDirection[1] && mDirection[2] && point.x == mBall[0] + 1 && point.y == mBall[1] + 1)) {
                mPlaySound=true;
                mDirection[1] = !mDirection[1];
                mDirection[2] = !mDirection[2];
                collision();
                testCollision();
            }
        }

        points.clear();
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
        if (mBall[1] == 18 && mDirection[2]) {
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
}
