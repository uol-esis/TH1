package de.uol.pgdoener.th1.application.service;

import de.uol.pgdoener.th1.application.dto.StructureDto;
import de.uol.pgdoener.th1.application.mapper.StructureMapper;
import de.uol.pgdoener.th1.infastructure.persistence.entity.Structure;
import de.uol.pgdoener.th1.infastructure.persistence.repository.StructureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StructureService {
    private final StructureRepository structureRepository;

    public void createStructures(List<StructureDto> structures, Long structureId) {
        List<Structure> structureList = StructureMapper.toEntity(structures, structureId);
        structureRepository.saveAll(structureList);
    }
}
