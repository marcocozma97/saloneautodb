package com.epicode.salone.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "avvisi",
        uniqueConstraints = @UniqueConstraint(columnNames = {"utente_id", "auto_id"}))
public class Avviso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "utente_id")
    private Utente utente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "auto_id")
    private Auto auto;

    // quando il prezzo scende a questo valore o sotto, parte la mail
    @Column(nullable = false)
    private Integer soglia;

    // il "segno che la mail è già partita"
    @Column(nullable = false)
    private boolean inviato;

    // token casuale per il link "disattiva avviso" nella mail
    @Column(nullable = false, unique = true, length = 64)
    private String tokenDisattivazione;

    @Column(nullable = false)
    private LocalDateTime dataCreazione;

    public Avviso() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Utente getUtente() { return utente; }
    public void setUtente(Utente utente) { this.utente = utente; }

    public Auto getAuto() { return auto; }
    public void setAuto(Auto auto) { this.auto = auto; }

    public Integer getSoglia() { return soglia; }
    public void setSoglia(Integer soglia) { this.soglia = soglia; }

    public boolean isInviato() { return inviato; }
    public void setInviato(boolean inviato) { this.inviato = inviato; }

    public String getTokenDisattivazione() { return tokenDisattivazione; }
    public void setTokenDisattivazione(String tokenDisattivazione) { this.tokenDisattivazione = tokenDisattivazione; }

    public LocalDateTime getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(LocalDateTime dataCreazione) { this.dataCreazione = dataCreazione; }
}