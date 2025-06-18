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
public class RemoveFooterStructure extends Structure {

    public RemoveFooterStructure(Long id, int position, Long tableStructureId, String name, String description,
                                 Integer threshold, String[] blackList) {
        super(id, position, tableStructureId, name, description);
        this.threshold = threshold;
        this.blackList = blackList;
    }

    private Integer threshold;

    @Type(StringArrayType.class)
    @Column(columnDefinition = "text[]", nullable = true)
    private String[] blackList;

}
