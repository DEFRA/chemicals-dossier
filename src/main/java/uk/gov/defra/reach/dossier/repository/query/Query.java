package uk.gov.defra.reach.dossier.repository.query;

import org.springframework.jdbc.core.ResultSetExtractor;

/**
 * A query for extracting data from a single dossier
 *
 * @param <T> the type of data the query returns
 */
public interface Query<T> {

  /**
   * Returns a T-SQL query to extract data from a persisted dossier
   *
   * @return the query
   */
  String getSQLQuery();

  /**
   * Returns a T-SQL query to extract data from a temporary dossier
   *
   * @return the query
   */
  String getTempSQLQuery();

  /**
   * Returns a {@code ResultSetExtractor} which will extract the data from the result of the SQL query
   *
   * @return the result set extractor
   */
  ResultSetExtractor<T> getResultSetExtractor();

}
