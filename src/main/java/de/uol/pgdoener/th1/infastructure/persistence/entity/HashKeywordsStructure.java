package de.uol.pgdoener.th1.infastructure.persistence.entity;

import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HashKeywordsStructure extends Structure {

    public HashKeywordsStructure(
            Long id,
            int position,
            Long tableStructureId,
            String name,
            String description,
            String[] keywords,
            Boolean ignoreCase,
            MatchType matchType
    ) {
        super(id, position, tableStructureId, name, description);
        this.keywords = keywords;
        this.ignoreCase = ignoreCase;
        this.matchType = matchType;
    }

    @Type(StringArrayType.class)
    @Column(columnDefinition = "text[]")
    private String[] keywords;

    private Boolean removeRows = true;

    private Boolean removeColumns = true;

    private Boolean ignoreCase = true;

    @Enumerated(EnumType.STRING)
    private MatchType matchType = MatchType.EQUALS;

}

