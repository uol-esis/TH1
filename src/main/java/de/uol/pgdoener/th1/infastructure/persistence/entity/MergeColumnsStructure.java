package de.uol.pgdoener.th1.infastructure.persistence.entity;

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
public class MergeColumnsStructure extends Structure {

    public MergeColumnsStructure(Long id, int position, Long tableStructureId, String name, String description,
                                 Integer[] columns, String headerName, Integer[] precedenceOrder) {
        super(id, position, tableStructureId, name, description);
        this.columns = columns;
        this.headerName = headerName;
        this.precedenceOrder = precedenceOrder;
    }

    @Type(IntArrayType.class)
    @Column(columnDefinition = "integer[]", nullable = true)
    private Integer[] columns;

    private String headerName;

    @Type(IntArrayType.class)
    @Column(columnDefinition = "integer[]", nullable = true)
    private Integer[] precedenceOrder;

}
