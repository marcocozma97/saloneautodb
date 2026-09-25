package com.epicode.salone.repository;

import com.epicode.salone.entity.Auto;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AutoRepository extends JpaRepository<Auto, Long> {

    // catalogo pubblico senza ricerca
    List<Auto> findByPubblicataTrue(Sort sort);

    // catalogo pubblico con ricerca su marca o modello
    @Query("SELECT a FROM Auto a WHERE a.pubblicata = true AND " +
            "(LOWER(a.marca) LIKE LOWER(CONCAT('%', :testo, '%')) " +
            "OR LOWER(a.modello) LIKE LOWER(CONCAT('%', :testo, '%')))")
    List<Auto> cercaPubblicate(@Param("testo") String testo, Sort sort);

    // una bozza deve risultare "non trovata"
    Optional<Auto> findByIdAndPubblicataTrue(Long id);
}