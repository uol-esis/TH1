package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures;

public sealed interface IStructure permits
        RemoveGroupedHeaderStructure,
        RemoveColumnByIndexStructure,
        RemoveRowByIndexStructure,
        FillEmptyStructure {
}
