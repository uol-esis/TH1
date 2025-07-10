package de.uol.pgdoener.th1.data.entity;

import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RemoveKeywordsStructure extends Structure {

    public RemoveKeywordsStructure(
            Long id,
            int position,
            Long tableStructureId,
            String name,
            String description,
            String[] keywords,
            Boolean removeRows,
            Boolean removeColumns,
            Boolean ignoreCase,
            MatchType matchType
    ) {
        super(id, position, tableStructureId, name, description);
        this.keywords = keywords;
        this.removeRows = removeRows;
        this.removeColumns = removeColumns;
        this.ignoreCase = ignoreCase;
        this.matchType = matchType;
    }

    @Type(StringArrayType.class)
    @Column(columnDefinition = "text[]", nullable = false)
    private String[] keywords;

    @Column(nullable = false)
    private Boolean removeRows = true;

    @Column(nullable = false)
    private Boolean removeColumns = true;

    @Column(nullable = false)
    private Boolean ignoreCase = true;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MatchType matchType = MatchType.EQUALS;

}

