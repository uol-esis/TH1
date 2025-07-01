package de.uol.pgdoener.th1.data.entity;


import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.util.List;
import java.util.Map;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PivotMatrixStructure extends Structure {

    public PivotMatrixStructure(
            Long id, int position, Long tableStructureId, String name, String description,
            Map<String, List<Integer>> pivotField, Integer[] blocklist, String[] keysToCarryForward
    ) {
        super(id, position, tableStructureId, name, description);
        this.pivotField = pivotField;
        this.blockIndices = blocklist;
        this.keysToCarryForward = keysToCarryForward;
    }

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, List<Integer>> pivotField;

    @Type(StringArrayType.class)
    @Column(columnDefinition = "integer[]")
    private Integer[] blockIndices;

    @Type(StringArrayType.class)
    @Column(columnDefinition = "text[]")
    private String[] keysToCarryForward;


}
