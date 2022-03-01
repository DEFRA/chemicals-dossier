package uk.gov.defra.reach.dossier.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.defra.reach.dossier.service.DossierIndexingService;

@RestController
public class DossierController {

  private final DossierIndexingService dossierIndexingService;

  @Autowired
  public DossierController(DossierIndexingService dossierIndexingService) {
    this.dossierIndexingService = dossierIndexingService;
  }

  @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
  public String root() {
    return "ok";
  }

  @PostMapping(value = "/dossier/index")
  public void indexDossier(@Valid @RequestBody DossierIndexRequest request) {
    dossierIndexingService.indexDossier(request.getStorageLocation());
  }

  @ExceptionHandler(DuplicateKeyException.class)
  @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Data integrity violation")
  public void handleConflict() {
    // Do nothing
  }

}
