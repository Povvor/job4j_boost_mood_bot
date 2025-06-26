package ru.job4j.bmb.tg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

public class TgService extends LongPoll {

    private final VoiceHandle voiceHandle;

    public TgService(VoiceHandle voiceHandle) {
        this.voiceHandle = voiceHandle;
    }

    @Override
    void receive(String message) {
        voiceHandle.process(message, this)
                .forEach(this::sent);
    }

    public static void main(String[] args) {
        new TgService(new VoiceHandle()).receive("Hello");
    }
}
