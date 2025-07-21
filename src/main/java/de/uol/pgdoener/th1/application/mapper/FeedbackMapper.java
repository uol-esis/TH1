package de.uol.pgdoener.th1.application.mapper;

import de.uol.pgdoener.th1.application.dto.FeedbackDto;
import de.uol.pgdoener.th1.infastructure.persistence.entity.Feedback;
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
