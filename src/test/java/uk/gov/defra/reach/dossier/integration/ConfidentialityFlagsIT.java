package uk.gov.defra.reach.dossier.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.graphql.spring.boot.test.GraphQLResponse;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.util.StringUtils;

public class ConfidentialityFlagsIT extends AbstractIT {

  private static Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of("brucine-conf-LE-cbi-INQUIRY.i6z", Set.of("LEGAL_ENTITY")),
        Arguments.of("cyclizine-substance-conf-ip-INQUIRY.i6z", Set.of("REFERENCE_SUBSTANCE")),
        Arguments.of("enoxolene-ref-substance-conf-noPa-INQUIRY.i6z", Set.of("REFERENCE_SUBSTANCE_INFO")),
        Arguments.of("fonofos-substance-conf-cbi-INQUIRY.i6z", Set.of("REFERENCE_SUBSTANCE")),
        Arguments.of("no-ref-substance-conf-LE-cbi-INQUIRY.i6z", Set.of("LEGAL_ENTITY")),
        Arguments.of("no-ref-substance-confidential-INQ.i6z", Set.of("REFERENCE_SUBSTANCE")),
        Arguments.of("parsalmide-substance-conf-noPa-INQUIRY.i6z", Set.of("REFERENCE_SUBSTANCE")),
        Arguments.of("ref-substance-and-LE-conf-INQUIRY.i6z", Set.of("REFERENCE_SUBSTANCE", "LEGAL_ENTITY")),
        Arguments.of("ref-substance-conf-with-flag-INQUIRY.i6z", Set.of("REFERENCE_SUBSTANCE")),
        Arguments.of("ribitol-ref-substance-conf-cbi-INQUIRY.i6z", Set.of("REFERENCE_SUBSTANCE_INFO")),
        Arguments.of("sulfotep-ref-substance-conf-ip-INQUIRY.i6z", Set.of("REFERENCE_SUBSTANCE_INFO")),
        Arguments.of("temephos-conf-LE-cbi-INQUIRY.i6z", Set.of("LEGAL_ENTITY")),
        Arguments.of("us-tsca-flag-set-on-ref-substance-INQUIRY.i6z", Set.of()),
        Arguments.of("conf-LE-and-ref-substance-REG-joint.i6z", Set.of("REFERENCE_SUBSTANCE", "LEGAL_ENTITY")),
        Arguments.of("heptanoate_with_secrets-REG.i6z", Set.of("LEGAL_ENTITY")),
        Arguments.of("ref-substance-flag-eu-reach-test.i6z", Set.of()),
        Arguments.of("ribitol-ref-substance-conf-cbi-REG.i6z", Set.of("REFERENCE_SUBSTANCE_INFO")),
        Arguments.of("secret_heptanoate-REG.i6z", Set.of("REFERENCE_SUBSTANCE")),
        Arguments.of("us-tsca-flag-set-on-ref-substance-REG-joint.i6z", Set.of()),
        Arguments.of("us-tsca-flag-set-on-ref-substance-REG.i6z", Set.of()),
        Arguments.of("hydrogen_peroxide_confidentiality_INQ_6-5-0-0.i6z", Set.of("REFERENCE_SUBSTANCE_INFO")),
        Arguments.of("hydrogen_peroxide_confidentiality_REG_LEAD_6-5-0-0.i6z", Set.of("REFERENCE_SUBSTANCE_INFO"))
    );
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void confidentialityFlagsForDossier(String dossierFile, Set<String> expectedConfidentialityFlags) {
    String dossierId = indexDossier(dossierFile);
    Set<String> flags = getConfidentialityFlagsForDossier(dossierId);
    assertThat(flags).isEqualTo(expectedConfidentialityFlags);
  }

  private Set<String> getConfidentialityFlagsForDossier(String dossierId) {
    GraphQLResponse response = executeGraphQLQuery("query($id:ID!) { \n"
        + "    getDossier(id:$id) { \n"
        + "        dossierType\n"
        + "        substance {\n"
        + "          ownerLegalEntityConfidentiality\n"
        + "          referenceSubstanceConfidentiality\n"
        + "        } \n"
        + "        referenceSubstance {\n"
        + "          referenceSubstanceInfoConfidentiality\n"
        + "        }\n"
        + "    }\n"
        + "}", dossierId);

    String leConf = response.get("$.data.getDossier.substance.ownerLegalEntityConfidentiality");
    String refConf = response.get("$.data.getDossier.substance.referenceSubstanceConfidentiality");
    String refInfoConf = null;
    if (response.get("$.data.getDossier.referenceSubstance", Map.class) != null) {
      refInfoConf = response.get("$.data.getDossier.referenceSubstance.referenceSubstanceInfoConfidentiality");
    }

    Set<String> flags = new HashSet<>();
    if (StringUtils.hasText(leConf)) {
      flags.add("LEGAL_ENTITY");
    }
    if (StringUtils.hasText(refConf)) {
      flags.add("REFERENCE_SUBSTANCE");
    }
    if (StringUtils.hasText(refInfoConf)) {
      flags.add("REFERENCE_SUBSTANCE_INFO");
    }
    return flags;
  }

}
