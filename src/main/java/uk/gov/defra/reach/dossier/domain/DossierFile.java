package uk.gov.defra.reach.dossier.domain;

import lombok.Data;
import org.w3c.dom.Document;

@Data
public class DossierFile {

  /**
   * Name of the file within the i6z archive
   */
  private final String name;

  /**
   * The raw XML contents
   */
  private final Document xml;

}
