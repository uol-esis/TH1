package de.uol.pgdoener.th1.business.infrastructure.converterchain;

import de.uol.pgdoener.th1.business.dto.StructureDto;
import de.uol.pgdoener.th1.business.dto.TableStructureDto;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.Converter;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.ConverterChain;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.ConverterFactory;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures.IStructure;
import de.uol.pgdoener.th1.business.mapper.StructureMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public class ConverterChainService {
    private final TableStructureDto tableStructure;
    private final ConverterChain converterChain;

    public ConverterChainService(TableStructureDto tableStructure) {
        this.tableStructure = tableStructure;
        this.converterChain = new ConverterChain();

        for (StructureDto structureDto : this.tableStructure.getStructures()) {
            IStructure structure = StructureMapper.toConverterStructure(structureDto);
            Converter converter = ConverterFactory.createConverter(structure);
            this.converterChain.add(converter);
        }
    }

    public ConverterResult performTransformation(InputFile inputFile) throws TransformationException {
        Objects.requireNonNull(converterChain.getFirst());
        String[][] transformedMatrix;
        try {
            String[][] inputArray = inputFile.asStringArray();
            if (inputArray.length == 0 || inputArray[0].length == 0) {
                log.debug("Empty input file");
                return new ConverterResult(tableStructure, inputArray);
            }
            transformedMatrix = converterChain.getFirst().handleRequest(inputArray);
        } catch (IOException e) {
            log.error("Error processing file: Could not read input file content", e);
            throw new TransformationException("Error processing file: Could not read input file content", e);
        }
        return new ConverterResult(tableStructure, transformedMatrix);
    }

}
