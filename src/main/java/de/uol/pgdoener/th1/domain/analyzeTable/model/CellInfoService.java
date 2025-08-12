package de.uol.pgdoener.th1.domain.analyzeTable.model;

import org.springframework.stereotype.Service;

@Service
public class CellInfoService {

    public boolean isString(CellInfo cellInfo) {
        return cellInfo.valueType() == ValueType.STRING;
    }

    public boolean isEmpty(CellInfo cellInfo) {
        return cellInfo.valueType() == ValueType.EMPTY;
    }

    public boolean hasEntry(CellInfo cellInfo) {
        return !isEmpty(cellInfo);
    }
}
