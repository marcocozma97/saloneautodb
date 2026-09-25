package com.epicode.salone.repository;

import com.epicode.salone.entity.Avviso;
import org.springframework.data.jpa.repository.JpaRepository;

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
}