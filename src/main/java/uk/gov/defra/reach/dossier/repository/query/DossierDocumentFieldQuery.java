package uk.gov.defra.reach.dossier.repository.query;

import uk.gov.defra.reach.dossier.xpath.DossierDocumentField;
import uk.gov.defra.reach.dossier.xpath.ManifestDocumentXPath;

public class DossierDocumentFieldQuery extends AbstractFieldQuery<DossierDocumentField> {

  public DossierDocumentFieldQuery() {
    super(ManifestDocumentXPath.DOSSIER_FILE_NAME);
  }
}
