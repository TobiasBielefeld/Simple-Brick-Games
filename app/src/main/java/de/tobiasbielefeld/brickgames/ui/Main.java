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

package de.tobiasbielefeld.brickgames.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import de.tobiasbielefeld.brickgames.R;
import de.tobiasbielefeld.brickgames.classes.Game;
import de.tobiasbielefeld.brickgames.surfaceViews.GameView1;
import de.tobiasbielefeld.brickgames.surfaceViews.GameView2;

import static de.tobiasbielefeld.brickgames.SharedData.*;
import static de.tobiasbielefeld.brickgames.classes.Game.*;

/*
 *  Main activity which loads everything, handles the button input and contains the main loop
 */

public class Main extends AppCompatActivity implements Runnable, View.OnTouchListener{

    private long mBackPressedTime;

    private Thread mThread = null;
    private Toast mToast;
    private Toolbar mToolbar;
    private ImageView[] mButton = new ImageView[8];
    private TextView[] mText = new TextView[5];
    private boolean mIsRunning;
    private int mPause;
    private long mLastFrameTime;

    private LinearLayout mLinearLayoutBackground;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        createSoundPool();

        mLinearLayoutBackground = (LinearLayout) findViewById(R.id.linearLayoutBackground); //the whole display
        LinearLayout l1 = (LinearLayout) findViewById(R.id.LinearLayout1);
        LinearLayout linearLayoutMenuButtons = (LinearLayout) findViewById(R.id.linearLayoutMenuButtons);
        LinearLayout linearLayoutGameField = (LinearLayout) findViewById(R.id.linearLayoutGameField);
        LinearLayout linearLayoutTexts = (LinearLayout) findViewById(R.id.linearLayoutTexts);
        LinearLayout linearLayoutGameExtra = (LinearLayout) findViewById(R.id.linearLayoutGameExtra);
        vibration = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        gameView1 = new GameView1(linearLayoutGameField.getContext());
        gameView2 = new GameView2(linearLayoutGameExtra.getContext());

        mButton[0] = (ImageView) findViewById(R.id.button_up);
        mButton[1] = (ImageView) findViewById(R.id.button_down);
        mButton[2] = (ImageView) findViewById(R.id.button_left);
        mButton[3] = (ImageView) findViewById(R.id.button_right);
        mButton[4] = (ImageView) findViewById(R.id.button_action);
        mButton[5] = (ImageView) findViewById(R.id.button_reset);
        mButton[6] = (ImageView) findViewById(R.id.button_pause);
        mButton[7] = (ImageView) findViewById(R.id.button_close);

        mText[0] = (TextView) findViewById(R.id.text_highscore);
        mText[1] = (TextView) findViewById(R.id.text_score);
        mText[2] = (TextView) findViewById(R.id.text_level);
        mText[3] = (TextView) findViewById(R.id.text_speed);
        mText[4] = (TextView) findViewById(R.id.text_pause);

        soundList[0] = sp.load(this, R.raw.beep, 1);
        soundList[1] = sp.load(this, R.raw.boing, 1);
        soundList[2] = sp.load(this, R.raw.explosion, 1);
        soundList[3] = sp.load(this, R.raw.boop, 1);
        soundList[4] = sp.load(this, R.raw.crunch, 1);
        soundList[5] = sp.load(this, R.raw.next, 1);
        soundList[6] = sp.load(this, R.raw.gameover, 1);
        soundList[7] = sp.load(this, R.raw.shoot, 1);

        mainActivity = this;
        setSupportActionBar(mToolbar);

        /* Load saved SharedData */
        savedData = PreferenceManager.getDefaultSharedPreferences(this);
        edit = savedData.edit();
        for (int i = 1; i <= NUMBER_OF_GAMES; i++)
            highScores[i] = savedData.getInt("HighScore" + Integer.toString(i), 0);
        look = savedData.getInt(getString(R.string.prefKeyTextures), 2);
        menu.load();

        /* set up the dimensions of the field */
        metrics = getResources().getDisplayMetrics();
        width = (int) ((metrics.heightPixels - 5.0) / (FIELD_HEIGHT * 2.0));

        Game.reset();

        /* set up the layouts (the layouts will be rearranged to fit the calculated field dimensions)*/
        params = new LinearLayout.LayoutParams(metrics.widthPixels, (FIELD_HEIGHT) * width + distanceHeight * 2);
        params.setMargins(0, width, 0, 0);  //width is okay
        l1.setLayoutParams(params);
        linearLayoutMenuButtons.setLayoutParams(new LinearLayout.LayoutParams((metrics.widthPixels / 2 - ((FIELD_WIDTH * width) / 2)) - distanceWidth, LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayoutGameField.setLayoutParams(new LinearLayout.LayoutParams(FIELD_WIDTH * width + distanceWidth * 2, LinearLayout.LayoutParams.MATCH_PARENT));
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(width, 0, 0, 0);
        linearLayoutTexts.setLayoutParams(params);
        linearLayoutGameExtra.setLayoutParams(new LinearLayout.LayoutParams(width * FIELD_WIDTH_2 + distanceWidth * 2, width * FIELD_HEIGHT_2 + distanceHeight * 2));
        linearLayoutGameField.addView(gameView1);
        linearLayoutGameExtra.addView(gameView2);

        for (int i=0;i<5;i++) {
            mButton[i].setOnTouchListener(this);
        }

        setBackgroundColor();
        changeButtonColor();
        showOrHideStatusBar();
    }

    @Override
    public void onPause() {                                                                         //pause thread
        super.onPause();
        mIsRunning = false;
        try {
            mThread.join();
        } catch (InterruptedException ignored) {}
    }

    @Override
    public void onResume() {                                                                        //resume thread
        super.onResume();
        mIsRunning = true;
        mThread = new Thread(this);
        mThread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item1) {
            if (Game.sCurrentGame != 0)                                                             //pause the game when opening, but not when the game menu is currently shown
                startPause();

            Intent intent = new Intent(getApplicationContext(), About.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.item2) {
            if (Game.sCurrentGame != 0)
                startPause();

            startActivityForResult(new Intent(getApplicationContext(), Settings.class), 0);
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {                 //to apply changes made in settings
        if (resultCode == RESULT_OK) {
            if (data.getIntExtra(getString(R.string.prefKeyBackground), 0) > 0)
                setBackgroundColor();

            if (data.getIntExtra(getString(R.string.prefKeyButtons), 0) > 0)
                changeButtonColor();

            if (data.getIntExtra(getString(R.string.prefKeyHideStatusBar), 0) > 0)
                showOrHideStatusBar();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent sEvent) {                                         //handle back mButton presses
        long BACK_PRESSED_TIME_DELTA = 2000;

        if (keyCode == KeyEvent.KEYCODE_BACK                                                        //if it was the back key and this feature is still activated in the Settings,
                && savedData.getBoolean(getString(R.string.prefKeyConfirmClosing), true)
                && (System.currentTimeMillis() - mBackPressedTime > BACK_PRESSED_TIME_DELTA)) {      //and the delta to the last time pressed mButton is over the max time

            showToast(getString(R.string.press_again));                                       //show mToast to press again
            mBackPressedTime = System.currentTimeMillis();                                           //and save the time as pressed
            return true;                                                                            //don't exit the game
        }

        return super.onKeyDown(keyCode, sEvent);                                                     //if the time delta is smaller than the max time, close game
    }

    @Override
    public void run() {                                                                             //Main loop
        while (mIsRunning) {

            //update game status
            if (mPause!=1) {
                Game.timerTick();
                Game.drawFieldContent();
            }

            //DRAW!!!
            gameView1.draw();
            gameView2.draw();

            long timeThisFrame = (System.currentTimeMillis() - mLastFrameTime);                     //update the frame counts
            long timeToSleep = TIMER_PAUSE - timeThisFrame;

            if (timeToSleep > 0) {                                                                  //sleep specific amount so the fps is looked
                try {
                    Thread.sleep(timeToSleep);
                } catch (InterruptedException ignored) {}
            }

            mLastFrameTime = System.currentTimeMillis();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {                                             //for button input, it is with multi touch but my program code doesn't work very good
        int pointerID = event.getPointerId(0) + 1;
        int pointerIndex = event.findPointerIndex(pointerID - 1);

        int[] location1 = new int[2];
        v.getLocationOnScreen(location1);

        float X = event.getX(pointerIndex) + location1[0];
        float Y = event.getY(pointerIndex) + location1[1];

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                for (int i = 0; i < 5; i++) {
                    int[] location = new int[2];
                    mButton[i].getLocationOnScreen(location);

                    if (mButtonPressed[i] != pointerID && Game.sEvent == 0 && mPause!=1
                            && X >= location[0]
                            && X <= location[0] + mButton[i].getWidth()
                            && Y >= location[1]
                            && Y <= location[1] + mButton[i].getHeight()) {
                        for (int j = 0; j < 5; j++) {
                            if (mButtonPressed[j] == pointerID) {
                                mButtonPressed[j] = 0;
                                mButtonPressedCounter[j] = 0;
                            }
                        }

                        vibrate();
                        mButtonPressed[i] = pointerID;
                        input = i + 1;
                        getCurrentGame().input();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                for (int i = 0; i < 5; i++) {
                    if (mButtonPressed[i] == pointerID) {
                        mButtonPressed[i] = 0;
                        mButtonPressedCounter[i] = 0;
                    }
                }
                break;
        }

        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void createNewSoundPool() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        sp = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();
    }

    @SuppressWarnings("deprecation")
    protected void createOldSoundPool() {
        sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
    }

    public void click1(View view) {
        switch (view.getId()) {
            case R.id.button_close:
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
                break;
            case R.id.button_pause:
                if (mPause==0)                                                                      //just start the pause
                    startPause();
                else if (mPause==1) {                                                               //this means pause is running and should stop, so set to 2 so the handler doesnt call itself again
                    mPause = 2;
                    mText[4].setVisibility(View.INVISIBLE);
                }
                else if (mPause==2)                                                                 //this means the handler should stop, but the pause button was pressed again, so just set to 1 so it wont stop
                    mPause=1;
                break;
            case R.id.button_reset:
                mText[4].setVisibility(View.INVISIBLE);
                mPause=0;
                Game.reset();
                break;
        }

        vibrate();
    }

    public void updateUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mText[0].setText(String.format(Locale.getDefault(), "%d00", highScores[Game.sCurrentGame == 0 ? menu.getChoice() : Game.sCurrentGame]));
                mText[1].setText(String.format(Locale.getDefault(), "%d00", Game.sScore));
                mText[2].setText(String.format(Locale.getDefault(), "%d", Game.sCurrentGame == 0 ? menu.getLevelChoice() : Game.sLevel));
                mText[3].setText(String.format(Locale.getDefault(), "%d", Game.sSpeed));
            }
        });
    }

    private void startPause() {
        if (mPause==0) {
            mPause=1;
            PauseHandler pauseHandler = new PauseHandler();
            pauseHandler.sendEmptyMessage(0);
        }
    }

    private void createSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            createNewSoundPool();
        } else {
            createOldSoundPool();
        }
    }

    private void showOrHideStatusBar() {
        if (savedData.getBoolean(getString(R.string.prefKeyHideStatusBar), false))
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        else
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void showToast(String text) {                                                     //simple function to show a new mToast text
        if (mToast == null)
            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);                        //initialize mToast
        else
            mToast.setText(text);

        mToast.show();
    }

    private void setBackgroundColor() {
        switch (savedData.getInt("prefKeyBackground",1)) {
            case 1: default:
                mToolbar.setBackgroundResource(R.color.background_blue);
                mLinearLayoutBackground.setBackgroundResource(R.color.background_blue);
                break;
            case 2:
                mToolbar.setBackgroundResource(R.color.background_green);
                mLinearLayoutBackground.setBackgroundResource(R.color.background_green);
                break;
            case 3:
                mToolbar.setBackgroundResource(R.color.background_red);
                mLinearLayoutBackground.setBackgroundResource(R.color.background_red);
                break;
            case 4:
                mToolbar.setBackgroundResource(R.color.background_yellow);
                mLinearLayoutBackground.setBackgroundResource(R.color.background_yellow);
                break;
            case 5:
                mToolbar.setBackgroundResource(R.color.background_gray);
                mLinearLayoutBackground.setBackgroundResource(R.color.background_gray);
                break;
        }
    }

    private void changeButtonColor() {
        int button1, button2, button3, button4, button5;

        switch (savedData.getInt("prefKeyButtons", 1)) {
            case 1:default:
                button1=R.drawable.button_1_blue;
                button2=R.drawable.button_2_blue;
                button3=R.drawable.button_3_blue;
                button4=R.drawable.button_4_blue;
                button5=R.drawable.button_5_blue;
                break;
            case 2:
                button1=R.drawable.button_1_green;
                button2=R.drawable.button_2_green;
                button3=R.drawable.button_3_green;
                button4=R.drawable.button_4_green;
                button5=R.drawable.button_5_green;
                break;
            case 3:
                button1=R.drawable.button_1_red;
                button2=R.drawable.button_2_red;
                button3=R.drawable.button_3_red;
                button4=R.drawable.button_4_red;
                button5=R.drawable.button_5_red;
                break;
            case 4:
                button1=R.drawable.button_1_yellow;
                button2=R.drawable.button_2_yellow;
                button3=R.drawable.button_3_yellow;
                button4=R.drawable.button_4_yellow;
                button5=R.drawable.button_5_yellow;
                break;
            case 5:
                button1=R.drawable.button_1_gray;
                button2=R.drawable.button_2_gray;
                button3=R.drawable.button_3_gray;
                button4=R.drawable.button_4_gray;
                button5=R.drawable.button_5_gray;
                break;
        }

        mButton[0].setImageResource(button1);
        mButton[1].setImageResource(button2);
        mButton[2].setImageResource(button3);
        mButton[3].setImageResource(button4);
        mButton[4].setImageResource(button5);
        mButton[5].setImageResource(button5);
        mButton[6].setImageResource(button5);
        mButton[7].setImageResource(button5);
    }

    private static class PauseHandler extends Handler {                                             //shows and hides the Pause text when game is paused
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (mainActivity.mPause==1) {

                if (mainActivity.mText[4].getVisibility() == View.VISIBLE)
                    mainActivity.mText[4].setVisibility(View.INVISIBLE);
                else
                    mainActivity.mText[4].setVisibility(View.VISIBLE);

                sendEmptyMessageDelayed(0,500);
            }
            else {
                mainActivity.mPause=0;
                mainActivity.mText[4].setVisibility(View.INVISIBLE);
            }

        }
    }

}

