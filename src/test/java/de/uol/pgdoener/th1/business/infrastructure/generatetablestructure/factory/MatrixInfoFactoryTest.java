package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.factory;

import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        MatrixInfoFactory.class,
        CellInfoFactory.class,
})
class MatrixInfoFactoryTest {

    @Autowired
    MatrixInfoFactory matrixInfoFactory;

    @Test
    void testCreate() {
        String[][] input = new String[][]{
                {"header1", "header2", ""},
                {"1.54", "2", "false"},
                {"4", " ", "6"},
                {"true", "8", "9.31"},
        };

        MatrixInfo matrixInfo = matrixInfoFactory.createParallel(input);

        assertEquals(new MatrixInfo(
                List.of(
                        new RowInfo(0,
                                List.of(
                                        new CellInfo(0, 0, ValueType.STRING),
                                        new CellInfo(0, 1, ValueType.STRING),
                                        new CellInfo(0, 2, ValueType.EMPTY)
                                )
                        ),
                        new RowInfo(1,
                                List.of(
                                        new CellInfo(1, 0, ValueType.NUMBER),
                                        new CellInfo(1, 1, ValueType.NUMBER),
                                        new CellInfo(1, 2, ValueType.BOOLEAN)
                                )
                        ),
                        new RowInfo(2,
                                List.of(
                                        new CellInfo(2, 0, ValueType.NUMBER),
                                        new CellInfo(2, 1, ValueType.EMPTY),
                                        new CellInfo(2, 2, ValueType.NUMBER)
                                )
                        ),
                        new RowInfo(3,
                                List.of(
                                        new CellInfo(3, 0, ValueType.BOOLEAN),
                                        new CellInfo(3, 1, ValueType.NUMBER),
                                        new CellInfo(3, 2, ValueType.NUMBER)
                                )
                        )
                ),
                List.of(
                        new ColumnInfo(0,
                                List.of(
                                        new CellInfo(0, 0, ValueType.STRING),
                                        new CellInfo(1, 0, ValueType.NUMBER),
                                        new CellInfo(2, 0, ValueType.NUMBER),
                                        new CellInfo(3, 0, ValueType.BOOLEAN)
                                )
                        ),
                        new ColumnInfo(1,
                                List.of(
                                        new CellInfo(0, 1, ValueType.STRING),
                                        new CellInfo(1, 1, ValueType.NUMBER),
                                        new CellInfo(2, 1, ValueType.EMPTY),
                                        new CellInfo(3, 1, ValueType.NUMBER)
                                )
                        ),
                        new ColumnInfo(2,
                                List.of(
                                        new CellInfo(0, 2, ValueType.EMPTY),
                                        new CellInfo(1, 2, ValueType.BOOLEAN),
                                        new CellInfo(2, 2, ValueType.NUMBER),
                                        new CellInfo(3, 2, ValueType.NUMBER)
                                )
                        )
                )
        ), matrixInfo);
        assertSame(matrixInfo.rowInfos().getFirst().cellInfos().getFirst(),
                matrixInfo.columnInfos().getFirst().cellInfos().getFirst());
    }

    @Test
    void testCreateWithLargeMatrix() {
        for (int iteration = 0; iteration < 2; iteration++) {
            String[][] input = new String[100_000][300];
            for (int i = 0; i < input.length; i++) {
                for (int j = 0; j < input[0].length; j++) {
                    input[i][j] = String.valueOf(i * input.length + j * iteration);
                }
            }

            long startTime = System.currentTimeMillis();
            MatrixInfo matrixInfo = matrixInfoFactory.createParallel(input);
            long endTime = System.currentTimeMillis();
            System.out.println("Time taken to create large matrix: " + (endTime - startTime) + " ms");

            assertEquals(input.length, matrixInfo.rowInfos().size());
            assertEquals(input[0].length, matrixInfo.columnInfos().size());
        }
    }

    @Test
    void testOneCell() {
        String[][] input = new String[][]{
                {"entry"}
        };

        MatrixInfo matrixInfo = matrixInfoFactory.createParallel(input);

        assertEquals(new MatrixInfo(
                List.of(
                        new RowInfo(0,
                                List.of(
                                        new CellInfo(0, 0, ValueType.STRING)
                                )
                        )
                ),
                List.of(
                        new ColumnInfo(0,
                                List.of(
                                        new CellInfo(0, 0, ValueType.STRING)
                                )
                        )
                )
        ), matrixInfo);
    }

}
