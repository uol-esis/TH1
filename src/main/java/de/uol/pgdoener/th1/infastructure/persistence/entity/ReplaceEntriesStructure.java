package de.uol.pgdoener.th1.infastructure.persistence.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReplaceEntriesStructure extends Structure {

    public ReplaceEntriesStructure(Long id, int position, Long tableStructureId, String name, String description,
                                   String replacement, String search, String regexSearch, List<Integer> columns,
                                   Integer startRow, Integer endRow) {
        super(id, position, tableStructureId, name, description);
        this.replacement = replacement;
        this.search = search;
        this.regexSearch = regexSearch;
        this.columns = columns;
        this.startRow = startRow;
        this.endRow = endRow;
    }

    private String replacement;

    private String search;
    private String regexSearch;

    private List<Integer> columns;
    private Integer startRow;
    private Integer endRow;

}
