package com.um.tp5.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.um.tp5.controllers.EmpleadoController;
import com.um.tp5.domain.Departamento;
import com.um.tp5.domain.Empleado;
import com.um.tp5.service.EmpleadoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EmpleadoController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class EmpleadoControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper om;

    @SuppressWarnings("removal")
    @MockitoBean
    private EmpleadoService empleadoService;

    private Empleado sample() {
        Empleado e = new Empleado();
        e.setId(1L);
        e.setNombre("Juan");
        e.setApellido("Pérez");
        e.setEmail("juan@empresa.com");
        e.setFechaContratacion(LocalDate.now());
        e.setSalario(new BigDecimal("50000"));
        Departamento d = new Departamento();
        d.setNombre("IT");
        e.setDepartamento(d);
        return e;
    }

    @Test
    void obtenerTodos() throws Exception {
        given(empleadoService.obtenerTodos()).willReturn(List.of(sample()));

        mvc.perform(get("/api/empleados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email", is("juan@empresa.com")));
    }

    @Test
    void obtenerPorId() throws Exception {
        given(empleadoService.buscarPorId(1L)).willReturn(sample());

        mvc.perform(get("/api/empleados/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Juan")));
    }

    @Test
    void deleteEmpleado() throws Exception {
        mvc.perform(delete("/api/empleados/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void obtenerEMpleadosPorDepartamento() throws Exception {
        given(empleadoService.buscarPorDepartamento("IT")).willReturn(List.of(sample()));

        mvc.perform(get("/api/empleados/departamento/IT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].departamento.nombre", is("IT")));
    }

    @Test
    void obtenerPorSalarioBetween() throws Exception {
        given(empleadoService.buscarPorRangoSalario(new BigDecimal("1000"), new BigDecimal("90000")))
                .willReturn(List.of(sample()));

        mvc.perform(get("/api/empleados/salario")
                        .param("min", "1000")
                        .param("max", "90000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email", is("juan@empresa.com")));
    }
}
