package de.uol.pgdoener.th1.metabase;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Optional;

/**
 * This class is a client for the Metabase API.
 * It provides methods to list databases and to notify Metabase to update its cache of a database.
 *
 * @see <a href="https://www.metabase.com/docs/latest/api-documentation.html">Metabase API Documentation</a>
 */
@Slf4j
@Component
public class MBClient {

    private final RestClient restClient;

    /**
     * The API key used to notify Metabase to update its cache.
     * It can be set in Metabase using the "MB_API_KEY" environment variable.
     */
    @Value("${mb.api.key}")
    private String notifyKey;

    /**
     * This key is used for all other API calls.
     * This key has to be created manually in Metabase.
     * See <a href="https://www.metabase.com/docs/latest/people-and-groups/api-keys#create-an-api-key">here</a> for more information.
     */
    @Value("${mb.api.general.key}")
    private String generalKey;

    public MBClient(@Value("${mb.api.basePath}") String basePath) {
        this.restClient = RestClient.builder()
                .baseUrl(basePath)
                .build();
    }

    /**
     * This method returns a list of databases known to the given Metabase instance.
     * The list is queried using the /api/database endpoint.
     *
     * @return a list of databases
     * @throws RestClientResponseException if the request fails with a 4xx or 5xx status code
     */
    public Optional<DatabaseResponse> listDatabases() throws RestClientResponseException {
        ResponseEntity<DatabaseResponse> response = restClient.get()
                .uri("/database")
                .header("X-API-KEY", generalKey)
                .retrieve()
                .toEntity(DatabaseResponse.class);

        if (response.getStatusCode().is2xxSuccessful())
            return Optional.ofNullable(response.getBody());
        log.debug("Could not list databases. Statuscode: {}", response.getStatusCode());
        return Optional.empty();
    }

    /**
     * Notifies Metabase that it should update its cache of the database with the given id.
     * The id can be obtained using the {@link MBClient#listDatabases()} method.
     * <p>
     * This implementation of updating databases is based on the Metabase <a href="https://www.metabase.com/docs/latest/databases/sync-scan#syncing-and-scanning-using-the-api">Documentation</a>
     *
     * @param id the id of the database to update
     * @throws RestClientResponseException if the request fails with a 4xx or 5xx status code
     */
    public void updateDatabase(String id) throws RestClientResponseException {
        ResponseEntity<Void> response = restClient.post()
                .uri("/notify/db/{id}", id)
                .header("X-METABASE-APIKEY", notifyKey)
                .retrieve()
                .toBodilessEntity();

        if (response.getStatusCode().is2xxSuccessful())
            log.debug("Database {} updated", id);
        else
            log.debug("Could not update database {}. Statuscode: {}", id, response.getStatusCode());
    }

}
