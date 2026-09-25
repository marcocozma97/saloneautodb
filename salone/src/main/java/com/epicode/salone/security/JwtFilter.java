package com.epicode.salone.security;

import com.epicode.salone.entity.Utente;
import com.epicode.salone.repository.UtenteRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UtenteRepository utenteRepository;

    public JwtFilter(JwtService jwtService, UtenteRepository utenteRepository) {
        this.jwtService = jwtService;
        this.utenteRepository = utenteRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Long idUtente = jwtService.leggiIdUtente(token);
                Optional<Utente> utente = utenteRepository.findById(idUtente);

                // se l'utente non esiste più non autentichiamo nessuno
                if (utente.isPresent()) {
                    // il ruolo lo prendiamo dal database, non dal token
                    List<SimpleGrantedAuthority> ruoli = List.of(
                            new SimpleGrantedAuthority("ROLE_" + utente.get().getRuolo().name()));

                    // come "principal" salviamo l'id
                    UsernamePasswordAuthenticationToken autenticazione =
                            new UsernamePasswordAuthenticationToken(utente.get().getId(), null, ruoli);

                    SecurityContextHolder.getContext().setAuthentication(autenticazione);
                }
            } catch (JwtException | IllegalArgumentException e) {
                // token falso o scaduto: la richiesta prosegue come se l'utente non fosse loggato
            }
        }

        filterChain.doFilter(request, response);
    }
}