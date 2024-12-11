package de.uol.pgdoener.th1.transform.structure;

import de.uol.pgdoener.th1.model.TableStructureDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TableStructureMapper {

    TableStructure toTableStructure(TableStructureDTO dto);

    TableStructureDTO toDTO(TableStructure entity);

}
