package uk.gov.defra.reach.dossier.xpath;

/**
 * XPaths for extracting values from a "Reference Substance" document
 */
public enum ReferenceSubstanceDocumentField implements DocumentField {

  REFERENCE_SUBSTANCE_NAME("referenceSubstanceName",
      "(/*:Document/*:Content/*:REFERENCE_SUBSTANCE/*:GeneralInfo/*:ReferenceSubstanceName/text()," +
             " /*:Document/*:Content/*:REFERENCE_SUBSTANCE/*:ReferenceSubstanceName/text())"),

  MOLECULAR_FORMULA("molecularFormula", "/*:Document/*:Content/*:REFERENCE_SUBSTANCE/*:MolecularStructuralInfo/*:MolecularFormula/text()"),

  EC_NUMBER("ecNumber", "/*:Document/*:Content/*:REFERENCE_SUBSTANCE/*:Inventory/*:InventoryEntry/*:entry[*:inventoryCode=\"EC\"]/*:numberInInventory/text()"),

  CAS_NUMBER("casNumber",
      "(/*:Document/*:Content/*:REFERENCE_SUBSTANCE/*:ReferenceSubstanceInfo/*:CASInfo/*:CASNumber/text()," +
             " /*:Document/*:Content/*:REFERENCE_SUBSTANCE/*:Inventory/*:CASNumber/text())"),

  IUPAC_NAME("iupacName",
      "(/*:Document/*:Content/*:REFERENCE_SUBSTANCE/*:ReferenceSubstanceInfo/*:IupacName/text()," +
             " /*:Document/*:Content/*:REFERENCE_SUBSTANCE/*:IupacName/text())"),

  REFERENCE_SUBSTANCE_INFO_CONFIDENTIALITY("referenceSubstanceInfoConfidentiality",
      "(/*:Document/*:Content/*:REFERENCE_SUBSTANCE/*:ReferenceSubstanceInfo/*:DataProtection/*:confidentiality/text()," +
             " /*:Document/*:Content/*:REFERENCE_SUBSTANCE/*:DataProtection/*:confidentiality/text())"),

  MOLECULAR_STRUCTURE_CONFIDENTIALITY("molecularStructureConfidentiality",
      "/*:Document/*:Content/*:REFERENCE_SUBSTANCE/*:MolecularStructuralInfo/*:DataProtection/*:confidentiality/text()");

  private final String fieldName;

  private final String xpath;

  ReferenceSubstanceDocumentField(String fieldName, String xpath) {
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

  public static ReferenceSubstanceDocumentField fromFieldName(String fieldName) {
    for (ReferenceSubstanceDocumentField value : values()) {
      if (value.fieldName.equals(fieldName)) {
        return value;
      }
    }
    return null;
  }
}
