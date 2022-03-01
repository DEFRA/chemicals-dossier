package uk.gov.defra.reach.dossier.xpath;

/**
 * XPaths for extracting values from a "Dossier" document
 */
public enum DossierDocumentField implements DocumentField {

  DOSSIER_TYPE("dossierType", "/*:Document/*:PlatformMetadata/*:documentSubType/text()"),

  JOINT_SUBMISSION("jointSubmission", "/*:Document/*:Content//*:SubmissionType/*:JointSubmission/text()"),

  TONNAGE_BAND("tonnageBand", "/*:Document/*:Content//*:SubmissionType/*:TonnageBandsOfRegistrant/*:TonnageBand/*:value/text()"),

  ONSITE_ISOLATED("onsiteIsolated", "/*:Document/*:Content//*:SubmissionType/*:TonnageBandsOfRegistrant/*:OnSiteIsolated/*:value/text()"),

  TRANSPORTED_ISOLATED("transportIsolated", "/*:Document/*:Content//*:SubmissionType/*:TonnageBandsOfRegistrant/*:TransportedIsolated/*:value/text()");

  private final String fieldName;

  private final String xpath;

  DossierDocumentField(String fieldName, String xpath) {
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

  public static final DossierDocumentField fromFieldName(String fieldName) {
    for (DossierDocumentField value : values()) {
      if (value.fieldName.equals(fieldName)) {
        return value;
      }
    }
    return null;
  }
}
