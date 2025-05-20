package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.factory;

import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.CellInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.RowInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class RowInfoFactory {

    private final CellInfoFactory cellInfoFactory;

    public RowInfo create(int rowIndex, String[] row) {
        List<CellInfo> cellInfos = new ArrayList<>(row.length);

        for (int i = 0; i < row.length; i++) {
            CellInfo cellInfo = cellInfoFactory.create(rowIndex, i, row[i]);
            cellInfos.add(cellInfo);
        }

        return new RowInfo(rowIndex, cellInfos);
    }
}
