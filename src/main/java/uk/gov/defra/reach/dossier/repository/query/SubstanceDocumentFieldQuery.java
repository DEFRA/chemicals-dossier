package uk.gov.defra.reach.dossier.repository.query;

import uk.gov.defra.reach.dossier.xpath.ManifestDocumentXPath;
import uk.gov.defra.reach.dossier.xpath.SubstanceDocumentField;

public class SubstanceDocumentFieldQuery extends AbstractFieldQuery<SubstanceDocumentField> {

  public SubstanceDocumentFieldQuery() {
    super(ManifestDocumentXPath.SUBSTANCE_FILE_NAME);
  }
}
