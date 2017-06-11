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
 * Like tetris: No explanation needed :D
 */

public class GameN extends Game {

    private final static int DIMENSION_HEIGHT = FIELD_HEIGHT + 1;
    private final static int DIMENSION_WIDTH = FIELD_WIDTH + 2;
    private final static int BLOCK_DIMEN=4;

    private int X;
    private int Y;
    private int mLastCounter;
    private int[] mLastNext = new int[4];
    private int[][] mTetrisField = new int[DIMENSION_WIDTH][DIMENSION_HEIGHT];
    private int[][] mSavedField = new int[DIMENSION_WIDTH][DIMENSION_HEIGHT];
    private int[][] mTemp = new int[BLOCK_DIMEN][BLOCK_DIMEN];

    private int[][] I_Block, O_Block, L_Block, J_Block, S_Block, Z_Block, T_Block;
    private int[][] mNextBlock = new int[BLOCK_DIMEN][BLOCK_DIMEN], mNowBlock = new int[BLOCK_DIMEN][BLOCK_DIMEN];

    protected void onStart() {
        super.mTimerLimit=40;

        X = 5;
        Y = 0;
        blockCalculation();
        mLastCounter = 0;

        for (int i = 0; i < 4; i++)
            mLastNext[i] = 0;

        fieldCalculation();

        nextBlock();

        copyArray(mNowBlock,mNextBlock);

        nextBlock();

        for (int i = 0; i < BLOCK_DIMEN; i++)
            for (int j = 0; j < BLOCK_DIMEN; j++)
                if (Y + i >= 0 && mNowBlock[j][i] != 0 && Y + i >= 0)
                    mTetrisField[X + j][Y + i] = mNowBlock[j][i];

        for (int j = 0; j < DIMENSION_HEIGHT; j++)
            for (int i = 0; i < DIMENSION_WIDTH; i++)
                mSavedField[i][j] = 0;

        for (int i = 0; i < sLevel - 1; i++)
            for (int j = 1; j < DIMENSION_WIDTH - 1; j++)
                if (rand.nextBoolean())
                    mSavedField[j][DIMENSION_HEIGHT - 2 - i] = 1;
                else
                    mSavedField[j][DIMENSION_HEIGHT - 2 - i] = 0;
    }

    protected void drawField() {
        for (int j = 0; j < FIELD_HEIGHT; j++)                                                      //restore field
            for (int i = 0; i < FIELD_WIDTH; i++)
                field[i][j] = mSavedField[1 + i][j];

        for (int i = 0; i < BLOCK_DIMEN; i++)
            for (int j = 0; j < BLOCK_DIMEN; j++)
                if (mNowBlock[j][i] != 0 && Y + i >= 0)
                    field[X - 1 + j][Y + i] = 1;
    }

    @Override
    protected void drawField2() {
        for (int i = 0; i < BLOCK_DIMEN; i++)
            for (int j = 0; j < BLOCK_DIMEN; j++)
                if (mNextBlock[i][j] != 0)
                    field2[i][j] = mNextBlock[i][j];
    }

    protected void calculation() {
        fieldCalculation();

        for (int j = 0; j < DIMENSION_HEIGHT - 1; j++)                                              //restore field
            for (int i = 1; i < DIMENSION_WIDTH - 1; i++)
                mTetrisField[i][j] = mSavedField[i][j];

        for (int j = 0; j < BLOCK_DIMEN; j++)
            for (int i = 0; i < BLOCK_DIMEN; i++)
                if (mTetrisField[5 + i][j] != 0 && mNowBlock[i][j] != 0)
                    sEvent = 4;

        breakCondition();
        Y++;

        for (int i = 0; i < BLOCK_DIMEN; i++)
            for (int j = 0; j < BLOCK_DIMEN; j++)
                if (mNowBlock[j][i] != 0)
                    mTetrisField[X + j][Y + i] = 1;
    }

    public void input() {
        fieldCalculation();

        for (int j = 0; j < DIMENSION_HEIGHT - 1; j++)                                              //restore field
            for (int i = 1; i < DIMENSION_WIDTH - 1; i++)
                mTetrisField[i][j] = mSavedField[i][j];

        switch (input) {
            case 2:
                calculation();
                break;
            case 3:
                for (int j = 0; j < BLOCK_DIMEN; j++) {
                    for (int i = 0; i < BLOCK_DIMEN; i++) {                                         //if there is a block left from this, don't move
                        int posX = X-1+i, posY = Y +j;

                        if (posX >= 0 && posX < DIMENSION_WIDTH && posY >= 0 && posY< DIMENSION_HEIGHT
                                && mTetrisField[posX][posY] != 0 && mNowBlock[i][j] != 0) {
                           return;
                        }
                    }
                }

                X--;
                playSound(4);
                break;
            case 4:
                for (int j = 0; j < BLOCK_DIMEN; j++) {
                    for (int i = 0; i < BLOCK_DIMEN; i++) {                                         //if there is a block on the right from this, don't move
                        int posX = X+1+i, posY = Y+j;

                        if (posX >= 0 && posX < DIMENSION_WIDTH && posY >= 0 && posY< DIMENSION_HEIGHT
                                && mTetrisField[posX][posY] != 0 && mNowBlock[i][j] != 0) {
                            return;
                        }
                    }
                }

                X++;
                playSound(4);
                break;
            case 5:
                playSound(2);
                tilt();
                break;
        }
    }

    private void blockCalculation() {
        I_Block  = new int[][]{
                {1,1,1,1},
                {0,0,0,0},
                {0,0,0,0},
                {0,0,0,0}};
        O_Block  = new int[][]{
                {1,1,0,0},
                {1,1,0,0},
                {0,0,0,0},
                {0,0,0,0}};
        L_Block  = new int[][]{
                {0,0,1,0},
                {1,1,1,0},
                {0,0,0,0},
                {0,0,0,0}};
        S_Block  = new int[][]{
                {1,1,0,0},
                {0,1,1,0},
                {0,0,0,0},
                {0,0,0,0}};
        Z_Block  = new int[][]{
                {0,1,1,0},
                {1,1,0,0},
                {0,0,0,0},
                {0,0,0,0}};
        J_Block  = new int[][]{
                {1,1,1,0},
                {0,0,1,0},
                {0,0,0,0},
                {0,0,0,0}};
        T_Block  = new int[][]{
                {1,0,0,0},
                {1,1,0,0},
                {1,0,0,0},
                {0,0,0,0}};
    }

    private void fieldCalculation() {
        for (int j = 0; j < DIMENSION_HEIGHT - 1; j++)                                              //for the rest of the field
            for (int i = 1; i < DIMENSION_WIDTH - 1; i++)
                mTetrisField[i][j] = 0;

        for (int j = 0; j < DIMENSION_HEIGHT; j++)                                                  //for the border of blocks around the field
            mTetrisField[0][j] = 1;

        for (int j = 0; j < DIMENSION_HEIGHT; j++)
            mTetrisField[DIMENSION_WIDTH - 1][j] = 1;

        for (int j = 0; j < DIMENSION_WIDTH; j++)
            mTetrisField[j][DIMENSION_HEIGHT - 1] = 1;
    }

    //calculate next block to play
    private void nextBlock() {
        int next;

        do {
            next = rand.nextInt(7);
        } while (next == mLastNext[0] || next == mLastNext[1] || next == mLastNext[2]);

        mLastNext[mLastCounter] = next;
        mLastCounter = (++mLastCounter) % 3;

        switch (next) {
            case 0:
                copyArray(mNextBlock,I_Block);
                break;
            case 1:
                copyArray(mNextBlock,O_Block);
                break;
            case 2:
                copyArray(mNextBlock,Z_Block);
                break;
            case 3:
                copyArray(mNextBlock,J_Block);
                break;
            case 4:
                copyArray(mNextBlock,S_Block);
                break;
            case 5:
                copyArray(mNextBlock,L_Block);
                break;
            case 6:
                copyArray(mNextBlock,T_Block);
                break;
        }
    }

    private void onBreak() {
        for (int j = 0; j < BLOCK_DIMEN; j++) {                                                     //save current block on the field
            for (int i = 0; i < BLOCK_DIMEN; i++) {
                if (mNowBlock[i][j] != 0 && (X+i) > 0 && (Y+j) >= 0 && (X+i) < DIMENSION_WIDTH && (Y+j) < DIMENSION_HEIGHT) {
                    if (mTetrisField[X+i][Y+j] == 1)
                        sEvent = 4;
                    else
                        mTetrisField[X+i][Y+j] = 1;
                }
            }
        }

        for (int j = 0; j < DIMENSION_HEIGHT - 1; j++) {                                            //test, when a row is full
            int counter  = 0;

            for (int i = 0; i < DIMENSION_WIDTH; i++) {
                if (mTetrisField[i][j] != 0)
                    counter++;

                if (counter == DIMENSION_WIDTH) {                                                   //delete row if it's full
                    for (int k = j; k > 0; k--)
                        for (int l = 1; l < DIMENSION_WIDTH - 1; l++)
                            mTetrisField[l][k] = mTetrisField[l][k - 1];

                    nextScore();

                    if (sSpeed < 9 && sScore % 20 == 0)
                        sSpeed++;
                }
            }
        }

        for (int j = 0; j < DIMENSION_HEIGHT; j++)                                                  //save field
            for (int i = 0; i < DIMENSION_WIDTH; i++)
                mSavedField[i][j] = mTetrisField[i][j];

        X = 5;                                                                                      //reset block position
        Y = -1;

        copyArray(mNowBlock, mNextBlock);

        playSound(5);
        nextBlock();
    }

    private void breakCondition() {                                                                 //If there is a block under NowBlock, call onBreak()
        for (int i = 0; i < BLOCK_DIMEN; i++) {
            for (int j = 0; j < BLOCK_DIMEN; j++) {
                int posX=X+j, posY = Y+i+1;

                if (posX>=0 && posX<DIMENSION_WIDTH && posY>=0 && posY<DIMENSION_HEIGHT &&
                        mNowBlock[j][i]!=0 && mTetrisField[posX][posY]!=0) {
                    onBreak();
                }
            }
        }
    }

    private void tilt() {
        for (int i = 0, j = BLOCK_DIMEN-1; j >=0; i++, j--)
            for (int k = 0; k < BLOCK_DIMEN; k++)
                mTemp[i][k] = mNowBlock[k][j];

        while (mTemp[0][0] == 0 && mTemp[1][0] == 0 && mTemp[2][0] == 0 && mTemp[3][0] == 0) {
            for (int i=0;i<BLOCK_DIMEN;i++)
                System.arraycopy(mTemp[i], 1, mTemp[i], 0, BLOCK_DIMEN - 1);

            for (int i=0;i<BLOCK_DIMEN;i++)
                mTemp[i][BLOCK_DIMEN-1]=0;
        }

        while (mTemp[0][0] == 0 && mTemp[0][1] == 0 && mTemp[0][2] == 0 && mTemp[0][3] == 0) {
            for (int i=0;i<BLOCK_DIMEN-1;i++)
                System.arraycopy(mTemp[i + 1], 0, mTemp[i], 0, BLOCK_DIMEN);

            for (int i=0;i<BLOCK_DIMEN;i++)
                mTemp[BLOCK_DIMEN-1][i]=0;
        }

        for (int j = 0; j < BLOCK_DIMEN; j++)                                                       //if tilting results in moving in another block
            for (int i = 0; i < BLOCK_DIMEN; i++)
                if (X+i>=0 && X+i <DIMENSION_WIDTH && Y+j>=0 && Y+j < DIMENSION_HEIGHT && mTemp[i][j]!=0 && mTetrisField[X + i][Y + j] != 0)
                    return;

        copyArray(mNowBlock,mTemp);
    }
}
