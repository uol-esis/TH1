package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure;

import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.CellInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.ValueType;
import org.springframework.stereotype.Service;

@Service
public class CellInfoService {

    public boolean isString(CellInfo cellInfo) {
        return cellInfo.valueType() == ValueType.STRING;
    }

    public boolean hasEntry(CellInfo cellInfo) {
        return cellInfo.valueType() == ValueType.NULL;
    }
}
