package ru.job4j.bmb.content;

import org.springframework.stereotype.Component;
import ru.job4j.bmb.repository.MoodRepository;

@Component
public class ContentProviderText implements ContentProvider {
    private final MoodRepository moodRepository;

    public ContentProviderText(MoodRepository moodRepository) {
        this.moodRepository = moodRepository;
    }

    @Override
    public Content byMood(Long chatId, Long moodId) {
        var content = new Content(chatId);
        content.setText(moodRepository.findById(moodId).orElseThrow().getText());
        return content;
    }
}