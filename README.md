# Sail Backend

API de gastos com integração de IA para extração de dados financeiros.

## Serviço de IA

- **OpenRouter**: chamado no Rabbit consumer via `OpenRouterExpenseService`, tentando modelos gratuitos em sequência.

## Registrar uma chave de API no OpenRouter

1. Crie uma conta gratuita em https://openrouter.ai (basta um e-mail, sem cartão de crédito).
2. Acesse a página de Keys: https://openrouter.ai/settings/keys
3. Clique em **Create Key**.
4. Dê um nome à chave (ex.: `sail-backend`) e clique em **Create**.
5. Copie a chave gerada (formato `sk-or-v1-...`). Ela aparece apenas uma vez.
6. Configure a chave no backend de uma das formas abaixo:
   - **Via variável de ambiente:**
     ```bash
     export OPENROUTER_API_KEY=sk-or-v1-xxxxx
     ```
   - **Ou direto no `application.yaml`:**
     ```yaml
     openrouter:
       api-key: sk-or-v1-xxxxx
     ```
7. Reinicie a aplicação para aplicar a configuração.

> A chave fica no cabeçalho `Authorization: Bearer <chave>` de cada chamada. **Nunca** commite a chave no repositório.

## Modelos gratuitos (free)

A lista de modelos é definida em `application.yaml` em `openrouter.models` (separados por vírgula):

```yaml
openrouter:
  models: openrouter/free,openai/gpt-oss-20b:free,google/gemma-4-31b-it:free,nvidia/nemotron-3-nano-30b-a3b:free,z-ai/glm-5.2:free
```

- `openrouter/free` é o auto-router do OpenRouter que escolhe automaticamente um modelo gratuito disponível.
- Os IDs com sufixo `:free` são alternativas manuais. O serviço tenta cada modelo em ordem e, se um falhar ou retornar vazio, pula para o próximo.
- A lista de modelos gratuitos muda com frequência. Consulte os atuais em: https://openrouter.ai/collections/free-models
- Para listar os gratuitos via API (sem chave): `curl -s https://openrouter.ai/api/v1/models` e filtre modelos com `pricing.prompt`/`pricing.completion` igual a `0`.

## Limites do free tier

- Sem cartão de crédito, sem custo por token.
- Limite de ~20 requisições por minuto e 50/dia (1.000/dia após compra única de US$ 10 em créditos).
- Não garante disponibilidade — ideal para testes/prototipagem.

## Testando o fluxo completo

1. Suba dependências (RabbitMQ): `docker compose -f src/main/resources/docker-compose.yaml up -d`
2. Rode a aplicação: `./mvnw spring-boot:run`
3. Envie uma mensagem no chat (o consumer Rabbit processa e chama o OpenRouter).