package de.uol.pgdoener.th1.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(schema = "th1-internal")
public class SchemaVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String tableName;

    @Column(nullable = false)
    private int version;

    @Column(nullable = false)
    private String changeType; // CREATED, REPLACED, TRANSFORMED

    @Column(nullable = false, columnDefinition = "TEXT")
    private String changeSql;

    @Column
    private String snapshotPath; // z. B. Pfad zur CSV/Parquet-Datei

    @Column(nullable = false)
    private Timestamp changedAt = new Timestamp(System.currentTimeMillis());
}

