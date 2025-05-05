package de.uol.pgdoener.th1.data.entity;

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
public class HeaderRowStructure extends Structure {

    public HeaderRowStructure(Long id, int position, Long tableStructureId, String name, String description,
                              String[] headerNames) {
        super(id, position, tableStructureId, name, description);
        this.headerNames = headerNames;
    }

    @Type(StringArrayType.class)
    @Column(columnDefinition = "text[]", nullable = true)
    private String[] headerNames;

}
