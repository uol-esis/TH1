package de.uol.pgdoener.th1.application.converterchain.builder;

import de.uol.pgdoener.th1.application.converterchain.model.Converter;
import de.uol.pgdoener.th1.application.converterchain.model.ConverterChain;
import de.uol.pgdoener.th1.application.dto.StructureDto;
import de.uol.pgdoener.th1.application.dto.TableStructureDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class ConverterChainBuilder {

    private final ConverterFactory converterFactory;

    public ConverterChain build(TableStructureDto tableStructure) {
        ConverterChain converterChain = new ConverterChain();
        List<StructureDto> structures = tableStructure.getStructures();
        for (int i = 0; i < structures.size(); i++) {
            Converter converter = converterFactory.createConverter(structures.get(i));
            converter.setIndex(i);
            converterChain.add(converter);
        }
        return converterChain;
    }
}

