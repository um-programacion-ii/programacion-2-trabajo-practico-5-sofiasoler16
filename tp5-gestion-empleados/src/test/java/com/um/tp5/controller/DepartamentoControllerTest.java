package com.um.tp5.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.um.tp5.controllers.DepartamentoController;
import com.um.tp5.controllers.EmpleadoController;
import com.um.tp5.domain.Departamento;
import com.um.tp5.domain.Empleado;
import com.um.tp5.service.DepartamentoService;
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

@WebMvcTest(controllers = DepartamentoController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class DepartamentoControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;

    @SuppressWarnings("removal")
    @MockitoBean
    private DepartamentoService departamentoService;

    private Departamento dep(Long id, String nombre, String desc) {
        Departamento d = new Departamento();
        d.setId(id);
        d.setNombre(nombre);
        d.setDescripcion(desc);
        return d;
    }

    @Test
    void obtenerTodosLosDepartamentos() throws Exception {
        given(departamentoService.obtenerTodos())
                .willReturn(List.of(dep(1L, "IT", "Tecnología"), dep(2L, "RH", "Recursos Humanos")));

        mvc.perform(get("/api/departamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre", is("IT")))
                .andExpect(jsonPath("$[1].nombre", is("RH")));
    }

    @Test
    void obtenerNombre() throws Exception {
        given(departamentoService.buscarPorNombre("RH"))
                .willReturn(dep(2L,"RH","Recursos Humanos"));

        mvc.perform(get("/api/departamentos/nombre/RH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.nombre", is("RH")));
    }

    @Test
    void deleteDepartamento() throws Exception {
        mvc.perform(delete("/api/departamentos/5"))
                .andExpect(status().isNoContent());
    }
}