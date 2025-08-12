package com.um.tp5.repository;

import com.um.tp5.domain.Departamento;
import com.um.tp5.domain.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    Optional<Empleado> findByEmail(String email);

    List<Empleado> findByDepartamento(Departamento departamento);

    List<Empleado> findBySalarioBetween(BigDecimal salarioMin, BigDecimal salarioMax);

    List<Empleado> findByFechaContratacionAfter(LocalDate fecha);

}
