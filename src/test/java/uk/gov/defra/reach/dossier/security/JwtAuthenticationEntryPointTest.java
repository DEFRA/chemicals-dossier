package uk.gov.defra.reach.dossier.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.junit.jupiter.api.Test;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

class JwtAuthenticationEntryPointTest {

  private static final String EXCEPTION_ERROR_MESSAGE = "Test Error Message";
  private static final String EXCEPTION_LOG_MESSAGE = "Responding with unauthorized error. Message - ";
  private static TestLogger testLogger;

  @BeforeAll
  public static void setUp() {
    Logger logger = (Logger) LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);
    testLogger = new TestLogger();
    testLogger.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
    logger.setLevel(Level.DEBUG);
    logger.addAppender(testLogger);
    testLogger.start();
  }

  @AfterEach
  public void afterEach() {
    testLogger.reset();
  }

  @Test
  @SneakyThrows
  void commence_callsSendErrorOnce_WithCorrectParameters_AndLogsError_whenCalled() {
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint = new JwtAuthenticationEntryPoint();
    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
    AuthenticationException authenticationException = mock(AuthenticationException.class);
    when(authenticationException.getMessage()).thenReturn(EXCEPTION_ERROR_MESSAGE);
    jwtAuthenticationEntryPoint.commence(httpServletRequest, httpServletResponse, authenticationException);
    verify(httpServletResponse, times(1)).sendError(HttpServletResponse.SC_UNAUTHORIZED, EXCEPTION_ERROR_MESSAGE);
    assertThat(testLogger.search(EXCEPTION_LOG_MESSAGE.concat(EXCEPTION_ERROR_MESSAGE)).size()).isEqualTo(1);
  }

  @Test
  void commence_ShouldThrowIOException_whenHttpServletResponse_SendError_ThrowsIOException() throws IOException {
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint = new JwtAuthenticationEntryPoint();
    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
    AuthenticationException authenticationException = mock(AuthenticationException.class);
    when(authenticationException.getMessage()).thenReturn(EXCEPTION_ERROR_MESSAGE);
    doThrow(new IOException()).when(httpServletResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED, EXCEPTION_ERROR_MESSAGE);
    assertThatExceptionOfType(IOException.class).isThrownBy(() -> jwtAuthenticationEntryPoint.commence(httpServletRequest, httpServletResponse, authenticationException));
  }
}
