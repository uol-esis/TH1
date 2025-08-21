package de.uol.pgdoener.th1.infastructure.persistence.entity;

import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HeaderRowStructure extends Structure {

    public HeaderRowStructure(
            Long id,
            int position,
            Long tableStructureId,
            String name,
            String description,
            String[] headerNames,
            HeaderPlacementType headerPlacementType
    ) {
        super(id, position, tableStructureId, name, description);
        this.headerNames = headerNames;
        this.headerPlacementType = headerPlacementType;
    }

    @Type(StringArrayType.class)
    @Column(columnDefinition = "text[]")
    private String[] headerNames;

    @Enumerated(EnumType.STRING)
    private HeaderPlacementType headerPlacementType;

}
