package de.uol.pgdoener.th1.data.entity;

import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class removeInvalidRowStructure extends Structure {

    public removeInvalidRowStructure(Long id, int position, Long tableStructureId, Integer threshold, String[] blackList) {
        super(id, position, tableStructureId);
        this.threshold = threshold;
        this.blackList = blackList;
    }

    private Integer threshold;

    @Type(StringArrayType.class)
    @Column(columnDefinition = "text[]", nullable = true)
    private String[] blackList;

}
