package de.uol.pgdoener.th1.business.infrastructure.converterchain;

import de.uol.pgdoener.th1.business.dto.StructureDto;
import de.uol.pgdoener.th1.business.dto.TableStructureDto;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.Converter;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.ConverterChain;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.ConverterFactory;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ConverterChainBuilder {
    private final TableStructureDto tableStructure;

    public ConverterChain build() {
        ConverterChain converterChain = new ConverterChain();
        List<StructureDto> structures = this.tableStructure.getStructures();
        for (int i = 0; i < structures.size(); i++) {
            Converter converter = ConverterFactory.createConverter(structures.get(i));
            converter.setIndex(i);
            converterChain.add(converter);
        }
        return converterChain;
    }
}
