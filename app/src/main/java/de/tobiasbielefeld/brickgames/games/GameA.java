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
import de.tobiasbielefeld.brickgames.classes.Game;

import static de.tobiasbielefeld.brickgames.SharedData.*;

/*
 * I would call this game "Tank!"
 *
 * The player tank has to shoot enemy tanks to get to the next level, sounds simple.
 *
 */
public class GameA extends Game {

    private boolean mTry = false;
    private int[][] mTank1 = new int[3][3], mTank2 = new int[3][3], mTank3 = new int[3][3], mTank4 = new int[3][3];
    private int[][] mEnemy1 = new int[3][3], mEnemy2 = new int[3][3], mEnemy3 = new int[3][3], mEnemy4 = new int[3][3];
    private Tank mTank = new Tank();
    private Dummy dummy = new Dummy();
    private Shoot shoot = new Shoot();
    private ArrayList<Point> toRemove = new ArrayList<>();

    protected void onStart() {
        super.mLevelsEnabled=true;
        super.mScoreLimit=20;
        super.mTimerLimit=50;

        tankCalculation();

        mTank.X[4] = FIELD_WIDTH / 2 - 2;
        mTank.Y[4] = FIELD_HEIGHT / 2 + 1;
        mTank.R[4] = 1;
        mTank.E[4] = true;
        toRemove.clear();

        for (int j = 0; j < 3; j++)
            for (int i = 0; i < 3; i++)
                mTank.A[4][i][j] = mTank1[i][j];

        for (int i = 0; i < 4; i++) mTank.E[i] = false;
        for (int i = 0; i < 5; i++) shoot.E[i] = false;
    }

    private void tankCalculation() {
        mTank1 = new int[][]{
                {0,1,1},
                {1,1,1},
                {0,1,1}};
        mTank2 = new int[][]{
                {1,1,0},
                {1,1,1},
                {1,1,0}};
        mTank3 = new int[][]{
                {0,1,0},
                {1,1,1},
                {1,1,1}};
        mTank4 = new int[][]{
                {1,1,1},
                {1,1,1},
                {0,1,0}};

        mEnemy1 = new int[][]{
                {0,1,1},
                {1,1,0},
                {0,1,1}};
        mEnemy2 = new int[][]{
                {1,1,0},
                {0,1,1},
                {1,1,0}};
        mEnemy3 = new int[][]{
                {0,1,0},
                {1,1,1},
                {1,0,1}};
        mEnemy4 = new int[][]{
                {1,0,1},
                {1,1,1},
                {0,1,0}};
    }

    public void input() {
        movement(4, input);
    }

    protected void drawField() {

        for (int k = 0; k < 5; k++) {
            if (shoot.E[k])
                field[shoot.X[k]][shoot.Y[k]] = 7;

            if (mTank.E[k])
                for (int j = 0; j < 3; j++)
                    for (int i = 0; i < 3; i++)
                        if (mTank.A[k][i][j] == 1)
                            field[mTank.X[k] + i][mTank.Y[k] + j] = k + 2;
        }
    }

    protected void calculation() {
        spawnEnemies();

        for (int k = 0; k < 4; k++)
            movement(k, rand.nextInt(20) + 1);
    }

    private void spawnEnemies() {
        int iRND = rand.nextInt(10);
        if (iRND > 3 || mTank.E[iRND]) return;

        switch (rand.nextInt(4)) {
            case 0:
                mTank.X[iRND] = 0;
                mTank.Y[iRND] = 0;
                break;
            case 1:
                mTank.X[iRND] = FIELD_WIDTH - 3;
                mTank.Y[iRND] = FIELD_HEIGHT - 3;
                break;
            case 2:
                mTank.X[iRND] = 0;
                mTank.Y[iRND] = FIELD_HEIGHT - 3;
                break;
            case 3:
                mTank.X[iRND] = FIELD_WIDTH - 3;
                mTank.Y[iRND] = 0;
                break;
        }

        for (int j = 0; j < 3; j++)
            for (int i = 0; i < 3; i++)
                if (mEnemy1[i][j] == 1 && field[mTank.X[iRND] + i][mTank.Y[iRND] + j] != 0)
                    return;

        for (int j = 0; j < 3; j++)
            for (int i = 0; i < 3; i++)
                mTank.A[iRND][i][j] = mEnemy1[i][j];

        mTank.E[iRND] = true;
        mTank.R[iRND] = 1;
    }

    private void movement(int k, int input) {
        if (!mTank.E[k]) return;

        dummy.X = mTank.X[k];
        dummy.Y = mTank.Y[k];

        switch (input) {
            case 1:
                for (int j = 0; j < 3; j++)
                    for (int i = 0; i < 3; i++)
                        dummy.A[i][j] = (k == 4 ? mTank1[i][j] : mEnemy1[i][j]);

                if (dummy.Y > 0 && mTank.R[k] == input)
                    dummy.Y--;

                if (mTry && dummy.Y > 0)
                    dummy.Y--;

                break;
            case 2:
                for (int j = 0; j < 3; j++)
                    for (int i = 0; i < 3; i++)
                        dummy.A[i][j] = (k == 4 ? mTank2[i][j] : mEnemy2[i][j]);

                if (dummy.Y < FIELD_HEIGHT - 3 && mTank.R[k] == input)
                    dummy.Y++;

                if (mTry && dummy.Y < FIELD_HEIGHT - 3)
                    dummy.Y++;

                break;
            case 3:
                for (int j = 0; j < 3; j++)
                    for (int i = 0; i < 3; i++)
                        dummy.A[i][j] = (k == 4 ? mTank3[i][j] : mEnemy3[i][j]);

                if (dummy.X > 0 && mTank.R[k] == input)
                    dummy.X--;

                if (mTry && dummy.X > 0)
                    dummy.X--;

                break;
            case 4:
                for (int j = 0; j < 3; j++)
                    for (int i = 0; i < 3; i++)
                        dummy.A[i][j] = (k == 4 ? mTank4[i][j] : mEnemy4[i][j]);

                if (dummy.X < FIELD_WIDTH - 3 && mTank.R[k] == input)
                    dummy.X++;

                if (mTry && dummy.X < FIELD_WIDTH - 3)
                    dummy.X++;

                break;
            case 5:
                if (!shoot.E[k]) {
                    if (k == 4) playSound(8);

                    shoot.R[k] = mTank.R[k];
                    shoot.E[k] = true;
                    sShoot = true;

                    switch (shoot.R[k]) {
                        case 1:
                            shoot.X[k] = mTank.X[k] + 1;
                            shoot.Y[k] = mTank.Y[k];
                            break;
                        case 2:
                            shoot.X[k] = mTank.X[k] + 1;
                            shoot.Y[k] = mTank.Y[k] + 2;
                            break;
                        case 3:
                            shoot.X[k] = mTank.X[k];
                            shoot.Y[k] = mTank.Y[k] + 1;
                            break;
                        case 4:
                            shoot.X[k] = mTank.X[k] + 2;
                            shoot.Y[k] = mTank.Y[k] + 1;
                            break;
                    }
                }
                break;
            case 10:case 11:case 12:case 13:case 14:case 15:case 16:case 17:case 18:case 19:
                movement(k, mTank.R[k]);
                return;
        }

        if (input < 5) {
            boolean bError = false;
            //drawFieldContentSwitch();

            for (int j = 0; j < 3; j++)
                for (int i = 0; i < 3; i++)
                    if (dummy.A[i][j] == 1 && field[dummy.X + i][dummy.Y + j] != 0 && field[dummy.X + i][dummy.Y + j] != k + 2)
                        bError = true;

            if (bError && !mTry) {
                mTry = true;
                movement(k, input);
            } else if (!bError) {
                mTank.X[k] = dummy.X;
                mTank.Y[k] = dummy.Y;
                mTank.R[k] = input;
                for (int j = 0; j < 3; j++)
                    for (int i = 0; i < 3; i++) mTank.A[k][i][j] = dummy.A[i][j];
            }
        }

        if (mTry)
            mTry = false;
    }

    public void shootCalculation() {
        for (int k = 0; k < 5; k++) {
            if (!shoot.E[k]) continue;

            switch (shoot.R[k]) {
                case 1:
                    if (shoot.Y[k] > 0) shoot.Y[k]--;
                    else shoot.E[k] = false;
                    break;
                case 2:
                    if (shoot.Y[k] < FIELD_HEIGHT - 1) shoot.Y[k]++;
                    else shoot.E[k] = false;
                    break;
                case 3:
                    if (shoot.X[k] > 0) shoot.X[k]--;
                    else shoot.E[k] = false;
                    break;
                case 4:
                    if (shoot.X[k] < FIELD_WIDTH - 1) shoot.X[k]++;
                    else shoot.E[k] = false;
                    break;
            }

            for (int l = 0; l < 5; l++) {
                if (mTank.E[l]) {
                    for (int j = 0; j < 3; j++) {
                        for (int i = 0; i < 3; i++) {
                            if (shoot.E[k] && shoot.X[k] == mTank.X[l] + i && shoot.Y[k] == mTank.Y[l] + j && mTank.A[l][i][j] == 1 && !(l == 4 && k == 4)) {
                                shoot.setShoot_false(k);

                                if (l == 4) {
                                    explosion(mTank.X[4], mTank.Y[4]);
                                    return;
                                }

                                if (k == 4) {
                                    playSound(5);
                                    mTank.E[l] = false;
                                    nextScore();
                                }
                            }
                        }
                    }
                }
            }

            for (int l = 0; l < 5; l++) {
                if (k != l && shoot.E[k] && shoot.E[l] && shoot.X[k] == shoot.X[l] && shoot.Y[l] == shoot.Y[k]) {
                    shoot.setShoot_false(l);
                    shoot.setShoot_false(k);
                }
            }

            for (Point point : sObjects) {
                if (shoot.E[k] && shoot.X[k] == point.x && shoot.Y[k] == point.y) {
                    if (k == 4) playSound(5);
                    shoot.setShoot_false(k);
                    toRemove.add(point);

                    //sObjects.remove(point);
                }
            }

            for (Point point : toRemove) {
                sObjects.remove(point);
            }

            toRemove.clear();

        }
    }

    private class Tank {
        int[] X = new int[5];
        int[] Y = new int[5];
        boolean[] E = new boolean[5];
        int[] R = new int[5];
        int[][][] A = new int[5][3][3];
    }

    private class Dummy {
        int X;
        int Y;
        int[][] A = new int[3][3];
    }

    private class Shoot {
        int[] X = new int[5];
        int[] Y = new int[5];
        boolean[] E = new boolean[5];
        int[] R = new int[5];

        void setShoot_false(int pX) {
            E[pX] = false;

            for (int i = 0; i < 5; i++)
                if (E[i])
                    return;

            sShoot = false;
        }
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
                        {0,M,0,0,0,0,0,0,M,0},
                        {0,M,M,0,0,0,0,M,M,0}};
                break;
            case 3:
                array = new int[][]{
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,M,M,0,0,0,0,M,M,0},
                        {0,M,0,0,0,0,0,0,M,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {M,M,0,0,0,0,0,0,0,0},
                        {M,M,0,0,0,0,0,0,0,0},
                        {M,M,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
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
                        {0,M,M,0,0,0,0,0,0,0},
                        {0,M,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,M,M,M,M,0,0,0},
                        {0,0,0,M,M,M,M,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,M,0},
                        {0,0,0,0,0,0,0,M,M,0}};
                break;
            case 5:
                array = new int[][]{
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,M,M,0,0,0,0,M,M,0},
                        {0,M,0,0,0,0,0,0,M,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,M,M,M,M,0,0,0},
                        {0,0,0,M,M,M,M,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,M,0,0,0,0,0,0,M,0},
                        {0,M,M,0,0,0,0,M,M,0}};
                break;
            case 6:
                array = new int[][]{
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,M,M,0,0,0,0,M,M,0},
                        {0,M,0,0,0,0,0,0,M,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,M,0,0,0,0,0,0,M,0},
                        {0,M,M,0,0,0,0,M,M,0}};
                break;
            case 7:
                array = new int[][]{
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,M,M,0,0,0,0,0,0,0},
                        {0,M,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,M,M,M,M,0,0,0},
                        {0,0,0,M,M,M,M,0,0,0},
                        {M,M,0,0,0,0,0,0,0,0},
                        {M,M,0,0,0,0,0,0,0,0},
                        {M,M,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,M,0,0,0,0,0,0,M,0},
                        {0,M,M,0,0,0,0,M,M,0}};
                break;
            case 8:
                array = new int[][]{
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,M,M,0,0,0,0,0,0,0},
                        {0,M,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,M,M,M,M,0,0,0},
                        {0,0,0,M,M,M,M,0,0,0},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,M,0},
                        {0,0,0,0,0,0,0,M,M,0}};
                break;
            case 9:
                array = new int[][]{
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,M,M,0,0,0,0,M,M,0},
                        {0,M,0,0,0,0,0,0,M,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,M,M,M,M,0,0,0},
                        {0,0,0,M,M,M,M,0,0,0},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {M,M,0,0,0,0,0,0,M,M},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0},
                        {0,M,0,0,0,0,0,0,M,0},
                        {0,M,M,0,0,0,0,M,M,0}};
                break;
        }

        for (int i=0; i<array.length;i++)
            for (int j=0; j< array[i].length;j++)
                if (array[i][j]==1)
                    obj(j,i);
    }

}
