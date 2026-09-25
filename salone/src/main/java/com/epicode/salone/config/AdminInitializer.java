package com.epicode.salone.config;

import com.epicode.salone.entity.Ruolo;
import com.epicode.salone.entity.Utente;
import com.epicode.salone.repository.UtenteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AdminInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;

    public AdminInitializer(UtenteRepository utenteRepository,
                            PasswordEncoder passwordEncoder,
                            @Value("${app.admin.email}") String adminEmail,
                            @Value("${app.admin.password}") String adminPassword) {
        this.utenteRepository = utenteRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        if (!utenteRepository.existsByEmail(adminEmail)) {
            Utente admin = new Utente();
            admin.setNome("Amministratore");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRuolo(Ruolo.ADMIN);
            admin.setDataRegistrazione(LocalDateTime.now());
            utenteRepository.save(admin);

            // nel log non scriviamo né email né password
            log.info("Account amministratore creato");
        }
    }
}