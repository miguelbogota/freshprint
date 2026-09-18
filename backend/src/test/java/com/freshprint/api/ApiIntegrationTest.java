package com.freshprint.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

/** Exercises the real HTTP contract against isolated in-memory storage. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "spring.datasource.url=jdbc:h2:mem:api-test;DB_CLOSE_DELAY=-1")
class ApiIntegrationTest {

  @LocalServerPort
  int port;

  @Autowired
  ObjectMapper mapper;

  private final HttpClient http = HttpClient.newHttpClient();

  @Test
  void listsAccumulatedUpdatesAndCompletesAnApply() throws Exception {
    var list = request("GET", "/api/engagements/template-updates", null);
    assertEquals(200, list.statusCode());
    var items = mapper.readTree(list.body()).path("items");
    assertEquals(12, items.size());
    var bluewater = request("GET", "/api/engagements/ENG-1007/template-update", null);
    assertEquals(200, bluewater.statusCode(), bluewater.body());
    var item = mapper.readTree(bluewater.body());
    assertEquals("PENDING", item.path("status").asText());
    assertEquals(2, item.path("pendingVersionCount").asInt());
    assertEquals("AVAILABLE", item.path("summary").path("state").asText());

    var stale = request("POST", "/api/engagements/ENG-1007/template-update-decisions",
        "{\"decision\":\"APPLY\",\"expectedBaselineVersion\":6,\"targetVersion\":7}");
    assertEquals(409, stale.statusCode());
    assertEquals(8, mapper.readTree(stale.body()).path("currentTargetVersion").asInt());

    var accepted = request("POST", "/api/engagements/ENG-1007/template-update-decisions",
        "{\"decision\":\"APPLY\",\"expectedBaselineVersion\":6,\"targetVersion\":8}");
    assertEquals(200, accepted.statusCode());
    var operationId = mapper.readTree(accepted.body()).path("operationId").asText();
    assertTrue(operationId.startsWith("OP-"));

    String outcome = "";
    for (int i = 0; i < 30; i++) {
      var operation = request("GET", "/api/template-update-operations/" + operationId, null);
      outcome = mapper.readTree(operation.body()).path("status").asText();
      if ("SUCCEEDED".equals(outcome) || "FAILED".equals(outcome)) break;
      Thread.sleep(50);
    }
    assertEquals("SUCCEEDED", outcome);
    var updated = request("GET", "/api/engagements/ENG-1007/template-update", null);
    assertEquals("CURRENT", mapper.readTree(updated.body()).path("status").asText());
  }

  @Test
  void decliningKeepsTheBaselineAndPreventsRepeatingTheSameOffer() throws Exception {
    var accepted = request("POST", "/api/engagements/ENG-1002/template-update-decisions",
        "{\"decision\":\"DECLINE\",\"expectedBaselineVersion\":4,\"targetVersion\":5}");
    assertEquals(200, accepted.statusCode());
    var operationId = mapper.readTree(accepted.body()).path("operationId").asText();
    String outcome = "";
    for (int i = 0; i < 30; i++) {
      outcome = mapper.readTree(request("GET", "/api/template-update-operations/" + operationId, null)
          .body()).path("status").asText();
      if ("SUCCEEDED".equals(outcome) || "FAILED".equals(outcome)) break;
      Thread.sleep(50);
    }
    assertEquals("SUCCEEDED", outcome);
    var detail = request("GET", "/api/engagements/ENG-1002/template-update", null);
    assertEquals(200, detail.statusCode(), detail.body());
    var item = mapper.readTree(detail.body());
    assertEquals(4, item.path("baselineVersion").asInt());
    assertTrue(item.path("declined").asBoolean());
    assertEquals(409, request("POST", "/api/engagements/ENG-1002/template-update-decisions",
        "{\"decision\":\"DECLINE\",\"expectedBaselineVersion\":4,\"targetVersion\":5}").statusCode());
  }

  private HttpResponse<String> request(String method, String path, String body) throws Exception {
    var builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
        .timeout(Duration.ofSeconds(5));
    if (body == null) builder.GET();
    else builder.header("Content-Type", "application/json")
        .method(method, HttpRequest.BodyPublishers.ofString(body));
    return http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
  }
}
