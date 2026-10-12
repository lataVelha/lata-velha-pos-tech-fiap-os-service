package br.com.lata.velha.ordem_servico.infrastructure.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import br.com.lata.velha.shared.application.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    private final Logger logger;

    @Bean
    public OpenAPI openAPI() {
        logger.logInfo("Configurando OpenAPI");
        return new OpenAPI()
                .info(new Info()
                        .title("Lata Velha — API de Oficina Mecânica")
                        .description("""
                                API de gerenciamento de Ordens de Serviço para oficina mecânica.

                                **Perfis:** ATENDENTE (abre OS, aprova serviços, entrega veículo) · MECANICO (diagnóstico e execução) · ADMIN (acesso total ao sistema)

                                ---

                                **Fluxo principal da OS:**

                                1. `POST /ordens-servico` — ATENDENTE abre a OS → status `RECEBIDA`
                                2. `PATCH /ordens-servico/{idOs}/iniciar-diagnostico` — MECANICO assume a OS → `EM_DIAGNOSTICO`
                                3. `PATCH /ordens-servico/adiciona-servico` — MECANICO adiciona serviços e peças (pode chamar múltiplas vezes)
                                4. `PATCH /ordens-servico/{idOs}/finalizar-diagnostico` — MECANICO encerra diagnóstico → `AGUARDANDO_APROVACAO` _(e-mail de aprovação da ordem de serviço ao proprietário)_
                                5. `PATCH /ordens-servico/aprovar` — ATENDENTE aprova ou recusa cada serviço → `APROVADA` _(e-mail de confirmação ao proprietário; e-mail ao ADMIN se houver peças a encomendar)_
                                6. `PATCH /ordens-servico/{idOs}/iniciar-servico/{servicoId}` — MECANICO inicia um serviço por vez → serviço `EM_EXECUCAO`
                                7. `PATCH /ordens-servico/{idOs}/finalizar-servico/{servicoId}` — MECANICO conclui o serviço → serviço `FINALIZADO` _(ao finalizar o último: OS → `FINALIZADA` e e-mail ao proprietário)_
                                8. `PATCH /ordens-servico/{idOs}/retirar-veiculo` — ATENDENTE registra entrega → `ENTREGUE`

                                ---

                                **Fluxo de recusa:**

                                - `PATCH /ordens-servico/aprovar` com serviços marcados como `RECUSADO` — recusa parcial (mantém ao menos um `APROVADO`)
                                - `PATCH /ordens-servico/{idOs}/reprovar` — ATENDENTE recusa a OS inteira → `REPROVADA` (terminal, nenhum serviço é executado)

                                ---

                                **Notificações automáticas por e-mail:**
                                - Proprietário, ao ser cadastrado pelo atendente (proprietário cadastrado)
                                - Proprietário, ao atendente criar a ordem de serviço (ordem de serviço aberta)
                                - Proprietário, ao mecânico iniciar o diagnóstico (diagnóstico iniciado)
                                - Proprietário, ao mecânico finalizar o diagnóstico (diagnóstico finalizado com lista de serviços identificados)
                                - Proprietário, ao atendente aprovar ordem de serviço (ordem de serviço aprovada)
                                - Proprietário, ao atendente reprovar ordem de serviço (ordem de serviço reprovada, retirar veículo)
                                - Administrador, ao aprovar a execução do serviço com peça sem estoque suficiente (encomenda de peça)
                                - Proprietário, ao mecânico iniciar a primeira execução do serviço (serviços em execução)
                                - Proprietário, ao mecânico finalizar todos os serviços (veículo pronto para retirada)
                                - Proprietário, ao retirar veículo da oficia (veículo entregue)

                                ---

                                **Autenticação:** todos os endpoints exigem `Bearer <token>` — obtenha via `POST /auth/login`.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe Lata Velha")
                        )
                )
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Token JWT obtido via POST /auth/login")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
