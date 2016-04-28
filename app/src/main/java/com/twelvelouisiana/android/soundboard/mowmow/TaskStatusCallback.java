package com.twelvelouisiana.android.soundboard.mowmow;

/**
 * Created by Tim O'Malley on 4/20/2016.
 */
public interface TaskStatusCallback {
    void onPreExecute();
    void onProgressUpdate(int progress);
    void onPostExecute(String result);
    void onCancelled();
}
