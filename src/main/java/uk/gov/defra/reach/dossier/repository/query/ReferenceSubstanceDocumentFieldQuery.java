package uk.gov.defra.reach.dossier.repository.query;

import uk.gov.defra.reach.dossier.xpath.ManifestDocumentXPath;
import uk.gov.defra.reach.dossier.xpath.ReferenceSubstanceDocumentField;

public class ReferenceSubstanceDocumentFieldQuery extends AbstractFieldQuery<ReferenceSubstanceDocumentField> {

  public ReferenceSubstanceDocumentFieldQuery() {
    super(ManifestDocumentXPath.REFERENCE_SUBSTANCE_FILE_NAME);
  }
}
