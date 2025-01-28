package de.uol.pgdoener.th1.api.payload.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CreateTableStructure {
    @NotNull
    private String name;
    @NotNull
    private final Character delimiter;
    @NotNull
    @Valid
    private final List<CreateStructure> structures;
    @Positive
    private final Integer endRow;
    @Positive
    private final Integer endColumn;
}
