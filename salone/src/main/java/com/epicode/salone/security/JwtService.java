package com.epicode.salone.security;

import com.epicode.salone.entity.Utente;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey chiave;
    private final long scadenzaMinuti;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.scadenza-minuti}") long scadenzaMinuti) {
        this.chiave = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.scadenzaMinuti = scadenzaMinuti;
    }

    // nel token mettiamo solo l'id dell'utente, la data di creazione e la scadenza
    public String generaToken(Utente utente) {
        Date adesso = new Date();
        Date scadenza = new Date(adesso.getTime() + scadenzaMinuti * 60 * 1000);

        return Jwts.builder()
                .subject(String.valueOf(utente.getId()))
                .issuedAt(adesso)
                .expiration(scadenza)
                .signWith(chiave)
                .compact();
    }

    // se il token è falsificato o scaduto, questo metodo lancia un'eccezione
    public Long leggiIdUtente(String token) {
        String subject = Jwts.parser()
                .verifyWith(chiave)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        return Long.valueOf(subject);
    }
}