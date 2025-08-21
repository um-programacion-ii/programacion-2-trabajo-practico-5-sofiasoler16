package com.um.tp5.service;

import com.um.tp5.domain.Departamento;

import java.util.List;

public interface DepartamentoService {
    Departamento guardar(Departamento departamento);
    Departamento buscarPorId(Long id);
    Departamento buscarPorNombre(String nombre);
    List<Departamento> obtenerTodos();
    void eliminar(Long id);
}
