package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures;

import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.MatrixInfo;

public record RemoveOverheadStructure(
        MatrixInfo matrixInfo
) implements IStructure {
}
