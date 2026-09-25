package com.epicode.salone.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "utenti")
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nome;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    // password cifrata con BCrypt
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Ruolo ruolo;

    @Column(nullable = false)
    private LocalDateTime dataRegistrazione;

    public Utente() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Ruolo getRuolo() { return ruolo; }
    public void setRuolo(Ruolo ruolo) { this.ruolo = ruolo; }

    public LocalDateTime getDataRegistrazione() { return dataRegistrazione; }
    public void setDataRegistrazione(LocalDateTime dataRegistrazione) { this.dataRegistrazione = dataRegistrazione; }
}