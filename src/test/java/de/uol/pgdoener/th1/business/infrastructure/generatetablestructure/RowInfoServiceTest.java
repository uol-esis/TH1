package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure;

import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.CellInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.RowInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.ValueType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RowInfoServiceTest {

    CellInfoService cellInfoService = new CellInfoService();
    RowInfoService rowInfoService = new RowInfoService(cellInfoService);

    @Test
    void testIsHeaderRow() {
        RowInfo row = constructRowInfo(ValueType.STRING, ValueType.STRING, ValueType.STRING);
        assertTrue(rowInfoService.isHeaderRow(row));

        row = constructRowInfo(ValueType.STRING, ValueType.EMPTY, ValueType.STRING);
        assertTrue(rowInfoService.isHeaderRow(row));

        row = constructRowInfo(ValueType.STRING, ValueType.EMPTY, ValueType.EMPTY);
        assertTrue(rowInfoService.isHeaderRow(row));

        row = constructRowInfo(ValueType.EMPTY, ValueType.EMPTY, ValueType.EMPTY);
        assertFalse(rowInfoService.isHeaderRow(row));
    }

    private RowInfo constructRowInfo(ValueType... valueTypes) {
        List<CellInfo> cellInfos = new ArrayList<>();
        for (int i = 0; i < valueTypes.length; i++) {
            cellInfos.add(new CellInfo(0, i, valueTypes[i]));
        }
        return new RowInfo(0, cellInfos);
    }

}
