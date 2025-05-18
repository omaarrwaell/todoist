package Config; // Your package name

// Import reactive security classes
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity; // <-- Use this!
import org.springframework.security.config.web.server.ServerHttpSecurity; // <-- Use this!
import org.springframework.security.web.server.SecurityWebFilterChain; // <-- Use this!

// Other necessary imports (keep only what's used)
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

// Remove unused Servlet security imports and JWT customizer if not used
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder; // Remove if not needed in Gateway
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
// import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
// import org.springframework.security.config.Customizer; // Only needed if oauth2ResourceServer is configured

// >>> Use the reactive annotation <<<
@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

    @Bean // Returns the reactive filter chain
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        http
                // Configure authorization rules using ServerHttpSecurity
                .authorizeExchange(exchanges -> exchanges
                        // --- Permit All Paths (matching your application.properties routes) ---
                        // Explicitly permit the registration path requested by the user
                        // This matches the client request hitting the Gateway: http://localhost:8089/user/api/users
                        .pathMatchers("/user/api/users").permitAll()

                        // Permit the login path (assuming it's /user/login based on common patterns and your route /user/**)
                        .pathMatchers("/user/login").permitAll()

                        // Permit password reset paths (assuming /user/password-reset/** matches your route /user/**)
                        .pathMatchers("/user/password-reset/**").permitAll()

                        // Add other public paths for other services here as needed
                        // These pathMatchers should match the paths clients use to hit the Gateway,
                        // *not* the internal paths used by the downstream services (unless they are the same).
                        // Example: If the board service has a public endpoint at /board/public, permit it here:
                        // .pathMatchers("/board/public/**").permitAll()
                        // Example: If search is public:
                        // .pathMatchers("/search/**").permitAll()
                        // Example: If getting reminder status is public:
                        // .pathMatchers("/reminder/status/**").permitAll()

                        // Permit Actuator health and prometheus endpoints (important for monitoring)
                        // Assuming Actuator is exposed on the Gateway's port directly
                        .pathMatchers("/actuator/health", "/actuator/prometheus").permitAll()


                        // --- Authenticated Paths ---
                        // All other requests require authentication.
                        // Since we removed JWT config and are not configuring other auth mechanisms here,
                        // any path NOT explicitly permitAll() will result in 401 Unauthorized by default.
                        .anyExchange().authenticated()
                )

                // >>> REMOVE OAuth2 Resource Server configuration as you are NOT using JWTs <<<
                // .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())) // Remove this line

                // >>> DISABLE CSRF for stateless APIs <<<
                .csrf(ServerHttpSecurity.CsrfSpec::disable); // Disable CSRF using reactive API

        return http.build();
    }

    // >>> REMOVE PasswordEncoder bean from the API Gateway <<<
    // This bean belongs in the user-service where passwords are hashed and verified.
    /*
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    */

    // Keep the GlobalFilter if it has a purpose, but note potential review
    // This filter runs independently of the SecurityFilterChain but acts on requests
    // going through the Gateway.
    @Bean
    public GlobalFilter customGlobalFilter() {
        // Reconsider the logic of this filter. It currently doesn't seem to modify headers meaningfully
        // compared to Gateway's default forwarding.
        return (exchange, chain) -> {
            // log.info("Custom Global Filter executed"); // Example logging
            exchange.getRequest().mutate()
                    .headers(headers -> headers.addAll(exchange.getRequest().getHeaders())); // This line looks suspicious
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                // This runs AFTER the downstream service responds
                // log.info("Custom Global Filter finished for {}", exchange.getRequest().getURI());
            }));
        };
    }
}