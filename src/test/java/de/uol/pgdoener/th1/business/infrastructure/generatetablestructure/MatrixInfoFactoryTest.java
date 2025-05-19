package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure;

import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class MatrixInfoFactoryTest {

    MatrixInfoFactory matrixInfoFactory = new MatrixInfoFactory();

    @Test
    void testCreate() {
        String[][] input = new String[][]{
                {"header1", "header2", "header3"},
                {"1", "2", "3"},
                {"4", "5", "6"}
        };

        MatrixInfo matrixInfo = matrixInfoFactory.create(input);

        assertEquals(new MatrixInfo(
                List.of(
                        new RowInfo(0,
                                List.of(
                                        new CellInfo(0, 0, "header1", ValueType.STRING),
                                        new CellInfo(0, 1, "header2", ValueType.STRING),
                                        new CellInfo(0, 2, "header3", ValueType.STRING)
                                )
                        ),
                        new RowInfo(1,
                                List.of(
                                        new CellInfo(1, 0, "1", ValueType.INTEGER),
                                        new CellInfo(1, 1, "2", ValueType.INTEGER),
                                        new CellInfo(1, 2, "3", ValueType.INTEGER)
                                )
                        ),
                        new RowInfo(2,
                                List.of(
                                        new CellInfo(2, 0, "4", ValueType.INTEGER),
                                        new CellInfo(2, 1, "5", ValueType.INTEGER),
                                        new CellInfo(2, 2, "6", ValueType.INTEGER)
                                )
                        )
                ),
                List.of(
                        new ColumnInfo(0,
                                List.of(
                                        new CellInfo(0, 0, "header1", ValueType.STRING),
                                        new CellInfo(1, 0, "1", ValueType.INTEGER),
                                        new CellInfo(2, 0, "4", ValueType.INTEGER)
                                )
                        ),
                        new ColumnInfo(1,
                                List.of(
                                        new CellInfo(0, 1, "header2", ValueType.STRING),
                                        new CellInfo(1, 1, "2", ValueType.INTEGER),
                                        new CellInfo(2, 1, "5", ValueType.INTEGER)
                                )
                        ),
                        new ColumnInfo(2,
                                List.of(
                                        new CellInfo(0, 2, "header3", ValueType.STRING),
                                        new CellInfo(1, 2, "3", ValueType.INTEGER),
                                        new CellInfo(2, 2, "6", ValueType.INTEGER)
                                )
                        )
                )
        ), matrixInfo);
        assertSame(matrixInfo.rowInfos().getFirst().cellInfos().getFirst(),
                matrixInfo.columnInfos().getFirst().cellInfos().getFirst());
    }

    @Test
    void testCreateWithLargeMatrix() {
        for (int iteration = 0; iteration < 10; iteration++) {

            String[][] input = new String[5000][5000];
            for (int i = 0; i < input.length; i++) {
                for (int j = 0; j < input[0].length; j++) {
                    input[i][j] = String.valueOf(i * input.length + j * iteration);
                }
            }

            long startTime = System.currentTimeMillis();
            MatrixInfo matrixInfo = matrixInfoFactory.create(input);
            long endTime = System.currentTimeMillis();
            System.out.println("Time taken to create large matrix: " + (endTime - startTime) + " ms");

            assertEquals(input.length, matrixInfo.rowInfos().size());
            assertEquals(input[0].length, matrixInfo.columnInfos().size());
        }
    }

}
