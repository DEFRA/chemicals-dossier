package uk.gov.defra.reach.dossier.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.gov.defra.reach.dossier.dto.Dossier;

@ExtendWith(MockitoExtension.class)
class DossierFileRepositoryTest {

  @Mock
  private JdbcTemplate jdbcTemplate;

  @InjectMocks
  DossierFileRepository dossierFileRepository;

  @Test
  void getDossierByStorageLocation_queriesForObject() {
    String storageLocation = UUID.randomUUID().toString();
    dossierFileRepository.getDossierByStorageLocation(storageLocation);
    verify(jdbcTemplate).queryForObject(any(String.class), any(DossierMapper.class), eq(storageLocation));
  }

  @Test
  void getDossierByStorageLocation_returnsNullOnError() {
    String storageLocation = UUID.randomUUID().toString();
    when(jdbcTemplate.queryForObject(any(String.class), any(DossierMapper.class), any(String.class)))
        .thenThrow(new EmptyResultDataAccessException(0));
    Dossier dossier = dossierFileRepository.getDossierByStorageLocation(storageLocation);
    verify(jdbcTemplate).queryForObject(any(String.class), any(DossierMapper.class), eq(storageLocation));
    assertThat(dossier).isNull();
  }

  @Test
  void getDossierById_queriesForObject() {
    UUID dossierId = UUID.randomUUID();
    dossierFileRepository.getDossierById(dossierId);
    verify(jdbcTemplate).queryForObject(any(String.class), any(DossierMapper.class), eq(dossierId));
  }

  @Test
  void getDossierById_returnsNullOnError() {
    UUID dossierId = UUID.randomUUID();
    when(jdbcTemplate.queryForObject(any(String.class), any(DossierMapper.class), any(UUID.class)))
        .thenThrow(new EmptyResultDataAccessException(0));
    Dossier dossier = dossierFileRepository.getDossierById(dossierId);
    verify(jdbcTemplate).queryForObject(any(String.class), any(DossierMapper.class), eq(dossierId));
    assertThat(dossier).isNull();
  }

  @Test
  void deleteByStorageLocation_shouldCallJdbcTemplateUpdate_WithCorrectParams() {
    String storageLocation = UUID.randomUUID().toString();
    dossierFileRepository.deleteByStorageLocation(storageLocation);
    verify(jdbcTemplate).update(any(String.class), eq(storageLocation));
  }

  @Test
  void deleteByDossierId_shouldCallJdbcTemplateUpdate_WithCorrectParams() {
    UUID dossierId = UUID.randomUUID();
    dossierFileRepository.deleteByDossierId(dossierId);
    verify(jdbcTemplate).update(any(String.class), eq(dossierId));
  }
}
