package com.epicode.salone.repository;

import com.epicode.salone.entity.Preferito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PreferitoRepository extends JpaRepository<Preferito, Long> {

    List<Preferito> findByUtenteId(Long utenteId);

    Optional<Preferito> findByIdAndUtenteId(Long id, Long utenteId);

    boolean existsByUtenteIdAndAutoId(Long utenteId, Long autoId);

    void deleteByUtenteId(Long utenteId);
}