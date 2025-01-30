package de.uol.pgdoener.th1.business.infrastructure.csv_converter.core.structures;

public sealed interface IStructure permits
        RemoveGroupedHeaderStructure,
        RemoveColumnByIndexStructure,
        RemoveRowByIndexStructure,
        FillEmptyStructure {
}
