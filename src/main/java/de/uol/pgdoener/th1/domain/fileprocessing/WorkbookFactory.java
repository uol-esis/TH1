package de.uol.pgdoener.th1.domain.fileprocessing;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.io.InputStream;

@FunctionalInterface
public interface WorkbookFactory {
    Workbook create(InputStream inputStream) throws IOException;
}
