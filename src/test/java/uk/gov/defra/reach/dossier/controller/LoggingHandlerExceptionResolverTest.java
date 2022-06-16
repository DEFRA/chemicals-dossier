package uk.gov.defra.reach.dossier.controller;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import uk.gov.defra.reach.dossier.security.TestLogger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class LoggingHandlerExceptionResolverTest {

  private final LoggingHandlerExceptionResolver loggingHandlerExceptionResolver = new LoggingHandlerExceptionResolver();
  private static TestLogger testLogger;

  @BeforeAll
  public static void setUp() {
    Logger logger = (Logger) LoggerFactory.getLogger(LoggingHandlerExceptionResolver.class);
    testLogger = new TestLogger();
    testLogger.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
    logger.setLevel(Level.DEBUG);
    logger.addAppender(testLogger);
    testLogger.start();
  }

  @Test
  void getOrder_ShouldReturnHighestHighestPrecedence_whenCalled() {
    assertThat(loggingHandlerExceptionResolver.getOrder()).isEqualTo(Ordered.HIGHEST_PRECEDENCE);
  }

  @Test
  void resolveException_ReturnsNull_AndLogsErrorWithExceptionMessage_WhenCalled() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    Object handler = mock(Object.class);

    String exceptionMessage = "An error occurred";

    Exception ex = new Exception(exceptionMessage);

    assertThat(loggingHandlerExceptionResolver.resolveException(request, response, handler, ex)).isNull();
    assertThat(testLogger.search(exceptionMessage).size()).isEqualTo(1);
  }
}
