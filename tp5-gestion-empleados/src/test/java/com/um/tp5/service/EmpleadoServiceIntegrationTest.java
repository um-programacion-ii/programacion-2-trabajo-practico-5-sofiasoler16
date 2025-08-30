package com.um.tp5.service;

import com.um.tp5.domain.Departamento;
import com.um.tp5.domain.Empleado;
import com.um.tp5.repository.DepartamentoRepository;
import com.um.tp5.repository.EmpleadoRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class EmpleadoServiceIntegrationTest {

    @Autowired private EmpleadoService empleadoService;
    @Autowired private EmpleadoRepository empleadoRepository;
    @Autowired private DepartamentoRepository departamentoRepository;
    
    @Test
    void cuandoGuardarEmpleado_entoncesSePersisteCorrectamente() {
        // Arrange
        Departamento departamento = new Departamento();
        departamento.setNombre("IT");
        departamento.setDescripcion("Departamento de Tecnología");
        departamento = departamentoRepository.save(departamento);

        Empleado empleado = new Empleado();
        empleado.setNombre("Juan");
        empleado.setApellido("Pérez");
        empleado.setEmail("juan.perez@empresa.com");
        empleado.setFechaContratacion(LocalDate.now());
        empleado.setSalario(new BigDecimal("50000.00"));
        empleado.setDepartamento(departamento);

        // Act
        Empleado empleadoGuardado = empleadoService.guardar(empleado);

        // Assert
        assertNotNull(empleadoGuardado.getId());
        assertEquals("juan.perez@empresa.com", empleadoGuardado.getEmail());
        assertTrue(empleadoRepository.existsById(empleadoGuardado.getId()));
    }

    @Test
    void cuandoBuscarPorEmailExistente_entoncesRetornaEmpleado() {
        // Arrange

        Departamento departamento = new Departamento();
        departamento.setNombre("IT");
        departamento.setDescripcion("Departamento de Tecnología");
        departamento = departamentoRepository.save(departamento);

        Empleado empleado = new Empleado();
        empleado.setNombre("Test");
        empleado.setApellido("Empleado");
        empleado.setEmail("test@empresa.com");
        empleado.setFechaContratacion(LocalDate.now());
        empleado.setSalario(new BigDecimal("50000.00"));
        empleado.setDepartamento(departamento);
        empleadoRepository.save(empleado);

        // Act
        Optional<Empleado> resultado = empleadoRepository.findByEmail("test@empresa.com");

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("test@empresa.com", resultado.get().getEmail());
    }
}