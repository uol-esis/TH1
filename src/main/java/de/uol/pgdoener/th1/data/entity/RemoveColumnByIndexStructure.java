package de.uol.pgdoener.th1.data.entity;

import io.hypersistence.utils.hibernate.type.array.IntArrayType;
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
public class RemoveColumnByIndexStructure extends Structure {

    public RemoveColumnByIndexStructure(Long id, int position, Long tableStructureId, String name, String description,
                                        Integer[] columns) {
        super(id, position, tableStructureId, name, description);
        this.columns = columns;
    }

    @Type(IntArrayType.class)
    @Column(columnDefinition = "integer[]", nullable = true)
    private Integer[] columns;

}