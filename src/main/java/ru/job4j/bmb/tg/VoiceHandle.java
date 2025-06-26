package ru.job4j.bmb.tg;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class VoiceHandle {

    public VoiceHandle() {
    }

    public List<String> process(String message, LongPoll longPoll) {
        var result = new ArrayList<String>();
        IntStream.range(0, 5).forEach(it -> {
            try {
                Thread.sleep(1000);
                longPoll.sent(String.format("Message: %s", it));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return result;
    }
}
