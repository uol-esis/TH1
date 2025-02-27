package de.uol.pgdoener.th1.data.entity;

import de.uol.pgdoener.th1.business.enums.ConverterType;
import io.hypersistence.utils.hibernate.type.array.IntArrayType;
import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Structure {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConverterType converterType;

    @Type(IntArrayType.class)
    @Column(columnDefinition = "integer[]", nullable = true)
    private Integer[] columns;

    @Type(IntArrayType.class)
    @Column(columnDefinition = "integer[]", nullable = true)
    private Integer[] rows;

    @Type(StringArrayType.class)
    @Column(columnDefinition = "text[]", nullable = true)
    private String[] headerNames;

    private Integer startRow;
    private Integer endRow;
    private Integer startColumn;
    private Integer endColumn;

    @Column(nullable = false)
    private int position;

    @Column(name = "table_structure_id", nullable = false)
    private Long tableStructureId;

}