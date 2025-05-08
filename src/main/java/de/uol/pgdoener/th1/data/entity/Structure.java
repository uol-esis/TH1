package de.uol.pgdoener.th1.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(schema = "th1-internal")
public class Structure {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private int position;

    @Column(name = "table_structure_id", nullable = false)
    private Long tableStructureId;

    private String name;

    private String description;

}