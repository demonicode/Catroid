/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.stage;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.io.sound.SoundManager;
import at.tugraz.ist.catroid.utils.Utils;

public class StageActivity extends Activity {

    public static SurfaceView sage;
    protected boolean isWaiting = false;
    private SoundManager soundManager;
    private StageManager stageManager;
    private boolean stagePlaying = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Utils.checkForSdCard(this)) {
            Window window = getWindow();
            window.requestFeature(Window.FEATURE_NO_TITLE);
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setContentView(R.layout.stage);
            sage = (SurfaceView) findViewById(R.id.stageView);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            soundManager = SoundManager.getInstance();
            stageManager = new StageManager(this);
            stageManager.start();
            stagePlaying = true;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
//        Log.i("StageActivity", "Number of pointers " + event.getPointerCount() + " action code: " + event.getAction() + " coordinates: x: "
//                + event.getX((int) event.getPointerCount() - 1) + " y: " + event.getY((int) event.getPointerCount() - 1));
        
        // first pointer: MotionEvent.ACTION_DOWN
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            processOnTouch((int) event.getX(), (int) event.getY());

        // second pointer: MotionEvent.ACTION_POINTER_2_DOWN
        if (event.getAction() == MotionEvent.ACTION_POINTER_2_DOWN)
            processOnTouch((int) event.getX(1), (int) event.getY(1));

        return false;
    }

    public void processOnTouch(int coordX, int coordY) {
        coordX = coordX + sage.getTop();
        coordY = coordY + sage.getLeft();

        stageManager.processOnTouch(coordX, coordY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.stage_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.stagemenuStart:
            pauseOrContinue();
            break;
        case R.id.stagemenuConstructionSite:
            toMainActivity();
            break;
        }
        return true;
    }

    protected void onStop() {
        super.onStop();
        soundManager.pause();
        stageManager.pause(false);
        stagePlaying = false;
    }

    protected void onRestart() {
        super.onRestart();
        stageManager.resume();
        soundManager.resume();
        stagePlaying = true;
    }

    protected void onDestroy() {
        super.onDestroy();
        soundManager.clear();
    }

    public void onBackPressed() {
        finish();
    }

    private void toMainActivity() {
        finish();
    }

    private void pauseOrContinue() {
        if (stagePlaying) {
            stageManager.pause(true);
            soundManager.pause();
            stagePlaying = false;
        } else {
            stageManager.resume();
            soundManager.resume();
            stagePlaying = true;
        }
    }
}
