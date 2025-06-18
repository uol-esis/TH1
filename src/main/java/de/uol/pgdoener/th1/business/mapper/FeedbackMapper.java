package de.uol.pgdoener.th1.business.mapper;

import de.uol.pgdoener.th1.business.dto.FeedbackDto;
import de.uol.pgdoener.th1.data.entity.Feedback;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeedbackMapper {

    public static Feedback toEntity(FeedbackDto dto) {
        return new Feedback(
                null,
                dto.getContent()
        );
    }

}
