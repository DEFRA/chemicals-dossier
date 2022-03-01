package uk.gov.defra.reach.dossier.xpath;

public interface DocumentField {

  /**
   * Returns an xpath which can be used to extract the field from the document
   * @return the xpath
   */
  String getXPath();

  /**
   * The name of the field as exposed via the GraphQL schema
   * @return the field name
   */
  String getFieldName();

}
