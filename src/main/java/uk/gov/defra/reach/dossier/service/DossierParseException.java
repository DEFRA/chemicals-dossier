package uk.gov.defra.reach.dossier.service;

public class DossierParseException extends RuntimeException {

  private static final long serialVersionUID = -8559549504591568485L;

  public DossierParseException(String message, Exception e) {
    super(message, e);
  }
}
