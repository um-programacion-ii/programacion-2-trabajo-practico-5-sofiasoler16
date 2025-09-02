package com.um.tp5.service;

import com.um.tp5.domain.Departamento;
import com.um.tp5.domain.Empleado;
import com.um.tp5.repository.DepartamentoRepository;
import com.um.tp5.repository.EmpleadoRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class DepartamentoServiceIntegrationTest {
    @Autowired private DepartamentoRepository departamentoRepository;
    @Autowired private EmpleadoRepository empleadoRepository;
    @Autowired private DepartamentoService departamentoService;
    @Autowired private EmpleadoService empleadoService;

    @Test
    void guardarDepartamento() {
        Departamento d = new Departamento();
        d.setNombre("Departamento");
        d.setDescripcion("Descripción"); // si tu entidad lo requiere

        // Act: usa SOLO el service
        Departamento guardado = departamentoService.guardar(d);

        // Assert
        assertNotNull(guardado.getId());                  // id autogenerado
        assertEquals("Departamento", guardado.getNombre());
        assertTrue(departamentoRepository.existsById(guardado.getId()));
    }


    @Test
    void buscarPorID_retornaDepartamentoPorID() {

        Departamento departamento = new Departamento();
        departamento.setNombre("RH");
        departamento.setDescripcion("Departamento de Recursos Humanos");
        departamento = departamentoRepository.save(departamento);

        Optional<Departamento> depPorId = departamentoRepository.findById(departamento.getId());

        assertTrue(depPorId.isPresent());
        assertEquals("RH", depPorId.get().getNombre());
        assertEquals("Departamento de Recursos Humanos", depPorId.get().getDescripcion());
    }

    //Repository
    @Test
    void cuandoBuscarPorNombre_retornaDepartamentoPorNombre() {
        Departamento departamento = new Departamento();
        departamento.setNombre("IT");
        departamento.setDescripcion("Departamento de Tecnología");
        departamento = departamentoRepository.save(departamento);

        Optional<Departamento> departamentoOptional = departamentoRepository.findByNombre("IT");
        assertTrue(departamentoOptional.isPresent());
        assertEquals(departamento.getNombre(), departamentoOptional.get().getNombre());
    }

    //Repository
    @Test
    void cuandoBuscarPorDescripcion_retornaDepartamentoPorDescripcion() {

        Departamento departamento = new Departamento();
        departamento.setNombre("RH");
        departamento.setDescripcion("Departamento de Recursos Humanos");
        departamento = departamentoRepository.save(departamento);

        Optional<Departamento> depPorDesc = departamentoRepository.findByDescripcion("Departamento de Recursos Humanos");
        assertTrue(depPorDesc.isPresent());
        assertEquals("RH", depPorDesc.get().getNombre());
    }

    //Repository
    @Test
    void guardarDepartamento_conRepository() {
        Departamento d = new Departamento();
        d.setNombre("Depto Repo");
        d.setDescripcion("desc");

        Departamento guardado = departamentoRepository.save(d);

        assertNotNull(guardado.getId());
        assertEquals("Depto Repo", guardado.getNombre());
    }

    @Test
    void eliminarDepartamento() {
        Departamento d = new Departamento();
        d.setNombre("Temporal");
        d.setDescripcion("x");
        d = departamentoRepository.save(d);

        departamentoService.eliminar(d.getId());
        assertFalse(departamentoRepository.existsById(d.getId()));
    }

    @Test
    void NoNombreUnicoExcepcion() {
        Departamento d1 = new Departamento();
        d1.setNombre("Unico");
        d1.setDescripcion("a");
        departamentoRepository.saveAndFlush(d1);

        Departamento d2 = new Departamento();
        d2.setNombre("Unico");
        d2.setDescripcion("b");

        assertThrows(DataIntegrityViolationException.class, () -> {
            departamentoRepository.saveAndFlush(d2);
        });
    }


}
