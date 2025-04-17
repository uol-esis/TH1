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
public class RemoveGroupedHeaderStructure extends Structure {

    public RemoveGroupedHeaderStructure(Long id, int position, Long tableStructureId, Integer[] columns, Integer[] rows, Integer startRow, Integer startColumn) {
        super(id, position, tableStructureId);
        this.columns = columns;
        this.rows = rows;
        this.startRow = startRow;
        this.startColumn = startColumn;
    }

    @Type(IntArrayType.class)
    @Column(columnDefinition = "integer[]", nullable = true)
    private Integer[] columns;

    @Type(IntArrayType.class)
    @Column(columnDefinition = "integer[]", nullable = true)
    private Integer[] rows;

    private Integer startRow;
    private Integer startColumn;

}
