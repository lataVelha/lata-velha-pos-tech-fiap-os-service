# language: pt
Funcionalidade: Ciclo de vida completo da ordem de serviço

  Acompanha uma ordem de serviço do recebimento do veículo até a entrega ao
  cliente, passando pelo diagnóstico, pela aprovação do orçamento, pela execução
  dos serviços e pelo pagamento. Os eventos que o BillingService e o
  InventoryService publicam são entregues à OS como o barramento os entregaria.

  Contexto:
    Dado um proprietário com um veículo cadastrado
    E um serviço de "Troca de óleo" disponível no catálogo
    E um mecânico responsável pelo atendimento

  Cenário: cliente retira o veículo após aprovar o orçamento, a execução e o pagamento
    Quando o atendente registra a entrada do veículo na oficina
    Então a ordem de serviço fica "RECEBIDA"

    Quando o mecânico inicia o diagnóstico
    Então a ordem de serviço fica "EM_DIAGNOSTICO"

    Quando o mecânico adiciona o serviço com 2 peças e mão de obra de 150.00
    E o mecânico finaliza o diagnóstico
    Então a ordem de serviço fica "AGUARDANDO_APROVACAO"
    E o evento "DiagnosticoFinalizado" é publicado para o BillingService

    Quando o BillingService informa que o cliente aprovou o orçamento
    Então a ordem de serviço fica "APROVADA"
    E a execução do serviço fica "APROVADO"

    Quando o InventoryService confirma que há peças disponíveis
    Então a ordem de serviço fica "EM_EXECUCAO"

    Quando o mecânico inicia a execução do serviço
    E o mecânico finaliza a execução do serviço
    Então a ordem de serviço fica "FINALIZADA"
    E o evento "ServiceEnded" é publicado para o InventoryService
    E o evento "OSEnded" é publicado para o BillingService

    Quando o BillingService informa que a cobrança foi emitida
    Então a ordem de serviço continua "FINALIZADA"
    E o veículo não pode ser retirado

    Quando o BillingService informa que o pagamento foi concluído
    Então a ordem de serviço fica "PAGA"

    Quando o cliente retira o veículo
    Então a ordem de serviço fica "ENTREGUE"
    E o histórico de estados registra a sequência completa do ciclo de vida

  Cenário: orçamento reprovado pelo cliente encerra a ordem sem cobrança
    Quando o atendente registra a entrada do veículo na oficina
    E o mecânico inicia o diagnóstico
    E o mecânico adiciona o serviço com 2 peças e mão de obra de 150.00
    E o mecânico finaliza o diagnóstico
    E o BillingService informa que o cliente reprovou o orçamento
    Então a ordem de serviço fica "REPROVADA"

    Quando o cliente retira o veículo
    Então a ordem de serviço fica "ENTREGUE"
