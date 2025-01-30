package de.uol.pgdoener.th1.business.service;

import de.uol.pgdoener.th1.business.dto.StructureDto;
import de.uol.pgdoener.th1.business.dto.TableStructureDto;
import de.uol.pgdoener.th1.business.dto.TableStructureSummaryDto;
import de.uol.pgdoener.th1.business.mapper.StructureMapper;
import de.uol.pgdoener.th1.business.mapper.TableStructureMapper;
import de.uol.pgdoener.th1.data.entity.Structure;
import de.uol.pgdoener.th1.data.entity.TableStructure;
import de.uol.pgdoener.th1.data.repository.StructureRepository;
import de.uol.pgdoener.th1.data.repository.TableStructureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.HttpServerErrorException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TableStructureService {

    private final TableStructureRepository tableStructureRepository;
    private final StructureRepository structureRepository;
    private final PlatformTransactionManager transactionManager;

    public void create(TableStructureDto tableStructureDto) {
        List<StructureDto> structureDtoList = tableStructureDto.getStructures();
        TableStructure tableStructure = TableStructureMapper.toEntity(tableStructureDto);

        new TransactionTemplate(transactionManager).execute(
                status -> {
                    try {
                        TableStructure savedTableStructure = tableStructureRepository.save(tableStructure);
                        for (int i = 0; i < structureDtoList.size(); i++) {
                            StructureDto structureDto = structureDtoList.get(i);
                            Structure structure = StructureMapper.toEntity(structureDto, i, savedTableStructure.getId());
                            structureRepository.save(structure);
                        }
                    } catch (Exception e) {
                        status.setRollbackOnly();
                        throw new HttpServerErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
                    }
                    return null;
                }
        );
    }

    /// TODO: Möglicherweise weniger zurück geben. Jetzt werden alle Informationen zurück gegeben.
    public List<TableStructureSummaryDto> getAll() {
        Iterable<TableStructure> tableStructures = tableStructureRepository.findAll();

        List<TableStructureSummaryDto> tableStructuresDto = new ArrayList<>();
        tableStructures.forEach(tableStructure -> {
            List<Structure> structureList = structureRepository.findByTableStructureId(tableStructure.getId());
            TableStructureSummaryDto tableStructureSummaryDto = TableStructureMapper.toSummaryDto(tableStructure, structureList);
            tableStructuresDto.add(tableStructureSummaryDto);
        });

        return tableStructuresDto;
    }

    public TableStructureDto getById(Long id) {
        TableStructure tableStructure = tableStructureRepository.findById(id).orElse(null);
        if (tableStructure == null) {
            throw new HttpServerErrorException(HttpStatus.BAD_REQUEST, "kein Eintrag");
        }

        List<Structure> structureList = structureRepository.findByTableStructureId(tableStructure.getId());
        return TableStructureMapper.toDto(tableStructure, structureList);
    }

}