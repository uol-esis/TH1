package de.uol.pgdoener.th1.data.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SplitRowStructure extends Structure {

    public SplitRowStructure(Long id, int position, Long tableStructureId, String name, String description,
                             Integer columnIndex, String delimiter, Integer startRow, Integer endRow) {
        super(id, position, tableStructureId, name, description);
        this.columnIndex = columnIndex;
        this.delimiter = delimiter;
        this.startRow = startRow;
        this.endRow = endRow;
    }

    private Integer columnIndex;

    private String delimiter;

    private Integer startRow;
    private Integer endRow;

}
