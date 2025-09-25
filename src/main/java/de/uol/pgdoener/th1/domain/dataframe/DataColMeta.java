package de.uol.pgdoener.th1.domain.dataframe;

import de.uol.pgdoener.th1.domain.fileprocessing.helper.ValueType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataColMeta {
    private final String name;
    private ValueType valueType;

    public DataColMeta(String name) {
        this.name = name;
    }

    public void mergeType(ValueType newType) {
        if (newType == null) return;
        if (this.valueType == null || newType.isHigherPriorityThan(this.valueType)) {
            this.valueType = newType;
        }
    }
}
