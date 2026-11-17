package br.com.cidade_atende_mini.config;

import br.com.cidade_atende_mini.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminUserInitializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserInitializer.class);

    private final AuthService authService;
    private final String adminEmail;
    private final String adminPassword;

    public AdminUserInitializer(
            AuthService authService,
            @Value("${app.admin.email:admin@urbanapp.local}") String adminEmail,
            @Value("${app.admin.password:}") String adminPassword) {
        this.authService = authService;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (adminPassword.isBlank()) {
            logger.info("SuperUser não configurado. Defina ADMIN_PASSWORD para criar o administrador inicial.");
            return;
        }
        if (adminEmail.isBlank()) {
            throw new IllegalStateException("ADMIN_EMAIL não pode ficar vazio.");
        }

        authService.createInitialAdmin(adminEmail, adminPassword);
    }
}
