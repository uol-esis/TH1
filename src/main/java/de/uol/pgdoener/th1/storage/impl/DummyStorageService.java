package de.uol.pgdoener.th1.storage.impl;

import de.uol.pgdoener.th1.storage.StorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@ConditionalOnProperty(value = "th1.objectstorage.type", havingValue = "none", matchIfMissing = true)
public class DummyStorageService implements StorageService {

    @PostConstruct
    private void init() {
        log.warn("No object storage configured");
    }

    @Override
    public Optional<UUID> store(InputStream inputStream) {
        throw new UnsupportedOperationException("Cannot store: No object storage configured");
    }

    @Override
    public Optional<InputStream> load(UUID objectID) {
        throw new UnsupportedOperationException("Cannot load: No object storage configured");
    }
}
