package edu.bu.finhub;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import edu.bu.finhub.persistence.SQL_BasedPersistence;
import edu.bu.finhub.persistence.SymbolsPersistence;
import edu.bu.server.handlers.EnqueueingFinnhubResponseHandler;
import java.net.URISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.tinylog.Logger;

@Tag("IntegrationTest")
public class PersistenceIntegrationTest {

  EnqueueingFinnhubResponseHandler mockEnqueueingFinnhubResponseHandler;

  @BeforeEach
  public void setUp() {
    mockEnqueueingFinnhubResponseHandler = mock(EnqueueingFinnhubResponseHandler.class);
  }

  @Test
  public void createRestartConfirm() throws URISyntaxException, InterruptedException {
    SymbolsPersistence symbolsPersistence = new SQL_BasedPersistence();

    FinHubWebSocketClient firstFinHubWebSocketClient =
        new FinHubWebSocketClient(
            FinhubService.WEBHOOK_URI + "?token=" + FinhubService.API_TOKEN,
            mockEnqueueingFinnhubResponseHandler,
            symbolsPersistence);

    firstFinHubWebSocketClient.init();

    Logger.info("Removing ORCL to ensure clean slate");
    firstFinHubWebSocketClient.unsubscribe("ORCL");

    assertFalse(firstFinHubWebSocketClient.getSubscribedSymbols().contains("ORCL"));

    firstFinHubWebSocketClient.unsubscribe("ORCL");
    firstFinHubWebSocketClient.closeBlocking();

    FinHubWebSocketClient secondFinHubWebSocketClient =
        new FinHubWebSocketClient(
            FinhubService.WEBHOOK_URI + "?token=" + FinhubService.API_TOKEN,
            mockEnqueueingFinnhubResponseHandler,
            symbolsPersistence);

    secondFinHubWebSocketClient.init();

    assertTrue(secondFinHubWebSocketClient.getSubscribedSymbols().contains("ORCL"));

    secondFinHubWebSocketClient.close();
  }
}
