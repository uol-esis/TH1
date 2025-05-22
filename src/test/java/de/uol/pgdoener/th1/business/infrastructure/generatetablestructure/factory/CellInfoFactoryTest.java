package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.factory;

import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.CellInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.ValueType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CellInfoFactoryTest {

    CellInfoFactory factory = new CellInfoFactory();

    @Test
    void testCreateString() {
        CellInfo cellInfo = factory.create(0, 0, "test");
        assertEquals(0, cellInfo.rowIndex());
        assertEquals(0, cellInfo.columnIndex());
        assertEquals(ValueType.STRING, cellInfo.valueType());
    }

    @Test
    void testCreateNumber() {
        CellInfo cellInfo = factory.create(4, 1, "123.45");
        assertEquals(4, cellInfo.rowIndex());
        assertEquals(1, cellInfo.columnIndex());
        assertEquals(ValueType.NUMBER, cellInfo.valueType());
    }

    @Test
    void testCreateBoolean() {
        CellInfo cellInfo = factory.create(2, 3, "true");
        assertEquals(2, cellInfo.rowIndex());
        assertEquals(3, cellInfo.columnIndex());
        assertEquals(ValueType.BOOLEAN, cellInfo.valueType());

        cellInfo = factory.create(5, 6, "false");
        assertEquals(5, cellInfo.rowIndex());
        assertEquals(6, cellInfo.columnIndex());
        assertEquals(ValueType.BOOLEAN, cellInfo.valueType());
    }

    @Test
    void testCreateEmpty() {
        CellInfo cellInfo = factory.create(1, 2, "");
        assertEquals(1, cellInfo.rowIndex());
        assertEquals(2, cellInfo.columnIndex());
        assertEquals(ValueType.EMPTY, cellInfo.valueType());
    }

    @Test
    void testCreateWhitespace() {
        CellInfo cellInfo = factory.create(3, 4, " ");
        assertEquals(3, cellInfo.rowIndex());
        assertEquals(4, cellInfo.columnIndex());
        assertEquals(ValueType.EMPTY, cellInfo.valueType());
    }

}
