package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures;


import java.util.Arrays;
import java.util.Objects;

public record RemoveHeaderStructure(
        Integer threshold,
        String[] blackList
) implements IStructure {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        RemoveHeaderStructure that = (RemoveHeaderStructure) o;
        return Arrays.equals(blackList, that.blackList) && Objects.equals(threshold, that.threshold);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(threshold);
        result = 31 * result + Objects.hashCode(blackList);
        return result;
    }

    @Override
    public String toString() {
        return "RemoveGroupedHeaderStructure{" +
                "threshold=" + threshold +
                ", blackList=" + Arrays.toString(blackList) +
                '}';
    }
}