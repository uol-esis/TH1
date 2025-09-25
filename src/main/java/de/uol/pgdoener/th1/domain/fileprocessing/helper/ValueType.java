package de.uol.pgdoener.th1.domain.fileprocessing.helper;

import lombok.Getter;

@Getter
public enum ValueType {
    TEXT, NUMBER, DATE, TIMESTAMP, UUID, BOOLEAN;

    public boolean isHigherPriorityThan(ValueType other) {
        return this.ordinal() > other.ordinal();
    }

}
