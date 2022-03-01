package uk.gov.defra.reach.dossier.resolvers;

import graphql.kickstart.tools.GraphQLResolver;
import graphql.language.Field;
import graphql.language.Selection;
import graphql.language.SelectionSet;
import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.defra.reach.dossier.dto.Dossier;
import uk.gov.defra.reach.dossier.dto.ReasonForUpdate;
import uk.gov.defra.reach.dossier.dto.ReferenceSubstance;
import uk.gov.defra.reach.dossier.dto.Substance;
import uk.gov.defra.reach.dossier.repository.DossierQueryRepository;
import uk.gov.defra.reach.dossier.repository.query.AbstractFieldQuery;
import uk.gov.defra.reach.dossier.repository.query.Query;
import uk.gov.defra.reach.dossier.repository.query.ReasonForRequestedUpdateQuery;
import uk.gov.defra.reach.dossier.repository.query.ReasonForSpontaneousUpdateQuery;
import uk.gov.defra.reach.dossier.repository.query.ReferenceSubstanceDocumentFieldQuery;
import uk.gov.defra.reach.dossier.repository.query.SubstanceDocumentFieldQuery;
import uk.gov.defra.reach.dossier.xpath.DocumentField;
import uk.gov.defra.reach.dossier.xpath.ReferenceSubstanceDocumentField;
import uk.gov.defra.reach.dossier.xpath.SubstanceDocumentField;

/**
 * GraphQL resolver for resolving complex fields within a {@link Dossier} object
 */
@Component
@Slf4j
public class DossierFieldResolver implements GraphQLResolver<Dossier> {

  private final DossierQueryRepository dossierQueryRepository;

  public DossierFieldResolver(DossierQueryRepository dossierQueryRepository) {
    this.dossierQueryRepository = dossierQueryRepository;
  }

  public Substance getSubstance(Dossier dossier, DataFetchingEnvironment env) {
    SubstanceDocumentFieldQuery query = new SubstanceDocumentFieldQuery();

    mapFieldsToQuery(env.getField().getSelectionSet(), query, SubstanceDocumentField::fromFieldName);

    Map<String, String> results = getResults(query, dossier);

    if (results != null) {
      return Substance.builder()
          .substanceName(results.get(SubstanceDocumentField.SUBSTANCE_NAME.getFieldName()))
          .ownerLegalEntityConfidentiality(results.get(SubstanceDocumentField.OWNER_LEGAL_ENTITY_CONFIDENTIALITY.getFieldName()))
          .referenceSubstanceConfidentiality(results.get(SubstanceDocumentField.REFERENCE_SUBSTANCE_CONFIDENTIALITY.getFieldName()))
          .build();
    } else {
      return null;
    }
  }

  public ReferenceSubstance getReferenceSubstance(Dossier dossier, DataFetchingEnvironment env) {
    ReferenceSubstanceDocumentFieldQuery query = new ReferenceSubstanceDocumentFieldQuery();

    mapFieldsToQuery(env.getField().getSelectionSet(), query, ReferenceSubstanceDocumentField::fromFieldName);

    Map<String, String> results = getResults(query, dossier);

    if (results != null) {
      return ReferenceSubstance.builder()
          .referenceSubstanceName(results.get(ReferenceSubstanceDocumentField.REFERENCE_SUBSTANCE_NAME.getFieldName()))
          .casNumber(results.get(ReferenceSubstanceDocumentField.CAS_NUMBER.getFieldName()))
          .ecNumber(results.get(ReferenceSubstanceDocumentField.EC_NUMBER.getFieldName()))
          .iupacName(results.get(ReferenceSubstanceDocumentField.IUPAC_NAME.getFieldName()))
          .molecularFormula(results.get(ReferenceSubstanceDocumentField.MOLECULAR_FORMULA.getFieldName()))
          .molecularStructureConfidentiality(results.get(ReferenceSubstanceDocumentField.MOLECULAR_STRUCTURE_CONFIDENTIALITY.getFieldName()))
          .referenceSubstanceInfoConfidentiality(results.get(ReferenceSubstanceDocumentField.REFERENCE_SUBSTANCE_INFO_CONFIDENTIALITY.getFieldName()))
          .build();
    } else {
      return null;
    }
  }

  public List<ReasonForUpdate> getReasonsForSpontaneousUpdate(Dossier dossier) {
    return getResults(new ReasonForSpontaneousUpdateQuery(), dossier);
  }

  public List<ReasonForUpdate> getReasonsForRequestedUpdate(Dossier dossier) {
    return getResults(new ReasonForRequestedUpdateQuery(), dossier);
  }

  /**
   * This will allow you to query a dossier either by storageLocation or DossierId
   * As there is no guarantee a dossier will have either it will always have at least one
   * The order of priority will be - StorageLocation then DossierId
   * @param query to be performed on the dossier
   * @param dossier object to be queried (requires dossierId, or StorageLocation)
   */
  private <T> T getResults(Query<T> query, Dossier dossier) {
    if (dossier.getStorageLocation() != null) {
      return dossierQueryRepository.queryTempDossier(query, dossier.getStorageLocation());
    } else {
      return dossierQueryRepository.queryDossier(query, dossier.getId());
    }
  }

  @SuppressWarnings("rawtypes")
  private <T extends DocumentField> void mapFieldsToQuery(SelectionSet selectionSet, AbstractFieldQuery<T> queryBuilder, Function<String, T> lookupFunction) {
    for (Selection selection : selectionSet.getSelections()) {
      if (selection instanceof Field) {
        Field field = (Field) selection;
        T xpath = lookupFunction.apply(field.getName());
        if (xpath != null) {
          queryBuilder.addField(xpath);
        }
      }
    }
  }
}
