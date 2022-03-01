package uk.gov.defra.reach.dossier.xpath;

/**
 * XPaths for extracting values from a "Substance" document
 */
public enum SubstanceDocumentField implements DocumentField {

  SUBSTANCE_NAME("substanceName", "/*:Document/*:Content/*:SUBSTANCE/*:ChemicalName/text()"),

  OWNER_LEGAL_ENTITY_CONFIDENTIALITY("ownerLegalEntityConfidentiality",
      "/*:Document/*:Content/*:SUBSTANCE/*:OwnerLegalEntityProtection/*:confidentiality/text()"),

  REFERENCE_SUBSTANCE_CONFIDENTIALITY("referenceSubstanceConfidentiality",
      "/*:Document/*:Content/*:SUBSTANCE/*:ReferenceSubstance/*:Protection/*:confidentiality/text()");

  private final String fieldName;

  private final String xpath;

  SubstanceDocumentField(String fieldName, String xpath) {
    this.fieldName = fieldName;
    this.xpath = xpath;
  }

  public String getXPath() {
    return xpath;
  }

  @Override
  public String getFieldName() {
    return fieldName;
  }

  public static SubstanceDocumentField fromFieldName(String fieldName) {
    for (SubstanceDocumentField value : values()) {
      if (value.fieldName.equals(fieldName)) {
        return value;
      }
    }
    return null;
  }
}
