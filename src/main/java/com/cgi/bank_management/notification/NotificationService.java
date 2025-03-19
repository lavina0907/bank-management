package com.cgi.bank_management.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private final HttpClient httpClient;
  private final String baseUrl;
  private final String notificationPath;

  public NotificationService(@Value("${notification.baseUrl}") String baseUrl,
      @Value("${notification.path}") String notificationPath) {
    this.httpClient = HttpClient.newHttpClient();
    this.baseUrl = baseUrl;
    this.notificationPath = notificationPath;
  }

  public CompletableFuture<HttpResponse<String>> sendNotification(String userId, NotificationType type, Map<String, Object> details) {
    try {
      String requestBody = createRequestBody(userId, type, details);
      String fullUrl = baseUrl.endsWith("/") ? baseUrl + notificationPath : baseUrl + "/" + notificationPath;
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(fullUrl))
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(requestBody))
          .build();
      return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    } catch (Exception e) {
      return CompletableFuture.failedFuture(e);
    }
  }

  private String createRequestBody(String userEmail, NotificationType type, Map<String, Object> details) throws Exception {
    Map<String, Object> requestBody = Map.of(
        "userId", userEmail,
        "type", type.name(),
        "details", details
    );
    return OBJECT_MAPPER.writeValueAsString(requestBody);
  }

  public enum NotificationType {
    USER_CREATED, BALANCE_DEBIT, BALANCE_CREDIT, ACCOUNT_CREATED
  }
}
