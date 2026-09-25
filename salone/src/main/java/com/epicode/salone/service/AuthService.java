package com.epicode.salone.service;

import com.epicode.salone.dto.LoginRequest;
import com.epicode.salone.dto.LoginResponse;
import com.epicode.salone.dto.RegistrazioneRequest;
import com.epicode.salone.dto.UtenteResponse;
import com.epicode.salone.entity.Ruolo;
import com.epicode.salone.entity.Utente;
import com.epicode.salone.exception.ConflictException;
import com.epicode.salone.exception.UnauthorizedException;
import com.epicode.salone.repository.UtenteRepository;
import com.epicode.salone.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UtenteRepository utenteRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.utenteRepository = utenteRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UtenteResponse registra(RegistrazioneRequest richiesta) {
        String email = richiesta.email().trim().toLowerCase();

        if (utenteRepository.existsByEmail(email)) {
            throw new ConflictException("Email già registrata");
        }

        Utente utente = new Utente();
        utente.setNome(richiesta.nome().trim());
        utente.setEmail(email);
        utente.setPassword(passwordEncoder.encode(richiesta.password()));
        utente.setRuolo(Ruolo.USER); // il ruolo lo decide sempre il server
        utente.setDataRegistrazione(LocalDateTime.now());

        Utente salvato = utenteRepository.save(utente);
        return UtenteResponse.from(salvato);
    }

    public LoginResponse login(LoginRequest richiesta) {
        String email = richiesta.email().trim().toLowerCase();

        // stesso messaggio sia se l'email non esiste sia se la password è sbagliata:
        Utente utente = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Email o password errati"));

        if (!passwordEncoder.matches(richiesta.password(), utente.getPassword())) {
            throw new UnauthorizedException("Email o password errati");
        }

        String token = jwtService.generaToken(utente);
        return new LoginResponse(token, utente.getNome(), utente.getRuolo().name());
    }
}