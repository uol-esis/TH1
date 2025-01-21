package de.uol.pgdoener.th1.api.payload.request;

import de.uol.pgdoener.th1.api.payload.validation.IsConverterType;
import de.uol.pgdoener.th1.api.payload.validation.ValidStructure;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@ValidStructure
public class CreateStructure {
    @NotNull
    @IsConverterType
    private String converterType;
    private Integer[] columnIndex;
    private Integer[] rowIndex;
    private Integer startR;
    private Integer startC;
    private Integer endR;
    private Integer endC;
}
