package com.epicode.salone.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "preferiti",
        uniqueConstraints = @UniqueConstraint(columnNames = {"utente_id", "auto_id"}))
public class Preferito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "utente_id")
    private Utente utente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "auto_id")
    private Auto auto;

    @Column(nullable = false)
    private LocalDateTime dataAggiunta;

    public Preferito() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Utente getUtente() { return utente; }
    public void setUtente(Utente utente) { this.utente = utente; }

    public Auto getAuto() { return auto; }
    public void setAuto(Auto auto) { this.auto = auto; }

    public LocalDateTime getDataAggiunta() { return dataAggiunta; }
    public void setDataAggiunta(LocalDateTime dataAggiunta) { this.dataAggiunta = dataAggiunta; }
}