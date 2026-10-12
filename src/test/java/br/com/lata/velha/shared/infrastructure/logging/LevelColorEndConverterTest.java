package br.com.lata.velha.shared.infrastructure.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class LevelColorEndConverterTest {

    private static final String ESC = "\u001B";

    @Mock
    private ILoggingEvent event;

    private final LevelColorEndConverter converter = new LevelColorEndConverter();

    @Test
    @DisplayName("deve sempre retornar o código de reset, independente do nível")
    void shouldAlwaysReturnReset() {
        lenient().when(event.getLevel()).thenReturn(Level.ERROR);

        assertEquals(ESC + "[0;39m", converter.convert(event));
    }
}
