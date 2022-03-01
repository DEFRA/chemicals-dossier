package uk.gov.defra.reach.dossier.resolvers;

import graphql.kickstart.tools.GraphQLQueryResolver;
import graphql.language.Field;
import graphql.language.Selection;
import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.defra.reach.dossier.dto.Dossier;
import uk.gov.defra.reach.dossier.repository.DossierFileRepository;
import uk.gov.defra.reach.dossier.repository.DossierQueryRepository;
import uk.gov.defra.reach.dossier.repository.query.DossierDocumentFieldQuery;
import uk.gov.defra.reach.dossier.xpath.DossierDocumentField;

/**
 * {@code GraphQLQueryResolver} for resolvining Dossiers and simple dossier fields.
 */
@Component
@Slf4j
public class DossierQueryResolver implements GraphQLQueryResolver {

  private final DossierFileRepository dossierFileRepository;
  private final DossierQueryRepository dossierQueryRepository;

  public DossierQueryResolver(DossierFileRepository dossierFileRepository, DossierQueryRepository dossierQueryRepository) {
    this.dossierFileRepository = dossierFileRepository;
    this.dossierQueryRepository = dossierQueryRepository;
  }

  public Dossier getDossier(String id, DataFetchingEnvironment env) {
    Dossier dossier = dossierFileRepository.getDossierById(UUID.fromString(id));
    return getDossierDetails(dossier, env);
  }

  public Dossier getTempDossier(String storageLocation, DataFetchingEnvironment env) {
    Dossier dossier = dossierFileRepository.getDossierByStorageLocation(storageLocation);
    return getDossierDetails(dossier, env);
  }

  private Dossier getDossierDetails(Dossier dossier, DataFetchingEnvironment env) {
    if (dossier == null) {
      return null;
    }

    DossierDocumentFieldQuery query = new DossierDocumentFieldQuery();

    List<Selection> selections = env.getField().getSelectionSet().getSelections();
    for (Selection selection : selections) {
      if (selection instanceof Field) {
        Field field = (Field) selection;
        DossierDocumentField dossierXpath = DossierDocumentField.fromFieldName(field.getName());
        if (dossierXpath != null) {
          query.addField(dossierXpath);
        }
      }
    }

    if (query.hasFields()) {
      Map<String, String> results;
      if (dossier.getStorageLocation() != null) {
        results = dossierQueryRepository.queryTempDossier(query, dossier.getStorageLocation());
      } else {
        results = dossierQueryRepository.queryDossier(query, dossier.getId());
      }

      if (results != null) {
        Dossier.DossierBuilder builder = Dossier.builder().id(dossier.getId());
        builder.storageLocation(dossier.getStorageLocation());
        builder.dossierType(results.get(DossierDocumentField.DOSSIER_TYPE.getFieldName()));
        builder.jointSubmission("true".equals(results.get(DossierDocumentField.JOINT_SUBMISSION.getFieldName())));
        builder.transportIsolated(results.get(DossierDocumentField.TRANSPORTED_ISOLATED.getFieldName()));
        builder.onSiteIsolated(results.get(DossierDocumentField.ONSITE_ISOLATED.getFieldName()));
        builder.tonnageBand(results.get(DossierDocumentField.TONNAGE_BAND.getFieldName()));
        return builder.build();
      }
    }
    return dossier;
  }

}
