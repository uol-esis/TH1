package de.uol.pgdoener.th1.infastructure.persistence.entity;

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
public class RemoveTrailingColumnStructure extends Structure {

    public RemoveTrailingColumnStructure(
            Long id, int position, Long tableStructureId, String name, String description, String[] blackList) {
        super(id, position, tableStructureId, name, description);
        this.blackList = blackList;
    }

    @Type(StringArrayType.class)
    @Column(columnDefinition = "text[]", nullable = true)
    private String[] blackList;

}
