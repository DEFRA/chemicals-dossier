package uk.gov.defra.reach.dossier.service;

public class DossierParseException extends RuntimeException {

  public DossierParseException(String message, Exception e) {
    super(message, e);
  }
}
