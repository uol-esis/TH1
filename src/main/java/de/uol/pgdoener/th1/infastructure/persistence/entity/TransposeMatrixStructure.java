package de.uol.pgdoener.th1.infastructure.persistence.entity;


import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class TransposeMatrixStructure extends Structure {

    public TransposeMatrixStructure(
            Long id, int position, Long tableStructureId, String name, String description) {
        super(id, position, tableStructureId, name, description);
    }
}
