package com.um.tp5.service;

import com.um.tp5.domain.Departamento;
import com.um.tp5.domain.Empleado;
import com.um.tp5.exceptions.RecursoNoEncontradoException;
import com.um.tp5.repository.DepartamentoRepository;
import com.um.tp5.repository.EmpleadoRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class EmpleadoServiceIntegrationTest {

    @Autowired private EmpleadoService empleadoService;
    @Autowired private EmpleadoRepository empleadoRepository;
    @Autowired private DepartamentoRepository departamentoRepository;

    private Departamento nuevoDepto(String nombre) {
        Departamento d = new Departamento();
        d.setNombre(nombre);
        d.setDescripcion("Desc " + nombre);
        return departamentoRepository.save(d);
    }

    private Empleado nuevoEmpleado(String nombre, String email, BigDecimal salario, Departamento d) {
        Empleado e = new Empleado();
        e.setNombre(nombre);
        e.setApellido("Apellido");
        e.setEmail(email);
        e.setFechaContratacion(LocalDate.now());
        e.setSalario(salario);
        e.setDepartamento(d);
        return empleadoRepository.save(e);
    }
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

    @Test
    void obtenerTodosLosEmpleados() {
        Departamento departamento = new Departamento();
        departamento.setNombre("IT");
        departamento.setDescripcion("Departamento de Tecnología");
        departamento = departamentoRepository.save(departamento);

        Empleado e1 = new Empleado();
        e1.setNombre("Marta");
        e1.setApellido("Gonzalez");
        e1.setEmail("martita@empresa.com");
        e1.setFechaContratacion(LocalDate.now());
        e1.setSalario(new BigDecimal("1000000.00"));
        e1.setDepartamento(departamento);

        Empleado e2 = new Empleado();
        e2.setNombre("Josefa");
        e2.setApellido("Garmendia");
        e2.setEmail("Garmendita@empresa.com");
        e2.setFechaContratacion(LocalDate.now());
        e2.setSalario(new BigDecimal("40000.00"));
        e2.setDepartamento(departamento);

        empleadoRepository.save(e1);
        empleadoRepository.save(e2);

        List<Empleado> resultado = empleadoService.obtenerTodos();

        assertEquals(2, resultado.size());

        List<String> resultado2 = resultado.stream().map(Empleado::getEmail).toList();
        assertTrue(resultado2.contains("martita@empresa.com"));
        assertTrue(resultado2.contains("Garmendita@empresa.com"));
    }

    @Test
    void buscarPorId_noExiste_lanzaExcepcion() {
        assertThrows(RecursoNoEncontradoException.class, () -> empleadoService.buscarPorId(99L));
    }

    @Test
    void buscarPorId_ok_service() {
        Departamento d = nuevoDepto("IT");
        Empleado e = nuevoEmpleado("Ana", "ana@empresa.com", new BigDecimal("60000.00"), d);

        Empleado encontrado = empleadoService.buscarPorId(e.getId());
        assertEquals("ana@empresa.com", encontrado.getEmail());
    }


    @Test
    void buscarPorDepartamento_ok_service() {
        Departamento dep1 = nuevoDepto("IT");
        Departamento dep2 = nuevoDepto("RH");

        nuevoEmpleado("Ana", "ana@empresa.com", new BigDecimal("60000"), dep1);
        nuevoEmpleado("Beto", "beto@empresa.com", new BigDecimal("70000"), dep2);

        List<Empleado> enDep1 = empleadoService.buscarPorDepartamento("IT");
        assertEquals(1, enDep1.size());
        assertEquals("ana@empresa.com", enDep1.getFirst().getEmail());
    }

    //Repository
    @Test
    void cuandoBuscarPorSalarioBetween_entoncesRetornaEmpleado() {
        Departamento departamento = new Departamento();
        departamento.setNombre("IT");
        departamento.setDescripcion("Departamento de Tecnología");
        departamento = departamentoRepository.save(departamento);

        Empleado empleado = new Empleado();
        empleado.setNombre("Marta");
        empleado.setApellido("Gonzalez");
        empleado.setEmail("martita@empresa.com");
        empleado.setFechaContratacion(LocalDate.now());
        empleado.setSalario(new BigDecimal("1000000.00"));
        empleado.setDepartamento(departamento);
        empleadoRepository.save(empleado);


        List<Empleado> resultado = empleadoRepository.findBySalarioBetween(
                new BigDecimal("500000.00"),
                new BigDecimal("1200000.00")
        );
        assertFalse(resultado.isEmpty());
        assertEquals("martita@empresa.com", resultado.getFirst().getEmail());
    }


    @Test
    void eliminar_ok() {
        Departamento departamento = new Departamento();
        departamento.setNombre("IT");
        departamento.setDescripcion("Departamento de Tecnología");
        departamento = departamentoRepository.save(departamento);

        Empleado e1 = new Empleado();
        e1.setNombre("Marta");
        e1.setApellido("Gonzalez");
        e1.setEmail("martita@empresa.com");
        e1.setFechaContratacion(LocalDate.now());
        e1.setSalario(new BigDecimal("1000000.00"));
        e1.setDepartamento(departamento);
        empleadoRepository.save(e1);

        empleadoService.eliminar(e1.getId());

        assertFalse(empleadoRepository.existsById(e1.getId()));
    }

    @Test
    void guardarCorrectamenteEmpleado() {
        Departamento d = nuevoDepto("IT");
        Empleado e = new Empleado();
        e.setNombre("Juan");
        e.setApellido("Pérez");
        e.setEmail("juan.perez@empresa.com");
        e.setFechaContratacion(LocalDate.now());
        e.setSalario(new BigDecimal("50000.00"));
        e.setDepartamento(d);

        Empleado guardado = empleadoService.guardar(e);

        assertNotNull(guardado.getId());
        assertTrue(empleadoRepository.existsById(guardado.getId()));
        assertEquals("juan.perez@empresa.com", guardado.getEmail());
    }

    @Test
    void actualizarEmpleado() {
        Departamento d = nuevoDepto("IT");
        Empleado e = nuevoEmpleado("Pepe", "pepe@empresa.com", new BigDecimal("80000.00"), d);

        Empleado pepe = new Empleado();
        pepe.setNombre("Pepe Mod");
        pepe.setApellido("Mod");
        pepe.setEmail("pepe.mod@empresa.com");
        pepe.setFechaContratacion(e.getFechaContratacion());
        pepe.setSalario(new BigDecimal("90000.00"));
        pepe.setDepartamento(d);

        Empleado actualizado = empleadoService.actualizar(e.getId(), pepe);

        assertEquals("Pepe Mod", actualizado.getNombre());
        assertEquals(new BigDecimal("90000.00"), actualizado.getSalario());
        assertEquals("pepe.mod@empresa.com", actualizado.getEmail());
    }

}