package de.uol.pgdoener.th1.domain.converterchain.service;

import de.uol.pgdoener.th1.application.dto.StructureDto;
import de.uol.pgdoener.th1.application.dto.TableStructureDto;
import de.uol.pgdoener.th1.domain.converterchain.factory.ConverterFactory;
import de.uol.pgdoener.th1.domain.converterchain.model.Converter;
import de.uol.pgdoener.th1.domain.converterchain.model.ConverterChain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ConverterChainCreationService {

    private final ConverterFactory converterFactory;

    public ConverterChain create(TableStructureDto tableStructure) {
        ConverterChain converterChain = new ConverterChain();
        List<StructureDto> structures = tableStructure.getStructures();
        for (int i = 0; i < structures.size(); i++) {
            Converter converter = converterFactory.create(structures.get(i));
            converter.setIndex(i);
            converterChain.add(converter);
        }
        return converterChain;
    }
}

