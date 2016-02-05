package com.dummies.silentmodetoggle;

import android.media.AudioManager;

/**
 * Created by RubenGuillermo on 12/28/2015.
 */
public class RingerHelper {

    private RingerHelper(){}

    public static void performToggle(AudioManager audioManager){
        audioManager.setRingerMode(isPhoneSilent(audioManager)? AudioManager.RINGER_MODE_NORMAL : AudioManager.RINGER_MODE_SILENT);
    }

    public static boolean isPhoneSilent(AudioManager audioManager){
        return audioManager.getRingerMode()==AudioManager.RINGER_MODE_SILENT;
    }
}
