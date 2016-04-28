package com.twelvelouisiana.android.soundboard.mowmow;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by Tim O'Malley on 4/22/2016.
 *
 * A retained fragment to finish the background task.
 */
public class SaveSoundFragment extends Fragment {
    private TaskStatusCallback _taskStatusCallback;
    private SaveSoundAsyncTask _saveSoundAsyncTask;
    private boolean isTaskExecuting = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * Deprecated method required for API 22 and lower.
     *
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof  TaskStatusCallback) {
            _taskStatusCallback = (TaskStatusCallback) activity;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TaskStatusCallback) {
            _taskStatusCallback = (TaskStatusCallback) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        _taskStatusCallback = null;
    }

    public void startBackgroundTask(SoundBoardBean soundBoardBean, String path) {
        if(!isTaskExecuting){
            _saveSoundAsyncTask = new SaveSoundAsyncTask(this.getActivity(), soundBoardBean);
            _saveSoundAsyncTask.execute(path);
            isTaskExecuting = true;
        }
    }

    public void cancelBackgroundTask() {
        if(isTaskExecuting){
            _saveSoundAsyncTask.cancel(true);
            isTaskExecuting = false;
        }
    }

    public void updateExecutingStatus(boolean isExecuting){
        this.isTaskExecuting = isExecuting;
    }

    /**
     * AsyncTask to save the sound file in the background.
     */
    private class SaveSoundAsyncTask extends AsyncTask<String, Void, String> {
        private final String TAG = SaveSoundAsyncTask.class.getSimpleName();
        private Context _context;
        private SoundBoardBean _soundBoardBean = null;

        public SaveSoundAsyncTask(Context context, SoundBoardBean soundBoardBean) {
            _context = context;
            _soundBoardBean = soundBoardBean;
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            if (_soundBoardBean == null) {
                return "The requested sound is not available.";
            }
            String soundTitle = _soundBoardBean.getTitle();
            try {
                String path = params[0];
                int resRAW = _soundBoardBean.getRawId();
                byte[] buffer = null;
                InputStream fis = _context.getResources().openRawResource(resRAW);
                int size = 0;

                size = fis.available();
                buffer = new byte[size];
                fis.read(buffer);
                fis.close();

                String filename = soundTitle + ".ogg";

                boolean exists = (new File(path)).exists();
                if (!exists) {
                    new File(path).mkdirs();
                }

                FileOutputStream save = new FileOutputStream(path + filename);
                save.write(buffer);
                save.flush();
                save.close();

                _context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path + filename)));

                File soundFile = new File(path, filename);

                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DATA, soundFile.getAbsolutePath());
                values.put(MediaStore.MediaColumns.TITLE, soundTitle);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/ogg");
                values.put(MediaStore.Audio.Media.ARTIST, _context.getString(R.string.artist_name));
                values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
                values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
                values.put(MediaStore.Audio.Media.IS_ALARM, true);
                values.put(MediaStore.Audio.Media.IS_MUSIC, false);

                // Insert it into the database
                _context.getContentResolver().insert(MediaStore.Audio.Media.getContentUriForPath(soundFile.getAbsolutePath()), values);

                // Result
                result = soundTitle + " saved.";
            } catch (Exception e) {
                result = soundTitle + " not saved.";
                Log.e(TAG, "Exception saving sound file : " + soundTitle, e);
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (_taskStatusCallback != null) {
                _taskStatusCallback.onPostExecute(result);
            }
        }
    }

}
