package io.gitlab.vitalijr2.ukrlatynka;

import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.text.CharSequenceLength.hasLength;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.gitlab.vitalijr2.telegram_bot.Update;
import io.gitlab.vitalijr2.telegram_bot.UpdateFactory;

@ExtendWith(MockitoExtension.class)
@Tag("fast")
class BotHandlerFastTest {

  private static Logger logger;

  @Captor
  private ArgumentCaptor<String> bodyCaptor;
  @Mock
  private Context context;
  @InjectMocks
  private BotHandler handler;
  @Mock
  private APIGatewayProxyRequestEvent requestEvent;
  @Mock
  private Update update;
  @Mock
  private UpdateFactory updateFactory;

  @BeforeAll
  static void setUpClass() {
    logger = LoggerFactory.getLogger(BotHandler.class);
  }

  @AfterEach
  void tearDown() {
    clearInvocations(logger);
  }

  @BeforeEach
  void setUp() {
    when(context.getAwsRequestId()).thenReturn("test-id");
  }

  @DisplayName("A request event with an empty body")
  @ParameterizedTest(name = "[{index}] body <{0}>")
  @NullAndEmptySource
  @ValueSource(strings = " ")
  void emptyRequestEventBody(String body) {
    // given
    when(requestEvent.getHeaders()).thenReturn(singletonMap("X-Forwarded-For", "1.2.3.4.5"));
    when(requestEvent.getBody()).thenReturn(body);

    // when
    var responseEvent = handler.handleRequest(requestEvent, context);

    // then
    verify(context).getAwsRequestId();
    verify(logger).warn("Empty request from {}", "1.2.3.4.5");

    assertAll("Response", () -> assertEquals("OK", responseEvent.getBody()),
        () -> assertEquals("text/plain", responseEvent.getHeaders().get("Content-Type")),
        () -> assertEquals(200, responseEvent.getStatusCode()));
  }

  @DisplayName("An update factory returns null: it cannot parse a request body")
  @Test
  void updateFactoryReturnsNull() throws Exception {
    // given
    when(requestEvent.getBody()).thenReturn("test body");

    // when
    var responseEvent = handler.handleRequest(requestEvent, context);

    // then
    verify(context).getAwsRequestId();
    verify(updateFactory).parseUpdate("test body");
    verify(update, never()).call();
    verifyNoInteractions(logger);

    assertAll("Response", () -> assertEquals("OK", responseEvent.getBody()),
        () -> assertEquals("text/plain", responseEvent.getHeaders().get("Content-Type")),
        () -> assertEquals(200, responseEvent.getStatusCode()));
  }

  @DisplayName("An update returns null: it cannot process a request")
  @Test
  void updateReturnsNull() throws Exception {
    // given
    when(requestEvent.getBody()).thenReturn("test body");
    when(updateFactory.parseUpdate(anyString())).thenReturn(update);

    // when
    var responseEvent = handler.handleRequest(requestEvent, context);

    // then
    verify(context).getAwsRequestId();
    verify(updateFactory).parseUpdate("test body");
    verify(update).call();
    verifyNoInteractions(logger);

    assertAll("Response", () -> assertEquals("OK", responseEvent.getBody()),
        () -> assertEquals("text/plain", responseEvent.getHeaders().get("Content-Type")),
        () -> assertEquals(200, responseEvent.getStatusCode()));
  }

  @DisplayName("An update throws an exception")
  @ParameterizedTest
  @CsvFileSource(resources = "/update-throws-an-exception.csv", numLinesToSkip = 1)
  void updateThrowsException(int length, String endsWith, String body) throws Exception {
    // given
    when(requestEvent.getHeaders()).thenReturn(singletonMap("X-Forwarded-For", "1.2.3.4.5"));
    when(requestEvent.getBody()).thenReturn(body);
    when(updateFactory.parseUpdate(anyString())).thenReturn(update);
    when(update.call()).thenThrow(new Exception("test exception"));

    // when
    var responseEvent = handler.handleRequest(requestEvent, context);

    // then
    verify(context).getAwsRequestId();
    verify(updateFactory).parseUpdate(body);
    verify(update).call();
    verify(logger).warn(eq("Update call from {}: {}\n{}"), eq("1.2.3.4.5"), eq("test exception"),
        bodyCaptor.capture());

    assertAll("Response", () -> assertEquals("OK", responseEvent.getBody()),
        () -> assertEquals("text/plain", responseEvent.getHeaders().get("Content-Type")),
        () -> assertEquals(200, responseEvent.getStatusCode()),
        () -> assertThat(bodyCaptor.getValue(), hasLength(length)),
        () -> assertThat(bodyCaptor.getValue(), endsWith(endsWith)));
  }

  @DisplayName("Happy path")
  @Test
  void happyPath() throws Exception {
    // given
    when(requestEvent.getBody()).thenReturn("test body");
    when(updateFactory.parseUpdate(anyString())).thenReturn(update);
    when(update.call()).thenReturn("{\"test\":\"pass\"}");

    // when
    var responseEvent = handler.handleRequest(requestEvent, context);

    // then
    verify(context).getAwsRequestId();
    verifyNoInteractions(logger);

    assertAll("Response", () -> assertEquals("{\"test\":\"pass\"}", responseEvent.getBody()),
        () -> assertEquals("application/json", responseEvent.getHeaders().get("Content-Type")),
        () -> assertEquals(200, responseEvent.getStatusCode()));
  }

}
