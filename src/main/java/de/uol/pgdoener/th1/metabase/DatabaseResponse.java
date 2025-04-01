package de.uol.pgdoener.th1.metabase;

import lombok.Data;

import java.util.List;

/**
 * This class represents the response from Metabase when a list of connected databases is queried via the
 * {@link MBClient#listDatabases()} method.
 */
@Data
public class DatabaseResponse {

    private List<Database> data;

}
