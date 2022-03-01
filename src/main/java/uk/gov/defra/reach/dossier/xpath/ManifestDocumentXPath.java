package uk.gov.defra.reach.dossier.xpath;

/**
 * XPaths for extracting values from a dossier manifest document
 */
public enum ManifestDocumentXPath {

  DOSSIER_FILE_NAME("/*:manifest/*:contained-documents/*:document[@id=/*:manifest/*:base-document-uuid/text()]/*:name/@*:href"),

  SUBSTANCE_FILE_NAME("/*:manifest/*:contained-documents/*:document[@id=/*:manifest/*:contained-documents/*:document[@id=/*:manifest/*:base-document-uuid/text()]/*:links/*:link[*:ref-type/text()=\"DOSSIER_SUBJECT\"]/*:ref-uuid/text()]/*:name/@*:href"),

  REFERENCE_SUBSTANCE_FILE_NAME("/*:manifest/*:contained-documents/*:document[@id=/*:manifest/*:contained-documents/*:document[@id=/*:manifest/*:contained-documents/*:document[@id=/*:manifest/*:base-document-uuid/text()]/*:links/*:link[*:ref-type/text()=\"DOSSIER_SUBJECT\"]/*:ref-uuid/text()]/*:links/*:link[*:ref-type=\"REFERENCE\"]/*:ref-uuid/text()]/*:name/@*:href");

  private final String xpath;

  ManifestDocumentXPath(String xpath) {
    this.xpath = xpath;
  }

  public String getXPath() {
    return xpath;
  }
}
