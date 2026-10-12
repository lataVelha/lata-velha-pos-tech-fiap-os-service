package br.com.lata.velha.shared.infrastructure.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Slf4jLoggerTest {

    private static class ListAppender extends AppenderBase<ILoggingEvent> {
        final List<ILoggingEvent> events = new ArrayList<>();

        @Override
        protected void append(ILoggingEvent eventObject) {
            eventObject.getCallerData();
            events.add(eventObject);
        }
    }

    private final Slf4jLogger logger = new Slf4jLogger();
    private ListAppender appender;
    private Logger underlyingLogger;

    @BeforeEach
    void setUp() {
        underlyingLogger = (Logger) LoggerFactory.getLogger(Slf4jLogger.class);
        underlyingLogger.setLevel(Level.TRACE);
        appender = new ListAppender();
        appender.setContext(underlyingLogger.getLoggerContext());
        appender.start();
        underlyingLogger.addAppender(appender);
    }

    @AfterEach
    void tearDown() {
        underlyingLogger.detachAppender(appender);
    }

    private ILoggingEvent onlyEvent() {
        assertEquals(1, appender.events.size());
        return appender.events.getFirst();
    }

    @Test
    @DisplayName("deve logar em INFO com a mensagem correta")
    void shouldLogInfoWithMessage() {
        logger.logInfo("hello");

        ILoggingEvent event = onlyEvent();
        assertEquals(Level.INFO, event.getLevel());
        assertEquals("hello", event.getFormattedMessage());
    }

    @Test
    @DisplayName("deve substituir os placeholders com os argumentos informados")
    void shouldSubstituteArgsInFormattedMessage() {
        logger.logInfo("value={} other={}", "x", 42);

        assertEquals("value=x other=42", onlyEvent().getFormattedMessage());
    }

    @Test
    @DisplayName("deve logar em WARN")
    void shouldLogWarn() {
        logger.logWarn("careful");

        assertEquals(Level.WARN, onlyEvent().getLevel());
    }

    @Test
    @DisplayName("deve logar em DEBUG")
    void shouldLogDebug() {
        logger.logDebug("debugging");

        assertEquals(Level.DEBUG, onlyEvent().getLevel());
    }

    @Test
    @DisplayName("deve logar em TRACE")
    void shouldLogTrace() {
        logger.logTrace("tracing");

        assertEquals(Level.TRACE, onlyEvent().getLevel());
    }

    @Test
    @DisplayName("deve logar em ERROR com a exceção anexada")
    void shouldLogErrorWithException() {
        RuntimeException ex = new RuntimeException("boom");

        logger.logError("failed", ex);

        ILoggingEvent event = onlyEvent();
        assertEquals(Level.ERROR, event.getLevel());
        assertEquals("failed", event.getFormattedMessage());
        assertNotNull(event.getThrowableProxy());
        assertEquals("boom", event.getThrowableProxy().getMessage());
    }

    @Test
    @DisplayName("deve reportar a classe/metodo chamador real, e nao o Slf4jLogger")
    void shouldReportRealCallerNotSlf4jLoggerItself() {
        logFromHere();

        StackTraceElement[] callerData = onlyEvent().getCallerData();
        assertNotNull(callerData);
        assertTrue(callerData.length > 0);
        assertEquals(Slf4jLoggerTest.class.getName(), callerData[0].getClassName());
        assertEquals("logFromHere", callerData[0].getMethodName());
        assertNotEquals(Slf4jLogger.class.getName(), callerData[0].getClassName());
    }

    private void logFromHere() {
        logger.logInfo("who called me?");
    }
}
