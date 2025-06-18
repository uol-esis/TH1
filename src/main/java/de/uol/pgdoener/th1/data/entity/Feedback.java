package de.uol.pgdoener.th1.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(schema = "th1-internal")
public class Feedback {

    @Id
    @GeneratedValue
    private UUID id;
    @Column(columnDefinition = "TEXT")
    private String content;

}
