package de.uol.pgdoener.th1.domain.tablestructure.service;

import de.uol.pgdoener.th1.domain.shared.exceptions.ServiceException;
import de.uol.pgdoener.th1.infastructure.persistence.repository.TableStructureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TableStructureValidationService {
    private final TableStructureRepository tableStructureRepository;

    /**
     * Validates that a TableStructure with the given ID exists.
     * <p>
     * If no entity is found, a {@link ServiceException} is thrown with a 404 status.
     *
     * @param id the ID of the TableStructure to validate
     * @throws ServiceException if no TableStructure with the given ID exists
     */
    public void validateTableStructureExists(Long id) {
        if (!tableStructureRepository.existsById(id)) {
            log.warn("Table structure with id {} not found", id);
            throw new ServiceException(
                    "The requested resource was not found.",
                    HttpStatus.NOT_FOUND,
                    "The TableStructure with the ID " + id + " does not exist.",
                    "Check if the ID is correct or if the entry exists"
            );
        }
    }

}
