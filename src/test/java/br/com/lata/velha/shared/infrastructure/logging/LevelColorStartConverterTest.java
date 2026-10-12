package br.com.lata.velha.shared.infrastructure.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LevelColorStartConverterTest {

    private static final String ESC = "\u001B";

    @Mock
    private ILoggingEvent event;

    private final LevelColorStartConverter converter = new LevelColorStartConverter();

    @Test
    @DisplayName("deve retornar string vazia para INFO (sem cor)")
    void shouldReturnEmptyForInfo() {
        when(event.getLevel()).thenReturn(Level.INFO);

        assertEquals("", converter.convert(event));
    }

    @Test
    @DisplayName("deve retornar código de cor vermelha para ERROR")
    void shouldReturnRedForError() {
        when(event.getLevel()).thenReturn(Level.ERROR);

        assertEquals(ESC + "[31m", converter.convert(event));
    }

    @Test
    @DisplayName("deve retornar código de cor amarela para WARN")
    void shouldReturnYellowForWarn() {
        when(event.getLevel()).thenReturn(Level.WARN);

        assertEquals(ESC + "[33m", converter.convert(event));
    }

    @Test
    @DisplayName("deve retornar código de cor roxa (256-color) para DEBUG")
    void shouldReturnPurpleForDebug() {
        when(event.getLevel()).thenReturn(Level.DEBUG);

        assertEquals(ESC + "[38;5;135m", converter.convert(event));
    }

    @Test
    @DisplayName("deve retornar código de cor laranja (256-color) para TRACE")
    void shouldReturnOrangeForTrace() {
        when(event.getLevel()).thenReturn(Level.TRACE);

        assertEquals(ESC + "[38;5;208m", converter.convert(event));
    }
}
