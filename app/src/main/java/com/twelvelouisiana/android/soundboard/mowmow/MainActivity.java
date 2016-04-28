package com.twelvelouisiana.android.soundboard.mowmow;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements TaskStatusCallback {
    private final String TAG = MainActivity.class.getSimpleName();
    public static final String RINGTONE_PATH = "/media/audio/ringtones/";
    public static final String NOTIFICATION_PATH = "/media/audio/notifications/";
    private static final String TAG_TASK_FRAGMENT = "task_fragment";

    private SaveSoundFragment _saveSoundFragment;

    private File sdDir = Environment.getExternalStorageDirectory();
    private Map<Integer, SoundBoardBean> _soundMap = new HashMap<Integer, SoundBoardBean>();

    private MediaPlayer _mediaPlayer = null;

	@Override
	public void onBackPressed() {
		stopMediaPlayer();
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        FragmentManager fm = getFragmentManager();
        _saveSoundFragment = (SaveSoundFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (_saveSoundFragment == null) {
            _saveSoundFragment = new SaveSoundFragment();
            fm.beginTransaction().add(_saveSoundFragment, TAG_TASK_FRAGMENT).commit();
        }

        // Add sounds to the map for easy lookup
        addSound(R.id.finn1, R.raw.finn1);
        addSound(R.id.finn2, R.raw.finn2);

        // Register the context menu for long clicks.
        ImageButton finn1Button = (ImageButton) findViewById(R.id.finn1);
        registerForContextMenu(finn1Button);
        ImageButton finn2Button = (ImageButton) findViewById(R.id.finn2);
        registerForContextMenu(finn2Button);

	}

    @Override
    protected void onPause() {
        stopMediaPlayer();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        _mediaPlayer = null;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(getString(R.string.menu_header_title));
        menu.add(0, v.getId(), 0, getString(R.string.save_as_ringtone));
        menu.add(0, v.getId(), 0, getString(R.string.save_as_notification));
        menu.add(0, v.getId(), 0, getString(R.string.cancel));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(getString(R.string.save_as_ringtone))) {
            saveRingtone(item.getItemId());
        } else if (item.getTitle().equals(getString(R.string.save_as_notification))) {
            saveNotification(item.getItemId());
        } else {
            return false;
        }
        return true;
    }

    public void saveRingtone(int id) {
        String path = sdDir.getAbsolutePath() + RINGTONE_PATH;
        _saveSoundFragment.startBackgroundTask(_soundMap.get(id), path);
    }

    public void saveNotification(int id) {
        String path = sdDir.getAbsolutePath() + NOTIFICATION_PATH;
        _saveSoundFragment.startBackgroundTask(_soundMap.get(id), path);
    }

    public void onClick1(View view) {
		playSound(R.raw.finn1);
	}

	public void onClick2(View view) {
		playSound(R.raw.finn2);
    }

    private void playSound(int resid) {
        if (_mediaPlayer != null)
        {
            _mediaPlayer.reset();
        }
        _mediaPlayer = MediaPlayer.create(getApplicationContext(), resid);
        _mediaPlayer.start();
    }

    private void stopMediaPlayer() {
        if (_mediaPlayer != null) {
             _mediaPlayer.stop();
            _mediaPlayer.release();
            _mediaPlayer = null;
        }
    }

    private void addSound(int id, int rawId)
    {
        String title = getResources().getResourceEntryName(id);
        SoundBoardBean bean = new SoundBoardBean(rawId, title, title);
        _soundMap.put(id, bean);
    }

    // Background task callbacks.

    @Override
    public void onPreExecute() {
        Log.d(TAG, "onPreExecute() called.");
    }

    @Override
    public void onProgressUpdate(int progress) {
        Log.d(TAG, "onProgressUpdate() called.");
    }

    @Override
    public void onPostExecute(String result) {
        Log.d(TAG, "onPostExecute() called.");
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        if (_saveSoundFragment != null)
            _saveSoundFragment.updateExecutingStatus(false);
    }

    @Override
    public void onCancelled() {
        Log.d(TAG, "onCancelled() called.");
    }
}
