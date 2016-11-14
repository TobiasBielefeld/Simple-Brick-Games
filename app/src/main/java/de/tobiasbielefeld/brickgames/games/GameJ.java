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
 *  third shooting game: But the levels from game C appear and shoot back!
 *  So destroy every block for next level
 */

public class GameJ extends Game {

    private int mTimer;
    private boolean mPlayerIsShooting;
    private  Enemy enemy = new Enemy();
    private int mPlayer;
    private int[] iShoot = new int[2];
    private int[][] fieldS = new int[FIELD_WIDTH][FIELD_HEIGHT];

    protected void onStart() {
        super.mTimerLimit=150;
        super.mFastShootEnabled=true;

        mTimer = 0;
        mPlayer = 4;
        enemy.Enabled = false;
        mPlayerIsShooting = false;
        sShoot = true;

        for (int j = 0; j < FIELD_HEIGHT; j++)
            for (int i = 0; i < FIELD_WIDTH; i++)
                fieldS[i][j] = 0;

        for (Point point : sObjects)
            fieldS[point.x][point.y-6]=1;
    }

    protected void drawField() {
        for (int j = 0; j < FIELD_HEIGHT; j++)
            for (int i = 0; i < FIELD_WIDTH; i++)
                field[i][j] = fieldS[i][j];

        for (int i = 0; i < 3; i++)
            if (mPlayer - 1 + i >= 0 && mPlayer - 1 + i < 10)
                field[mPlayer - 1 + i][19] = 1;

        if (enemy.Enabled)
            field[enemy.Shoot[0]][enemy.Shoot[1]] = 1;

        if (mPlayerIsShooting)
            field[iShoot[0]][iShoot[1]] = 1;

        field[mPlayer][18] = 1;
    }

    private void calculateEnemyShoot() {

        for (int i = FIELD_HEIGHT - 1; i >= 0; i--)
            for (int j = 0; j < FIELD_WIDTH; j++)
                if (fieldS[j][i] != 0) {
                    fieldS[j][i] = 0;
                    enemy.Shoot[0] = j;
                    enemy.Shoot[1] = i + 1;
                    enemy.Enabled = true;
                    enemy.Direction = (j != 0 && (j == FIELD_WIDTH - 1 || rand.nextBoolean()));
                    return;
                }
    }

    public void shootCalculation() {
        if (mPlayerIsShooting) {

            if (fieldS[iShoot[0]][iShoot[1]] == 1) {

                playSound(5);
                fieldS[iShoot[0]][iShoot[1]] = 0;
                mPlayerIsShooting = false;
                nextScore();
                winCondition();
            }

            if (enemy.Enabled && iShoot[0] == enemy.Shoot[0] && iShoot[1] == enemy.Shoot[1]) {

                playSound(5);
                enemy.Enabled = false;
                mPlayerIsShooting = false;
                winCondition();
            }

            iShoot[1]--;

            if (iShoot[1] < 0)
                mPlayerIsShooting = false;
        }

        if (enemy.Enabled)
            if ((--mTimer) % 10 == 0) {
                enemy.Shoot[1]++;

                if (enemy.Direction) enemy.Shoot[0]--;
                else enemy.Shoot[0]++;

                if (enemy.Shoot[0] == 0 || enemy.Shoot[0] == FIELD_WIDTH - 1)
                    enemy.Direction = !enemy.Direction;

                if (enemy.Shoot[1] == FIELD_HEIGHT - 2 && enemy.Shoot[0] == mPlayer || enemy.Shoot[1] == FIELD_HEIGHT - 1 && enemy.Shoot[0] >= mPlayer - 1 && enemy.Shoot[0] <= mPlayer + 1)
                    explosion(mPlayer, FIELD_HEIGHT - 1);
                else if (enemy.Shoot[1] == FIELD_HEIGHT) {
                    enemy.Enabled = false;
                    winCondition();
                }
            }
    }

    private void winCondition() {
        if (enemy.Enabled || sEvent != 0)
            return;

        for (int j = 0; j < FIELD_HEIGHT; j++)
            for (int i = 0; i < FIELD_WIDTH; i++)
                if (fieldS[i][j] != 0)
                    return;

        nextLevel();
    }

    protected void calculation() {
        if (!enemy.Enabled)
            calculateEnemyShoot();

        for (int j = FIELD_HEIGHT - 1; j > 0; j--)
            for (int i = 0; i < FIELD_WIDTH; i++)
                fieldS[i][j] = fieldS[i][j - 1];

        for (int i = 0; i < FIELD_WIDTH; i++)
            fieldS[i][0] = 0;

        for (int i = 0; i < 10; i++)
            if (fieldS[i][18] == 1)
                explosion(i, 18);
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
                if (!mPlayerIsShooting) {
                    mPlayerIsShooting = true;
                    iShoot[0] = mPlayer;
                    iShoot[1] = 17;
                }
                break;
        }
    }

    private class Enemy {
        boolean Enabled;
        boolean Direction;
        int[] Shoot = new int[2];
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

