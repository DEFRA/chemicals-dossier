package uk.gov.defra.reach.dossier.security;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.SneakyThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;


public class JwtAuthenticationFilterTest {

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String AUTHORIZATION_HEADER_VALUE = "Bearer token";
  private static final String EXCEPTION_LOG_MESSAGE = "Could not set user authentication in security context";

  private static TestLogger testLogger;
  private static SecurityContext securityContext;

  @BeforeAll
  public static void setUp() {
    Logger logger = (Logger) LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    testLogger = new TestLogger();
    testLogger.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
    logger.setLevel(Level.DEBUG);
    logger.addAppender(testLogger);
    testLogger.start();

    Authentication authentication = mock(Authentication.class);
    securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
  }

  @AfterEach
  public void afterEach() {
    testLogger.reset();
  }

  @Test
  @SneakyThrows
  void doFilterInternal_shouldNotSetAuthentication_WhenRequestHasNoJwT() {
    JwtTokenValidator jwtTokenValidator = mock(JwtTokenValidator.class);

    JwtAuthenticationFilter jwtAuthenticationFilter = spy(new JwtAuthenticationFilter(jwtTokenValidator));

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn("");

    HttpServletResponse response = mock(HttpServletResponse.class);

    FilterChain filterChain = mock(FilterChain.class);

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
    verify(filterChain, times(1)).doFilter(request, response);
    verify(securityContext, times(0)).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
  }

  @Test
  @SneakyThrows
  void doFilterInternal_shouldNotSetAuthentication_WhenJWTHasText_AndJWTIsNotValid() {
    JwtTokenValidator jwtTokenValidator = mock(JwtTokenValidator.class);
    when(jwtTokenValidator.validateToken(any(String.class))).thenReturn(false);

    JwtAuthenticationFilter jwtAuthenticationFilter = spy(new JwtAuthenticationFilter(jwtTokenValidator));

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(AUTHORIZATION_HEADER_VALUE);

    HttpServletResponse response = mock(HttpServletResponse.class);

    FilterChain filterChain = mock(FilterChain.class);

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
    verify(filterChain, times(1)).doFilter(request, response);
    verify(securityContext, times(0)).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
  }

  @Test
  @SneakyThrows
  void doFilterInternal_shouldSetAuthentication_WhenJWTHasText_AndJwtIsValid() {
    JwtTokenValidator jwtTokenValidator = mock(JwtTokenValidator.class);
    when(jwtTokenValidator.validateToken(any(String.class))).thenReturn(true);

    JwtAuthenticationFilter jwtAuthenticationFilter = spy(new JwtAuthenticationFilter(jwtTokenValidator));

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(AUTHORIZATION_HEADER_VALUE);

    HttpServletResponse response = mock(HttpServletResponse.class);

    FilterChain filterChain = mock(FilterChain.class);

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
    verify(filterChain, times(1)).doFilter(request, response);
    verify(securityContext, times(1)).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
  }

  @Test
  @SneakyThrows
  void doFilterInternal_ShouldLogError_WhenExceptionThrown() {
    JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(null);
    FilterChain filterChain = mock(FilterChain.class);
    jwtAuthenticationFilter.doFilterInternal(null, null, filterChain);
    assertThat(testLogger.search(EXCEPTION_LOG_MESSAGE).size()).isEqualTo(1);
  }
}
