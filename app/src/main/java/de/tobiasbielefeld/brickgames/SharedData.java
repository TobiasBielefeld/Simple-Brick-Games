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

package de.tobiasbielefeld.brickgames;

import android.content.SharedPreferences;
import android.media.SoundPool;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;
import java.util.Random;

import de.tobiasbielefeld.brickgames.helper.DrawFading;
import de.tobiasbielefeld.brickgames.ui.Main;
import de.tobiasbielefeld.brickgames.classes.Game;
import de.tobiasbielefeld.brickgames.games.*;
import de.tobiasbielefeld.brickgames.helper.DrawExplosion;
import de.tobiasbielefeld.brickgames.helper.DrawGameOver;
import de.tobiasbielefeld.brickgames.surfaceViews.*;

/*
 * A lot of stuff in static context so i can use them across the whole project.
 * I was used in C++ to use header files and put everything there. This is like that.
 */

public class SharedData {

    public final static int NUMBER_OF_GAMES = 14;
    public final static int version = 2;
    public final static int TIMER_PAUSE = 20;
    public final static String MENU_GAME_CHOICE = "menuGameChoice";

    public static Menu menu = new Menu();
    private static GameA gameA = new GameA();
    private static GameB gameB = new GameB();
    private static GameC gameC = new GameC();
    private static GameD gameD = new GameD();
    private static GameE gameE = new GameE();
    private static GameF gameF = new GameF();
    private static GameG gameG = new GameG();
    private static GameH gameH = new GameH();
    private static GameI gameI = new GameI();
    private static GameJ gameJ = new GameJ();
    private static GameK gameK = new GameK();
    private static GameL gameL = new GameL();
    private static GameM gameM = new GameM();
    private static GameN gameN = new GameN();
    public static Game games[] = {menu, gameA, gameB, gameC, gameD, gameE, gameF, gameG, gameH, gameI, gameJ, gameK, gameL, gameM, gameN};

    public static DrawExplosion drawExplosion = new DrawExplosion();
    public static DrawGameOver drawGameOver = new DrawGameOver();
    public static DrawFading drawFading = new DrawFading();

    public static SharedPreferences savedData;
    public static Random rand = new Random();
    public static DisplayMetrics metrics;
    public static LinearLayout.LayoutParams params;
    public static SharedPreferences.Editor edit;
    public static int[] soundList = new int[8];
    public static SoundPool sp;// = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
    public static GameView1 gameView1;
    public static GameView2 gameView2;
    public static Vibrator vibration;
    public static Main mainActivity;

    public static int width, look, input;
    public static int distanceWidth = 5, distanceHeight = 5;

    public static int timerCounter, timerCounter2;
    public static boolean nextLevel;

    public static int[] mButtonPressedCounter = new int[6];
    public static int[] highScores = new int[NUMBER_OF_GAMES + 1];
    public static int[] mButtonPressed = new int[6];

    public static void saveData(String key, int value) {
        edit.putInt(key, value);
        edit.apply();
    }

    public static Game getCurrentGame() {
        return games[Game.sCurrentGame];
    }

    public static void copyArray(int[][] destination, int[][] origin) {                             //copies 2 dimensional arrays of same size!

        for (int i=0; i < origin.length; i++) {
            System.arraycopy(origin[i], 0, destination[i], 0, origin[i].length);
        }
    }

    public static void vibrate() {
        int strength = savedData.getInt("prefKeyVibrationStrength",20);

        if (strength>0)
            vibration.vibrate(strength);
    }

    public static void vibrate(int duration) {
        vibration.vibrate(duration);
    }

}
