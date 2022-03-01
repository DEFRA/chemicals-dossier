package uk.gov.defra.reach.dossier.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;
import uk.gov.defra.reach.dossier.dto.Dossier;

public class DossierMapper implements RowMapper<Dossier> {

  @Override
  public Dossier mapRow(ResultSet resultSet, int i) throws SQLException {
    return Dossier.builder()
        .id(Optional.ofNullable(resultSet.getString("DossierId")).map(UUID::fromString).orElse(null))
        .storageLocation(resultSet.getString("storageLocation"))
        .build();
  }
}
