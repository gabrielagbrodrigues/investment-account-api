# Investment Account API

API REST desenvolvida em Java com Spring Boot para gerenciamento de uma conta corrente virtual de investimentos, com controle de movimentações financeiras, bloqueio preventivo de valores e liquidação automática de operações.

O projeto foi desenvolvido como parte de um desafio técnico do Santander, com foco na segurança e na transparência das informações financeiras, especialmente na disponibilidade dos valores durante o período de liquidação dos investimentos.

## Problema de negócio

Ao realizar um investimento, o cliente pode ter o valor debitado da conta somente após um prazo específico, como D+3, D+10 ou D+30.

Durante esse intervalo, o valor precisa continuar compondo o patrimônio total do cliente, mas não pode permanecer disponível para novas movimentações, evitando que seja utilizado indevidamente.

A solução proposta permite:

- Visualizar o patrimônio total da conta.
- Consultar o saldo disponível para movimentações.
- Identificar valores bloqueados aguardando liquidação.
- Registrar operações de depósito, compra e venda.
- Acompanhar o status das movimentações.
- Consultar o extrato completo ou por período.
- Realizar a liquidação automática das operações após o prazo definido.

## Tecnologias utilizadas

- **Java 17** — linguagem de programação.
- **Spring Boot 4.1.2** — construção da aplicação e disponibilização dos serviços REST.
- **Spring Data JPA / Hibernate** — persistência e mapeamento objeto-relacional.
- **MySQL** — banco de dados relacional.
- **Maven** — gerenciamento de dependências e build.
- **Swagger / OpenAPI** — documentação e exploração dos endpoints.
- **Insomnia** — testes manuais das requisições HTTP.
- **IntelliJ IDEA** — ambiente de desenvolvimento.

## Arquitetura e organização

A aplicação utiliza uma arquitetura em camadas, organizada por módulos de negócio, facilitando a manutenção e a evolução dos componentes.

```text
src/main/java/investment_account_api
│
├── account
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── enums
│   ├── repository
│   └── service
│
├── config
│
├── exception
│
├── statement
│   ├── controller
│   ├── dto
│   └── service
│
└── transaction
    ├── controller
    ├── dto
    ├── entity
    ├── enums
    ├── repository
    ├── scheduler
    └── service
```

### Responsabilidades das camadas

- **Controller:** recebe as requisições HTTP e disponibiliza os endpoints.
- **Service:** concentra as regras de negócio e o processamento das operações.
- **Repository:** realiza o acesso e a persistência dos dados.
- **Entity:** representa as entidades persistidas no banco de dados.
- **DTO:** organiza os dados de entrada e saída da API.
- **Config:** concentra configurações da aplicação.
- **Exception:** centraliza o tratamento de exceções.

## Principais funcionalidades

### Gerenciamento de contas

- Abertura de conta virtual de investimentos.
- Ativação da conta.
- Consulta dos dados da conta.
- Consulta do saldo e patrimônio.

### Movimentações financeiras

- Depósitos.
- Compra de investimentos.
- Venda de investimentos.
- Controle de movimentações pendentes e liquidadas.
- Identificação de operações por chave de idempotência.

### Bloqueio preventivo

Quando uma operação de investimento aguarda liquidação, o valor correspondente permanece compondo o patrimônio total, mas é identificado como bloqueado.

Dessa forma, o saldo disponível considera as restrições das operações pendentes, evitando a utilização indevida dos recursos.

### Liquidação automática

A aplicação possui um agendador (`TransactionSettlementScheduler`) responsável por verificar periodicamente as operações pendentes e processar aquelas cujo prazo de liquidação foi atingido.

Para permitir a demonstração do comportamento temporal no protótipo, foi implementada uma configuração de relógio simulado (`ClockConfig`), na qual:

- 1 dia simulado corresponde a 2 minutos reais.
- Prazos como D+10 e D+30 podem ser demonstrados em um intervalo reduzido.

Essa aceleração é utilizada exclusivamente para facilitar os testes e a demonstração da solução.

### Extrato de movimentações

O módulo de extrato permite consultar:

- Histórico completo de operações.
- Tipo e valor da movimentação.
- Data da operação.
- Situação da operação.
- Consulta por intervalo de datas.

## Documentação da API

A documentação interativa foi implementada com Swagger / OpenAPI.

Com a aplicação em execução, acesse:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

Pela interface, é possível visualizar os endpoints, consultar os parâmetros e executar requisições para explorar as funcionalidades da API.

## Como executar o projeto

### Pré-requisitos

- Java 17 ou superior.
- MySQL instalado e em execução.
- Git.
- IDE de sua preferência (IntelliJ IDEA recomendado).

### 1. Clonar o repositório

```bash
git clone https://github.com/gabrielagbrodrigues/investment-account-api.git
```

### 2. Acessar a pasta do projeto

```bash
cd investment-account-api
```

### 3. Configurar o banco de dados

Crie um banco de dados MySQL para a aplicação.

Configure as seguintes variáveis de ambiente:

- `INVESTMENT_ACCOUNT_DB_URL`
- `INVESTMENT_ACCOUNT_DB_USERNAME`
- `INVESTMENT_ACCOUNT_DB_PASSWORD`

Exemplo de formato para a URL de conexão:

```text
jdbc:mysql://localhost:3306/investment_account_db
```

Configure os valores de acordo com seu ambiente local. Não é necessário inserir credenciais diretamente no código-fonte.

### 4. Executar a aplicação

No Windows, utilizando o Maven Wrapper:

```bash
mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
./mvnw spring-boot:run
```

Após a inicialização, a API estará disponível em:

```text
http://localhost:8080
```

## Melhorias futuras

Como possibilidades de evolução da solução, estão previstas:

- Implementação de testes automatizados com JUnit e Mockito.
- Integração com mensageria utilizando RabbitMQ para processamento assíncrono de eventos.
- Evolução do processamento de liquidação com integração a um Backoffice.
- Aprimoramento da observabilidade e do monitoramento da aplicação.
- Evolução dos mecanismos de segurança e autenticação.

## Considerações finais

O projeto demonstra uma solução modular para gerenciamento de uma conta virtual de investimentos, considerando regras de disponibilidade de saldo, bloqueio preventivo de valores e acompanhamento das movimentações financeiras.

A arquitetura foi estruturada para facilitar a manutenção e permitir futuras evoluções, mantendo como prioridade a consistência das informações apresentadas ao cliente.

---

**Desenvolvido por Gabriela Rodrigues da Silva**

[GitHub](https://github.com/gabrielagbrodrigues)
