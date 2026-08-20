package com.pdv.repository;

import com.pdv.enums.StatusMesa;
import com.pdv.model.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {

    Optional<Mesa> findByNumero(Integer numero);

    List<Mesa> findByStatus(StatusMesa status);

    long countByStatus(StatusMesa status);
}
