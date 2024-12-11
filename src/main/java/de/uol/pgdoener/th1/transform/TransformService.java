package de.uol.pgdoener.th1.transform;

import de.uol.pgdoener.th1.api.TransformApi;
import de.uol.pgdoener.th1.api.TransformApiDelegate;
import de.uol.pgdoener.th1.model.TableStructureDTO;
import de.uol.pgdoener.th1.transform.structure.TableStructureMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransformService implements TransformApiDelegate {

    private final TableStructureMapper tableStructureMapper;

    /**
     * GET /transform/get : Get Table Structure
     * Get a table structure by id
     *
     * @param tableStructureId (required)
     * @return Table structure found. (status code 200)
     * or Table structure not found. (status code 404)
     * @see TransformApi#getTableStructure
     */
    @Override
    public ResponseEntity<TableStructureDTO> getTableStructure(String tableStructureId) {
        return TransformApiDelegate.super.getTableStructure(tableStructureId);
    }

    /**
     * POST /transform/save : Save Table Structure
     * Save a table structure
     *
     * @param tableStructure (required)
     * @return Table structure saved. Returns the saved table structure containing an id. The id can be used to reference the table structure in the future. (status code 200)
     * or Invalid table structure. (status code 400)
     * @see TransformApi#saveTableStructure
     */
    @Override
    public ResponseEntity<TableStructureDTO> saveTableStructure(TableStructureDTO tableStructure) {
        return TransformApiDelegate.super.saveTableStructure(tableStructure);
    }

    /**
     * POST /transform/execute : Transform
     * Transform a table using a given table structure
     *
     * @param tableStructureId (required)
     * @return Table transformed. (status code 200)
     * or Table structure not found. (status code 404)
     * @see TransformApi#transform
     */
    @Override
    public ResponseEntity<Void> transform(String tableStructureId) {
        return TransformApiDelegate.super.transform(tableStructureId);
    }
}
