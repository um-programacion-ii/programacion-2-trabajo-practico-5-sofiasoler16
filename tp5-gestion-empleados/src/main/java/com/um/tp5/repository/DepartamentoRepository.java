package com.um.tp5.repository;

import com.um.tp5.domain.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
    Optional<Departamento> findByNombre(String nombre);
    Optional<Departamento> findByDescripcion(String descripcion);

}
