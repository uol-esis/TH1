package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures;

public sealed interface IStructure permits FillEmptyRowStructure, HeaderRowStructure, RemoveColumnByIndexStructure, RemoveFooterStructure, RemoveGroupedHeaderStructure, RemoveRowByIndexStructure {
}
