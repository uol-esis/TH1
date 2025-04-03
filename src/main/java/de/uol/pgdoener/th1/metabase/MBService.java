package de.uol.pgdoener.th1.metabase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MBService {

    private final MBClient mbClient;

    public void updateAllDatabases() throws MetabaseException {
        DatabaseResponse databaseResponse;
        try {
            databaseResponse = mbClient.listDatabases().orElseThrow();
        } catch (RestClientResponseException | NoSuchElementException e) {
            throw new MetabaseException("Error listing databases", e);
        }

        databaseResponse.getData().forEach(database -> {
            try {
                log.debug("Updating database {}", database);
                mbClient.updateDatabase(database.getId());
            } catch (RestClientResponseException e) {
                throw new MetabaseException("Error updating database " + database.getId(), e);
            }
        });
    }

}
