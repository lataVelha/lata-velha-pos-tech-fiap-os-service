package br.com.lata.velha.ordem_servico.domain.value_objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DocumentoTest {

    // ==================== CPF ====================

    @Nested
    @DisplayName("CPF")
    class CpfTests {

        @Test
        @DisplayName("deve criar CPF válido")
        void shouldCreateValidCpf() {
            Documento doc = Documento.of("52998224725");

            assertEquals("52998224725", doc.getValor());
            assertEquals(Documento.Tipo.CPF, doc.getTipo());
        }

        @Test
        @DisplayName("deve criar CPF válido com formatação")
        void shouldCreateCpfWithFormatting() {
            Documento doc = Documento.of("529.982.247-25");

            assertEquals("52998224725", doc.getValor());
            assertEquals(Documento.Tipo.CPF, doc.getTipo());
        }

        @Test
        @DisplayName("deve formatar CPF corretamente")
        void shouldFormatCpf() {
            Documento doc = Documento.of("52998224725");

            assertEquals("529.982.247-25", doc.getFormatted());
        }

        @Test
        @DisplayName("deve rejeitar CPF com dígitos iguais")
        void shouldRejectCpfWithEqualDigits() {
            assertThrows(IllegalArgumentException.class, () -> Documento.of("11111111111"));
        }

        @Test
        @DisplayName("deve rejeitar CPF com dígito verificador inválido")
        void shouldRejectCpfWithInvalidCheckDigit() {
            assertThrows(IllegalArgumentException.class, () -> Documento.of("52998224700"));
        }

        @Test
        @DisplayName("deve rejeitar CPF com menos de 11 dígitos")
        void shouldRejectShortCpf() {
            assertThrows(IllegalArgumentException.class, () -> Documento.of("1234567890"));
        }
    }

    // ==================== CNPJ ====================

    @Nested
    @DisplayName("CNPJ")
    class CnpjTests {

        @Test
        @DisplayName("deve criar CNPJ numérico válido (formato antigo)")
        void shouldCreateValidNumericCnpj() {
            Documento doc = Documento.of("11222333000181");

            assertEquals("11222333000181", doc.getValor());
            assertEquals(Documento.Tipo.CNPJ, doc.getTipo());
        }

        @Test
        @DisplayName("deve criar CNPJ numérico com formatação")
        void shouldCreateCnpjWithFormatting() {
            Documento doc = Documento.of("11.222.333/0001-81");

            assertEquals("11222333000181", doc.getValor());
        }

        @Test
        @DisplayName("deve formatar CNPJ corretamente")
        void shouldFormatCnpj() {
            Documento doc = Documento.of("11222333000181");

            assertEquals("11.222.333/0001-81", doc.getFormatted());
        }

        @Test
        @DisplayName("deve criar CNPJ alfanumérico (formato novo)")
        void shouldCreateAlphanumericCnpj() {
            Documento doc = Documento.of("AB1CD234EF5G67");

            assertEquals("AB1CD234EF5G67", doc.getValor());
            assertEquals(Documento.Tipo.CNPJ, doc.getTipo());
        }

        @Test
        @DisplayName("deve formatar CNPJ alfanumérico corretamente")
        void shouldFormatAlphanumericCnpj() {
            Documento doc = Documento.of("AB1CD234EF5G67");

            assertEquals("AB.1CD.234/EF5G-67", doc.getFormatted());
        }

        @Test
        @DisplayName("deve converter CNPJ alfanumérico para maiúsculas")
        void shouldConvertToUpperCase() {
            Documento doc = Documento.of("ab1cd234ef5g67");

            assertEquals("AB1CD234EF5G67", doc.getValor());
        }

        @Test
        @DisplayName("deve rejeitar CNPJ numérico com dígitos iguais")
        void shouldRejectCnpjWithEqualDigits() {
            assertThrows(IllegalArgumentException.class, () -> Documento.of("11111111111111"));
        }

        @Test
        @DisplayName("deve rejeitar CNPJ numérico com dígito verificador inválido")
        void shouldRejectCnpjWithInvalidCheckDigit() {
            assertThrows(IllegalArgumentException.class, () -> Documento.of("11222333000100"));
        }
    }

    // ==================== GERAL ====================

    @Nested
    @DisplayName("Validações gerais")
    class GeneralValidations {

        @Test
        @DisplayName("deve rejeitar documento nulo")
        void shouldRejectNull() {
            assertThrows(IllegalArgumentException.class, () -> Documento.of(null));
        }

        @Test
        @DisplayName("deve rejeitar documento vazio")
        void shouldRejectEmpty() {
            assertThrows(IllegalArgumentException.class, () -> Documento.of(""));
        }

        @Test
        @DisplayName("deve rejeitar documento com tamanho inválido")
        void shouldRejectInvalidLength() {
            assertThrows(IllegalArgumentException.class, () -> Documento.of("123456"));
        }

        @Test
        @DisplayName("documentos iguais devem ser equals")
        void shouldBeEqualWithSameValue() {
            Documento doc1 = Documento.of("52998224725");
            Documento doc2 = Documento.of("529.982.247-25");

            assertEquals(doc1, doc2);
            assertEquals(doc1.hashCode(), doc2.hashCode());
        }

        @Test
        @DisplayName("documentos diferentes não devem ser equals")
        void shouldNotBeEqualWithDifferentValue() {
            Documento cpf = Documento.of("52998224725");
            Documento cnpj = Documento.of("11222333000181");

            assertNotEquals(cpf, cnpj);
        }

        @Test
        @DisplayName("toString deve retornar formatado")
        void shouldReturnFormattedOnToString() {
            Documento doc = Documento.of("52998224725");

            assertEquals("529.982.247-25", doc.toString());
        }
    }
}