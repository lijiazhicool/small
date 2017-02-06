package com.av.ringtone.logic;

import com.av.ringtone.model.VoiceModel;

/**
 * Created by LiJiaZhi on 17/2/6.
 */

public interface MediaListener {
    void play(VoiceModel model);

    void pause();

    void stop();
}
