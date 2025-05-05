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
public class RemoveRowByIndexStructure extends Structure {

    public RemoveRowByIndexStructure(Long id, int position, Long tableStructureId, String name, String description,
                                     Integer[] rows) {
        super(id, position, tableStructureId, name, description);
        this.rows = rows;
    }

    @Type(IntArrayType.class)
    @Column(columnDefinition = "integer[]", nullable = true)
    private Integer[] rows;

}
