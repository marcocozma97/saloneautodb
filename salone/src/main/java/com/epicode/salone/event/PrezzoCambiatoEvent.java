package com.epicode.salone.event;

// l'evento porta solo i dati che servono: quale auto, prezzo prima, prezzo dopo
public record PrezzoCambiatoEvent(Long autoId, Integer vecchioPrezzo, Integer nuovoPrezzo) {
}