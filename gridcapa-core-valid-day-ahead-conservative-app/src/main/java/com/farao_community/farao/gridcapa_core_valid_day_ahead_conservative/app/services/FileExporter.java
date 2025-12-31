package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.services;

import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.exception.CoreValidD2ConservativeInternalException;
import com.farao_community.farao.minio_adapter.starter.MinioAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.IVA_BRANCH_JSON_FILE_NAME;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.IVA_RESULT_FILE_TYPE;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.MINIO_DESTINATION_PATH_FORMAT;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.PROCESS_NAME;

@Service
public class FileExporter {

    private final MinioAdapter minioAdapter;

    @Value("${core-valid-day-ahead-conservative-runner.zone-id}")
    private String zoneId;

    public FileExporter(MinioAdapter minioAdapter) {
        this.minioAdapter = minioAdapter;
    }

    public void uploadOutputToMinio(final byte[] outputFile, final OffsetDateTime timestamp) {
        try (final InputStream inputStream = new ByteArrayInputStream(outputFile)) {
            final String minioOutputPath = makeDestinationMinioPath(timestamp) + IVA_BRANCH_JSON_FILE_NAME;
            minioAdapter.uploadOutputForTimestamp(minioOutputPath, inputStream, PROCESS_NAME, IVA_RESULT_FILE_TYPE, timestamp);
        } catch (final IOException e) {
            throw new CoreValidD2ConservativeInternalException("Error during output file upload", e);
        }
    }

    private String makeDestinationMinioPath(final OffsetDateTime offsetDateTime) {
        final ZonedDateTime targetDateTime = offsetDateTime.atZoneSameInstant(ZoneId.of(zoneId));
        final DateTimeFormatter df = DateTimeFormatter.ofPattern(MINIO_DESTINATION_PATH_FORMAT);
        return df.format(targetDateTime);
    }
}
