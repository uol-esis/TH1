package de.uol.pgdoener.th1.data.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReplaceEntriesStructure extends Structure {

    public ReplaceEntriesStructure(Long id, int position, Long tableStructureId, String name, String description,
                                   String replacement, String search, String regexSearch,
                                   Integer startRow, Integer endRow, Integer startColumn, Integer endColumn) {
        super(id, position, tableStructureId, name, description);
        this.replacement = replacement;
        this.search = search;
        this.regexSearch = regexSearch;
        this.startRow = startRow;
        this.endRow = endRow;
        this.startColumn = startColumn;
        this.endColumn = endColumn;
    }

    private String replacement;

    private String search;
    private String regexSearch;

    private Integer startRow;
    private Integer endRow;
    private Integer startColumn;
    private Integer endColumn;

}
