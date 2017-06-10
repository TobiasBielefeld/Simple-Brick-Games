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

package de.tobiasbielefeld.brickgames.classes;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;

import static de.tobiasbielefeld.brickgames.SharedData.*;

/*
 *  my main class for games. It provides a lot of methods for the implemented games
 */

public abstract class Game {

    public static int sCurrentGame;

    public final static int FIELD_WIDTH = 10;
    public final static int FIELD_WIDTH_2 = 4;
    public final static int FIELD_HEIGHT = 20;
    public final static int FIELD_HEIGHT_2 = 4;

    protected boolean mLevelsEnabled=false;                                                         //shows if the game has sLevels, override level() if so
    protected boolean mFastShootEnabled=false;                                                      //set to true if the shooting has to be twice as fast
    protected int mScoreLimit = 0;                                                                  //after which points the next sLevel should start. 0 means no sLevels
    public int mTimerLimit = 50;                                                                    //sets how fast the calculation should be called.

    public static boolean sShoot;
    public static int sLevel, sSpeed, sScore, sEvent;
    public static ArrayList<Point> sObjects = new ArrayList<>();
    public static int[][] field = new int[FIELD_WIDTH][FIELD_HEIGHT];
    public static int[][] field2 = new int[FIELD_WIDTH_2][FIELD_HEIGHT_2];
    private static int sLives;

    private final static int AUTO_FIRE_SPEED = 2;

    protected abstract void onStart();                                                              //called on every game start, initialize stuff here

    protected abstract void calculation();                                                          //called on every tick of the main loop, so calculate stuff here

    public abstract void input();                                                                   //handle button input here

    protected abstract void drawField();                                                            //set everything to be drawn on the main game field

    protected void drawField2() {                                                                   //little field on the right side of the main filed
        //override if you want to show something else on the second screen
        for (int i = 1; i <= 4; i++)
            if (sLives >= i)
                field2[0][4 - i] = 1;
    }

    public void shootCalculation() {
        //override if your game has something to shoot at thread calling speed
    }

    protected void level() { //override if you have levels
        sObjects.clear();

        if (getCurrentGame().mLevelsEnabled)
            Log.e("Level Error", "The current game has levels, but doesn't override the level() function");
    }

    public static void timerTick() {                                                                //handle button presses, shoots and of course the calculation of games
        timerCounter = (++timerCounter) % (getCurrentGame().mTimerLimit - (getCurrentGame().mTimerLimit/10*sSpeed) );
        timerCounter2 = ++timerCounter2 % 200;

        if (sEvent == 0) {
            //if (timerCounter2 % AUTO_FIRE_SPEED == 0) {
            if (sCurrentGame != 0 && timerCounter2 % AUTO_FIRE_SPEED == 0) {
                for (int i = 0; i < 5; i++) {
                    if (mButtonPressed[i] != 0) {
                        if (mButtonPressedCounter[i] < 5)
                            mButtonPressedCounter[i]++;
                        else {
                            input = i + 1;
                            getCurrentGame().input();
                        }
                    }
                }
            }

            if (timerCounter == 0) {
                getCurrentGame().calculation();
            }

            if (sShoot) {
                getCurrentGame().shootCalculation();

                if (getCurrentGame().mFastShootEnabled && sShoot)                                  //if fast shoot is true, call the shootcalculation again, so it is twice as fast as normal
                    getCurrentGame().shootCalculation();
            }
        }
    }

    public static void drawFieldContent() {

        switch (sEvent) {
            case 1:
                drawFading.draw();
                break;
            case 4:
                drawGameOver.draw();
                break;
            case 5:
                drawExplosion.draw();
                break;
            default:
                drawGameField();
                break;
        }
    }

    protected static void explosion(int x, int y) {                                                 //call to set an explosion on these coordinates. They will be corrected to fit on the field
        drawExplosion.setLocation(x,y);
        sEvent=5;
        drawGameField();
    }

    protected static void obj(int x, int y) {                                                       //call this with x and y coordinates to get these points automatically drawn
        sObjects.add(new Point(x,y));
    }

    public static void playSound(int soundID) {                                                     //call to play sounds
        //1=beep,2=boing,3=explosion,4=boop,5=crunch,6=next,7=gameover,8=shoot
        if (savedData.getBoolean("prefKeySound", true))
            sp.play(soundList[--soundID], 1, 1, 0, 0, 1);
    }

    protected void nextScore() {                                                                    //call to increment the score. it will automatically increase level and speed according
        if (mScoreLimit == 0)                                                                       //to mScoreLimit (if there is a limit set in your game) if you have levels enabled in the game,
            sScore++;                                                                               //the next level will starts after reaching mScoreLimit
        else if (++sScore % mScoreLimit == 0) {
            if (mLevelsEnabled)
                nextLevel();
            else {
                if (sLevel < 9)
                    sLevel++;
                else {
                    sLevel = 1;
                    if (sSpeed < 9)
                        sSpeed++;
                }
            }
        }

        if (sScore > highScores[sCurrentGame]) {
            highScores[sCurrentGame] = sScore;
            saveData("HighScore" + Integer.toString(sCurrentGame), sScore);
        }

        mainActivity.updateUI();                                                                    //update the shown values
    }

    public void nextLevel() {                                                                       //call to switch to the nextlevel directly. it will increase the speed after level 9
        if (!nextLevel) {
            nextLevel = true;
            sEvent = 1;
            playSound(6);
        } else {
            nextLevel = false;

            if (sLevel < 9)
                sLevel++;
            else {
                sLevel = 1;

                if (sSpeed < 9)
                    sSpeed++;
            }

            level();
            mainActivity.updateUI();
        }

        //mainActivity.updateUI();
    }

    public void decrementLives() {                                                                  //call this to decrement lives, it will be automatically called after an explosion
        if (sScore!=0)                                                                               //only decrement if score is over zero,
            sLives--;                                                                                //so the player doesn't have to restart the game if he didn't got points already

        if (sLives == 0) {
            field2[0][3] = 0;
            sEvent=4;
        } else
            sEvent = 1;
    }

    public static void initialisation() {
        sShoot = false;
        sEvent = 0;

        input = 0;
        timerCounter = -1;
        timerCounter2 = 0;

        getCurrentGame().onStart();
        mainActivity.updateUI();
    }

    private static void drawGameField() {                                                           //resets the field on every thread tick, then calls the game draw methods to fill it again

        for (int i = 0; i < FIELD_WIDTH; i++)
            for (int j = 0; j < FIELD_HEIGHT; j++)
                field[i][j] = 0;

        for (int i = 0; i < FIELD_WIDTH_2; i++)
            for (int j = 0; j < FIELD_HEIGHT_2; j++)
                field2[i][j] = 0;

        for (Point point : sObjects)
            field[point.x][point.y]=1;

        getCurrentGame().drawField();
        getCurrentGame().drawField2();
    }

    private static Game getCurrentGame() {
        return games[sCurrentGame];
    }

    public static void reset() {                                                                    //reset whole game to show the main menu
        sObjects.clear();
        sLives = 4;
        sLevel = 1;
        sSpeed = 1;
        sCurrentGame = 0;
        sScore = 0;

        drawExplosion.reset();
        drawGameOver.reset();
        drawFading.reset();
        initialisation();
    }
}
