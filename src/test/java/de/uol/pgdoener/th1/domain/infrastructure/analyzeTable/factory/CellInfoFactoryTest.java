package de.uol.pgdoener.th1.domain.infrastructure.analyzeTable.factory;

import de.uol.pgdoener.th1.domain.analyzeTable.model.CellInfo;
import de.uol.pgdoener.th1.domain.analyzeTable.model.ValueType;
import de.uol.pgdoener.th1.domain.analyzeTable.factory.CellInfoFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        CellInfoFactory.class,
})
class CellInfoFactoryTest {

    @Autowired
    CellInfoFactory factory;

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
