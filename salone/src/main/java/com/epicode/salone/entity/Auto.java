package com.epicode.salone.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auto")
public class Auto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String marca;

    @Column(nullable = false, length = 50)
    private String modello;

    @Column(nullable = false)
    private Integer anno;

    @Column(nullable = false)
    private Integer chilometri;

    @Column(length = 2000)
    private String descrizione;

    // prezzo di vendita
    @Column(nullable = false)
    private Integer prezzo;

    // prezzo pagato dal salone (solo l'amministratore)
    @Column(nullable = false)
    private Integer prezzoAcquisto;

    // false = bozza (solo l'amministratore)
    @Column(nullable = false)
    private boolean pubblicata;

    @Column(nullable = false)
    private LocalDateTime dataCreazione;

    public Auto() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModello() { return modello; }
    public void setModello(String modello) { this.modello = modello; }

    public Integer getAnno() { return anno; }
    public void setAnno(Integer anno) { this.anno = anno; }

    public Integer getChilometri() { return chilometri; }
    public void setChilometri(Integer chilometri) { this.chilometri = chilometri; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public Integer getPrezzo() { return prezzo; }
    public void setPrezzo(Integer prezzo) { this.prezzo = prezzo; }

    public Integer getPrezzoAcquisto() { return prezzoAcquisto; }
    public void setPrezzoAcquisto(Integer prezzoAcquisto) { this.prezzoAcquisto = prezzoAcquisto; }

    public boolean isPubblicata() { return pubblicata; }
    public void setPubblicata(boolean pubblicata) { this.pubblicata = pubblicata; }

    public LocalDateTime getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(LocalDateTime dataCreazione) { this.dataCreazione = dataCreazione; }
}