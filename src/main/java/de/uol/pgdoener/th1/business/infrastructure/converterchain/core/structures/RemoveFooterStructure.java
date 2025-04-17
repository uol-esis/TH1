package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures;

import lombok.NonNull;

import java.util.Arrays;
import java.util.Objects;

public record RemoveFooterStructure(
        Integer threshold,
        @NonNull String[] blackList
) implements IStructure {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        RemoveFooterStructure that = (RemoveFooterStructure) o;
        return Objects.equals(threshold, that.threshold) && Arrays.equals(blackList, that.blackList);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(threshold);
        result = 31 * result + Arrays.hashCode(blackList);
        return result;
    }

    @Override
    public String toString() {
        return "RemoveGroupedHeaderStructure{" +
                "rows=" + threshold +
                ", blackList=" + Arrays.toString(blackList) +
                '}';
    }

}

