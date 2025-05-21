package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure;

import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.*;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.factory.CellInfoFactory;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.factory.MatrixInfoFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class MatrixInfoFactoryTest {

    CellInfoFactory cellInfoFactory = new CellInfoFactory();
    MatrixInfoFactory matrixInfoFactory = new MatrixInfoFactory(cellInfoFactory);

    @Test
    void testCreate() {
        String[][] input = new String[][]{
                {"header1", "header2", "header3"},
                {"1", "2", "3"},
                {"4", "5", "6"},
                {"7", "8", "9"},
        };

        MatrixInfo matrixInfo = matrixInfoFactory.createParallel(input);

        assertEquals(new MatrixInfo(
                List.of(
                        new RowInfo(0,
                                List.of(
                                        new CellInfo(0, 0, ValueType.STRING),
                                        new CellInfo(0, 1, ValueType.STRING),
                                        new CellInfo(0, 2, ValueType.STRING)
                                )
                        ),
                        new RowInfo(1,
                                List.of(
                                        new CellInfo(1, 0, ValueType.NUMBER),
                                        new CellInfo(1, 1, ValueType.NUMBER),
                                        new CellInfo(1, 2, ValueType.NUMBER)
                                )
                        ),
                        new RowInfo(2,
                                List.of(
                                        new CellInfo(2, 0, ValueType.NUMBER),
                                        new CellInfo(2, 1, ValueType.NUMBER),
                                        new CellInfo(2, 2, ValueType.NUMBER)
                                )
                        ),
                        new RowInfo(3,
                                List.of(
                                        new CellInfo(3, 0, ValueType.NUMBER),
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
                                        new CellInfo(3, 0, ValueType.NUMBER)
                                )
                        ),
                        new ColumnInfo(1,
                                List.of(
                                        new CellInfo(0, 1, ValueType.STRING),
                                        new CellInfo(1, 1, ValueType.NUMBER),
                                        new CellInfo(2, 1, ValueType.NUMBER),
                                        new CellInfo(3, 1, ValueType.NUMBER)
                                )
                        ),
                        new ColumnInfo(2,
                                List.of(
                                        new CellInfo(0, 2, ValueType.STRING),
                                        new CellInfo(1, 2, ValueType.NUMBER),
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
        for (int iteration = 0; iteration < 20; iteration++) {

            String[][] input = new String[6000][5000];
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

}
