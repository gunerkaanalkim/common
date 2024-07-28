package org.kaanalkim.common.interceptor;

import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.kaanalkim.common.exception.JWTVerificationException;
import org.kaanalkim.common.model.request.AuthorizationVerificationRequest;
import org.kaanalkim.common.model.response.AuthorizationVerificationResponse;
import org.kaanalkim.common.model.response.JWTVerificationResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public abstract class AuthenticationFilter extends OncePerRequestFilter {
    private final String authServerBaseURL;

    private final String tokenVerificationURL;

    private final String authorizationVerificationURL;

    private final long realmId;

    public AuthenticationFilter(String authServerBaseURL, String tokenVerificationURL, String authorizationVerificationURL, long realmId) {
        this.authServerBaseURL = authServerBaseURL;
        this.tokenVerificationURL = tokenVerificationURL;
        this.authorizationVerificationURL = authorizationVerificationURL;
        this.realmId = realmId;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        this.preFilter(request, response);

        Optional<String> whiteListedURL = this.getWhileListUrls()
                .stream()
                .filter(url -> request.getRequestURI().equals(url))
                .findFirst();

        if (whiteListedURL.isPresent()) {
            chain.doFilter(request, response);
        }

        if (Objects.nonNull(request.getHeader("Authorization"))) {
            final String token = request.getHeader("Authorization").substring(7);

            log.info("Requested URL is : {}", request.getRequestURI());

            try {

                JWTVerificationResponse jwtVerificationResponse = this.verifyJWTToken(token, request);
                this.hasPermission(jwtVerificationResponse, request, token);

            } catch (URISyntaxException | InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

        this.postFilter(request, response);

        chain.doFilter(request, response);
    }

    //Check JWT Token
    private JWTVerificationResponse verifyJWTToken(String token, HttpServletRequest request)
            throws IOException, InterruptedException, URISyntaxException {
        HttpRequest tokenValidationRequest = HttpRequest.newBuilder()
                .uri(new URI(authServerBaseURL + tokenVerificationURL))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> tokenValidationResponse = HttpClient
                .newHttpClient()
                .send(tokenValidationRequest, HttpResponse.BodyHandlers.ofString());

        if (tokenValidationResponse.statusCode() == HttpStatus.UNAUTHORIZED.value()) {
            log.info("Requested has been failed, {}", request.getRequestURI());
            throw new JWTVerificationException("401 Token not valid.");
        }

        return new Gson().fromJson(tokenValidationResponse.body(), JWTVerificationResponse.class);
    }

    //Check permission
    private void hasPermission(JWTVerificationResponse jwtVerificationResponse, HttpServletRequest request, String token)
            throws URISyntaxException, IOException, InterruptedException {
        String username = jwtVerificationResponse.getClaims().get("sub").toString();
        String permissionVerificationURL = String.format("%s%s", authServerBaseURL, authorizationVerificationURL);

        AuthorizationVerificationRequest requestBody = AuthorizationVerificationRequest.builder()
                .requestPath(request.getRequestURI())
                .username(username)
                .realmId(realmId)
                .build();

        HttpRequest authorizationValidationRequest = HttpRequest
                .newBuilder()
                .uri(new URI(permissionVerificationURL))
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token))
                .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(requestBody)))
                .build();

        HttpResponse<String> authorizationResponse = HttpClient
                .newHttpClient()
                .send(authorizationValidationRequest, HttpResponse.BodyHandlers.ofString());

        AuthorizationVerificationResponse authorizationVerificationResponse = new Gson()
                .fromJson(authorizationResponse.body(), AuthorizationVerificationResponse.class);

        if (!authorizationVerificationResponse.isHasPermission()) {
            String message = String.format("There is no permission to access this resource : %s", request.getRequestURI());

            log.info(message);
            throw new JWTVerificationException(message);
        }

    }

    public abstract List<String> getWhileListUrls();

    public abstract void preFilter(HttpServletRequest request, HttpServletResponse response);

    public abstract void postFilter(HttpServletRequest request, HttpServletResponse response);
}
