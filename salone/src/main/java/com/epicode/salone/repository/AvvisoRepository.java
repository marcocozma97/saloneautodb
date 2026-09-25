package com.epicode.salone.repository;

import com.epicode.salone.entity.Avviso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface AvvisoRepository extends JpaRepository<Avviso, Long> {

    List<Avviso> findByUtenteId(Long utenteId);

    Optional<Avviso> findByIdAndUtenteId(Long id, Long utenteId);

    Optional<Avviso> findByUtenteIdAndAutoId(Long utenteId, Long autoId);

    Optional<Avviso> findByTokenDisattivazione(String token);

    List<Avviso> findByAutoIdAndInviatoFalse(Long autoId);

    void deleteByUtenteId(Long utenteId);

    void deleteByUtenteIdAndAutoId(Long utenteId, Long autoId);

    // il prezzo ha "attraversato" la soglia.
    @Query("SELECT a FROM Avviso a " +
            "WHERE a.auto.id = :autoId " +
            "AND a.inviato = false " +
            "AND a.soglia < :vecchioPrezzo " +
            "AND a.soglia >= :nuovoPrezzo")
    List<Avviso> trovaAvvisiAttraversati(@Param("autoId") Long autoId,
                                         @Param("vecchioPrezzo") Integer vecchioPrezzo,
                                         @Param("nuovoPrezzo") Integer nuovoPrezzo);

    // Restituisce il numero di righe aggiornate
    @Modifying
    @Transactional
    @Query("UPDATE Avviso a SET a.inviato = true WHERE a.id = :id AND a.inviato = false")
    int segnaComeInviato(@Param("id") Long id);
}