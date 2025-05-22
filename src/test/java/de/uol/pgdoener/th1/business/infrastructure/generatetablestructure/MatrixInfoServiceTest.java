package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure;

import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.MatrixInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.factory.CellInfoFactory;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.factory.MatrixInfoFactory;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MatrixInfoServiceTest {

    CellInfoFactory cellInfoFactory = new CellInfoFactory();
    MatrixInfoFactory matrixInfoFactory = new MatrixInfoFactory(cellInfoFactory);

    CellInfoService cellInfoService = new CellInfoService();
    ColumnInfoService columnInfoService = new ColumnInfoService(cellInfoService);
    RowInfoService rowInfoService = new RowInfoService(cellInfoService);

    MatrixInfoService matrixInfoService = new MatrixInfoService(rowInfoService, columnInfoService, cellInfoService);

    @Test
    void testDetectGroupedHeaderCornerNotPresent() {
        String[][] input = {
                {"g1", "m1", "w1"},
                {"s1", "d1", "d2"},
                {"s2", "d3", "d4"}
        };
        MatrixInfo matrixInfo = matrixInfoFactory.create(input);

        Optional<Pair<Integer, Integer>> detected = matrixInfoService.detectGroupedHeaderCorner(matrixInfo);

        assertFalse(detected.isPresent());
    }

    @Test
    void testDetectGroupedHeaderCornerEmpty() {
        String[][] input = {
                {"  ", "m1", "w1"},
                {"s1", "d1", "d2"},
                {"s2", "d3", "d4"}
        };
        MatrixInfo matrixInfo = matrixInfoFactory.create(input);

        Optional<Pair<Integer, Integer>> detected = matrixInfoService.detectGroupedHeaderCorner(matrixInfo);

        assertFalse(detected.isPresent());
    }

    @Test
    void testDetectGroupedHeaderCornerOneRowOneColumn() {
        String[][] input = {
                {"g1", "m1", "w1"},
                {"sv", "  ", "  "},
                {"s1", "d1", "d2"},
                {"s2", "d3", "d4"}
        };
        MatrixInfo matrixInfo = matrixInfoFactory.create(input);

        Optional<Pair<Integer, Integer>> detected = matrixInfoService.detectGroupedHeaderCorner(matrixInfo);

        assertTrue(detected.isPresent());
        assertEquals(1, detected.get().getFirst());
        assertEquals(1, detected.get().getSecond());
    }

    @Test
    void testDetectGroupedHeaderCornerOneRowTwoColumn() {
        String[][] input = {
                {"g1", "  ", "m1", "w1"},
                {"sv", "sv", "  ", "  "},
                {"s1", "s3", "d1", "d2"},
                {"s2", "s4", "d3", "d4"}
        };
        MatrixInfo matrixInfo = matrixInfoFactory.create(input);

        Optional<Pair<Integer, Integer>> detected = matrixInfoService.detectGroupedHeaderCorner(matrixInfo);

        assertTrue(detected.isPresent());
        assertEquals(2, detected.get().getFirst());
        assertEquals(1, detected.get().getSecond());
    }

    @Test
    void testDetectGroupedHeaderCornerTwoRowTwoColumn() {
        String[][] input = {
                {"g1", "  ", "m1", ""},
                {"g2", "  ", "m2", "w1"},
                {"sv", "sv", "  ", "  "},
                {"s1", "s3", "d1", "d2"},
                {"s2", "s4", "d3", "d4"}
        };
        MatrixInfo matrixInfo = matrixInfoFactory.create(input);

        Optional<Pair<Integer, Integer>> detected = matrixInfoService.detectGroupedHeaderCorner(matrixInfo);

        assertTrue(detected.isPresent());
        assertEquals(2, detected.get().getFirst());
        assertEquals(2, detected.get().getSecond());
    }

    @Test
    void testDetectGroupedHeaderCornerTwoRowTwoColumnInvalid() {
        String[][] input = {
                {"g1", "in", "m1", ""},
                {"g2", "  ", "m2", "w1"},
                {"sv", "sv", "  ", "  "},
                {"s1", "s3", "d1", "d2"},
                {"s2", "s4", "d3", "d4"}
        };
        MatrixInfo matrixInfo = matrixInfoFactory.create(input);

        Optional<Pair<Integer, Integer>> detected = matrixInfoService.detectGroupedHeaderCorner(matrixInfo);

        assertFalse(detected.isPresent());
    }

    @Test
    void testDetectGroupedHeaderCornerTwoRowTwoColumnNoRowsToFill() {
        String[][] input = {
                {"g1", "  ", "m1", "in"},
                {"g2", "  ", "m2", "w1"},
                {"sv", "sv", "  ", "  "},
                {"s1", "s3", "d1", "d2"},
                {"s2", "s4", "d3", "d4"}
        };
        MatrixInfo matrixInfo = matrixInfoFactory.create(input);

        Optional<Pair<Integer, Integer>> detected = matrixInfoService.detectGroupedHeaderCorner(matrixInfo);

        assertFalse(detected.isPresent());
    }

}
