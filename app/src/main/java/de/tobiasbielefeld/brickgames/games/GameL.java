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
 * Like "Frogger", get to the other side and make rows. Fill the row for next level.
 * The streets can head left or right, set in the level
 */

public class GameL extends Game {

    private Point mPlayer;
    private int[][] mRow;
    private int[] mDirection;

    protected void onStart() {
        super.mTimerLimit=45;

        mPlayer = new Point(5,19);
    }

    protected void drawField() {
        for (int j = 0; j < 8; j++)
            for (int i = 0; i < 10; i++)
                if (mRow[j][i] == 1)
                    field[i][3 + 2 * j] = 1;

        for (int i = 2; i < 19; i += 2)
            for (int j = 0; j < FIELD_WIDTH; j++)
                field[j][i] = 1;

        for (Point point : sObjects) {
            field[point.x][point.y]=1;
        }

        if (field[mPlayer.x][mPlayer.y] == 0) {
            if (timerCounter2 % 20 < 10)
                field[mPlayer.x][mPlayer.y] = 1;
        }
    }

    protected void calculation() {
        for (int j = 0; j < 8; j++) {
            if (mDirection[j]==1) {
                int temp = mRow[j][15];
                System.arraycopy(mRow[j], 0, mRow[j], 1, 15);
                mRow[j][0] = temp;
            } else {
                int temp = mRow[j][0];
                System.arraycopy(mRow[j], 1, mRow[j], 0, 15);
                mRow[j][15] = temp;
            }
        }

        if (mPlayer.y != FIELD_HEIGHT - 1) {
            if ((mPlayer.y==1 || mDirection[(mPlayer.y-3)/2]==1) && mPlayer.x < FIELD_WIDTH - 1)
                mPlayer.x++;
            else if (mDirection[(mPlayer.y-3)/2]==0 && mPlayer.x >0)
                mPlayer.x--;
            else
                explosion(mPlayer.x, mPlayer.y);
        }

        if (sObjects.size() == FIELD_WIDTH)
            nextLevel();

        test();
    }

    public void input() {
        if (input != 5 && !(input == 1 && mPlayer.y == 1))
            playSound(4);

        switch (input) {
            case 1:
                if (mPlayer.y > 1)
                    mPlayer.y -= 2;
                else if (mPlayer.y > 0) {
                    mPlayer.y--;

                    if (field[mPlayer.x][mPlayer.y] == 1)
                        explosion(mPlayer.x, mPlayer.y);
                    else {
                        playSound(2);
                        obj(mPlayer.x, mPlayer.y);
                        nextScore();

                        mPlayer.x = 5;
                        mPlayer.y = 19;
                    }
                }
                break;
            case 2:
                if (mPlayer.y < FIELD_HEIGHT - 2)
                    mPlayer.y += 2;
                else if (mPlayer.y < FIELD_HEIGHT - 1)
                    mPlayer.y--;
                break;
            case 3:
                if (mPlayer.x > 0)
                    mPlayer.x--;
                break;
            case 4:
                if (mPlayer.x < FIELD_WIDTH - 1)
                    mPlayer.x++;
                break;
            case 5:
                calculation();
                break;
        }

        test();
    }

    private void test() {
        for (int j = 0; j < 8; j++)
            for (int i = 0; i < 10; i++)
                if (mRow[j][i] == 1 && mPlayer.x==i && mPlayer.y==3+2*j)
                    explosion(mPlayer.x, mPlayer.y);
    }

    protected void level() {
        sObjects.clear();
        int M=1;

        //Width is 16, height 8
        switch (sLevel) {
            case 1:default:
                mRow = new int[][]{
                        {0,0,M,M,0,0,0,0,0,M,M,M,M,0,0,0},
                        {0,0,0,M,M,0,0,0,0,0,M,M,M,M,0,0},
                        {M,0,0,0,0,M,M,0,0,0,0,0,M,M,M,M},
                        {0,0,0,M,0,0,0,0,M,M,0,0,0,0,M,M},
                        {M,0,0,0,0,M,M,M,M,0,0,0,0,M,M,M},
                        {M,M,M,0,0,0,0,M,M,M,M,0,0,0,0,M},
                        {M,0,0,0,0,M,M,M,M,0,0,0,0,M,M,M},
                        {M,M,0,0,0,0,0,M,M,M,0,0,0,0,0,M}};
                mDirection = new int[] {1,1,1,1,1,1,1,1};
                break;
            case 2:
                mRow = new int[][]{
                        {0,0,M,M,0,0,0,0,0,M,M,M,0,0,0,0},
                        {M,0,0,M,0,0,M,M,0,0,M,M,M,M,0,0},
                        {M,0,0,0,0,0,M,0,0,0,0,0,M,M,M,M},
                        {0,0,0,M,0,0,0,0,M,M,0,0,M,0,0,0},
                        {M,0,0,0,0,M,M,0,0,0,0,0,0,M,M,M},
                        {M,M,M,0,0,0,0,0,M,M,M,0,0,0,0,M},
                        {M,0,0,0,0,M,0,0,M,0,0,0,0,M,M,M},
                        {M,M,M,0,0,0,0,0,M,M,0,0,0,M,0,0}};
                mDirection = new int[] {1,1,1,0,1,1,1,1};
                break;
            case 3:
                mRow = new int[][]{
                        {0,0,M,M,0,0,0,0,0,M,M,M,0,0,0,0},
                        {M,0,0,M,0,0,M,M,0,0,M,M,M,M,0,0},
                        {M,0,M,M,0,0,0,0,M,M,M,0,0,0,0,M},
                        {0,0,0,M,0,0,0,0,M,M,0,0,0,0,0,0},
                        {M,0,0,0,0,M,M,0,0,M,M,M,0,0,0,M},
                        {M,M,M,0,0,0,0,0,M,M,M,0,0,0,0,M},
                        {M,0,0,0,0,M,0,0,M,0,0,0,0,M,M,M},
                        {M,M,M,0,0,0,0,0,M,M,0,0,0,M,0,0}};
                mDirection = new int[] {1,0,1,1,1,0,1,1};
                break;
            case 4:
                mRow = new int[][]{
                        {0,0,0,M,0,0,0,0,M,M,0,0,0,0,0,0},
                        {0,0,M,M,0,0,0,0,0,M,M,M,0,0,0,0},
                        {M,0,0,M,0,0,M,M,M,0,0,0,0,M,0,0},
                        {M,0,0,0,0,M,0,0,M,0,0,0,0,M,M,M},
                        {0,M,0,0,0,M,0,0,0,M,0,0,0,M,M,0},
                        {M,0,0,0,M,M,M,0,0,0,M,M,0,0,0,M},
                        {M,0,0,0,0,M,0,0,0,0,0,0,0,M,M,M},
                        {M,M,M,0,0,0,0,0,M,M,0,0,0,M,0,0}};
                mDirection = new int[] {1,1,0,1,0,1,0,1};
                break;
            case 5:
                mRow = new int[][]{
                        {M,0,0,0,0,M,0,0,M,0,0,0,0,M,M,M},
                        {0,0,M,M,0,M,M,M,0,M,M,M,0,0,0,0},
                        {M,0,0,M,0,0,0,0,M,0,0,0,0,M,0,0},
                        {M,0,0,0,0,M,0,0,M,0,0,0,0,M,M,M},
                        {0,M,0,0,0,M,0,0,0,M,0,0,0,M,M,0},
                        {0,0,0,M,0,0,0,0,M,M,0,0,M,M,M,0},
                        {0,0,M,M,0,0,M,0,0,M,M,M,0,0,0,0},
                        {M,0,0,M,0,0,M,M,M,0,0,0,0,M,0,0},};
                mDirection = new int[] {0,1,0,1,0,1,0,1};
                break;
            case 6:
                mRow = new int[][]{
                        {0,0,M,M,0,0,0,0,0,M,M,M,0,0,0,0},
                        {0,0,M,M,0,M,M,M,0,M,M,M,0,0,0,0},
                        {M,0,0,0,0,M,M,M,M,0,0,0,0,M,M,M},
                        {M,0,M,M,0,M,0,0,M,0,M,M,0,M,M,M},
                        {M,0,0,0,0,M,M,M,M,0,0,0,0,M,M,M},
                        {M,0,0,M,0,0,M,M,M,0,0,0,0,M,0,0},
                        {0,0,M,M,0,0,M,0,0,M,M,M,0,0,0,0},
                        {M,0,0,M,0,0,M,M,M,0,0,0,0,M,0,0},};
                mDirection = new int[] {0,0,1,0,0,1,1,0};
                break;
            case 7:
                mRow = new int[][]{
                        {M,M,M,0,0,0,0,0,M,M,M,0,0,0,0,M},
                        {0,0,M,M,0,M,0,0,0,M,M,M,0,0,0,0},
                        {M,0,0,M,0,0,M,0,0,0,0,0,0,M,0,0},
                        {M,0,0,0,0,0,M,M,M,M,M,M,M,M,M,M},
                        {M,M,0,0,0,M,0,0,M,0,0,0,M,M,M,M},
                        {M,0,0,0,0,M,0,0,M,0,0,0,0,M,M,M},
                        {M,0,0,M,0,0,M,M,0,0,M,M,0,M,0,0},
                        {M,M,M,0,0,0,0,0,M,M,0,0,0,M,0,0}};
                mDirection = new int[] {0,0,1,0,0,0,1,0};
                break;
            case 8://
                mRow = new int[][]{
                        {0,0,M,M,0,0,0,0,0,M,M,M,0,0,0,0},
                        {M,0,0,M,0,0,M,M,0,0,M,M,M,M,0,0},
                        {M,M,M,M,M,M,0,0,0,0,M,M,M,M,M,M},
                        {M,0,0,0,0,M,0,0,M,0,0,0,0,M,M,M},
                        {M,M,M,0,0,0,0,0,M,M,M,0,0,0,0,M},
                        {M,0,0,0,0,0,M,M,M,M,M,M,M,M,M,M},
                        {M,0,0,0,0,M,M,M,M,0,0,0,0,M,M,M},
                        {M,M,M,0,0,0,0,0,M,M,0,0,0,M,0,0}};
                mDirection = new int[] {0,0,0,0,1,1,0,0};
                break;
            case 9:
                mRow = new int[][]{
                        {M,M,M,M,M,0,0,0,M,M,M,M,M,M,M,M},
                        {M,0,0,M,0,0,M,0,0,0,M,M,M,M,0,0},
                        {M,M,M,M,M,M,0,0,0,0,M,M,M,M,M,M},
                        {M,0,0,0,0,M,0,0,M,0,0,0,0,M,M,M},
                        {M,M,M,0,0,0,0,0,M,M,M,0,0,0,0,M},
                        {M,M,M,M,M,0,0,0,M,M,M,M,M,M,M,M},
                        {0,M,0,0,0,M,0,0,0,M,0,0,0,M,M,0},
                        {M,M,M,0,0,0,0,0,M,M,0,0,0,M,0,0}};
                mDirection = new int[] {0,0,0,0,1,0,0,0};
                break;
        }
    }
}
