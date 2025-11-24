package be.ugent.reeks123;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import javax.sql.DataSource;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Value("${security.admin.name}")
    String adminUsername;
    @Value("${security.admin.password}")
    String adminPassword;
    @Value("${security.admin.roles}")
    String adminRoles;

    @Autowired
     Environment environment;



    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder, DataSource dataSource) {
        LoggerFactory.getLogger(SecurityConfig.class).info("Encoded password: " + passwordEncoder.encode(adminPassword));
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        if (!manager.userExists(adminUsername)) {
            manager.createUser(User.withUsername(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))
                    .roles(adminRoles)
                    .build());
        }
        return manager;
    }

    @Bean
    public PasswordEncoder encoder() {
        //return new BCryptPasswordEncoder();
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(requests->
                        // protect both the admin page and the admin javascript asset so navigation to /admin.html triggers authentication
                        requests.requestMatchers("/admin.html", "/admin.js").hasRole("ADMIN")
                                .anyRequest().permitAll()) // alle andere requests zijn toegankelijk voor iedereen
                // Use form-based login so unauthenticated navigations are redirected to a login page (avoids silent 403)
                .formLogin(withDefaults())
                .exceptionHandling(withDefaults()) // Enable exception handling to redirect to login page on access denied
                .csrf((csrf) -> csrf // Cross-Site Request Forgery (CSRF) protection
                        .ignoringRequestMatchers("/h2-console/*") // geen CSRF voor de h2-console
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) //CSRF-token in headers as cookie
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())) // Disable BREACH
                .headers(h -> h.frameOptions(frameOptionsConfig -> frameOptionsConfig.sameOrigin())); // This so embedded frames in h2-console are working
        if (environment.getActiveProfiles().length > 0 && environment.getActiveProfiles()[0].trim().equalsIgnoreCase("test")) {
            http.csrf(csrf -> csrf.disable());
        }
        return http.build();
    }

}
