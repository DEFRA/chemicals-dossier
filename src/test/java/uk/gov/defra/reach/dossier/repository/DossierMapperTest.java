package uk.gov.defra.reach.dossier.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.defra.reach.dossier.dto.Dossier;

@ExtendWith(MockitoExtension.class)
class DossierMapperTest {

  @Mock
  private ResultSet rs;

  UUID dossierId = UUID.randomUUID();
  String storageLocation = UUID.randomUUID().toString();

  DossierMapper mapper = new DossierMapper();


  @Test
  @SneakyThrows
  void mapRow_shoudlMapBothValuesWhenPresent() {
    when(rs.getString("DossierId")).thenReturn(dossierId.toString());
    when(rs.getString("storageLocation")).thenReturn(storageLocation);
    Dossier dossier = mapper.mapRow(rs, 0);

    assertThat(dossier.getId()).isEqualTo(dossierId);
    assertThat(dossier.getStorageLocation()).isEqualTo(storageLocation);
  }

  @Test
  @SneakyThrows
  void mapRow_shoudlMapStorageLocationWhenPresent() {
    when(rs.getString("DossierId")).thenReturn(null);
    when(rs.getString("storageLocation")).thenReturn(storageLocation);
    Dossier dossier = mapper.mapRow(rs, 0);

    assertThat(dossier.getId()).isEqualTo(null);
    assertThat(dossier.getStorageLocation()).isEqualTo(storageLocation);
  }

}
