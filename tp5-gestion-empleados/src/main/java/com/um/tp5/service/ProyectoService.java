package com.um.tp5.service;

import com.um.tp5.domain.Proyecto;

import java.util.List;

public interface ProyectoService {
    Proyecto guardar(Proyecto proyecto);
    Proyecto buscarPorId(Long id);
    List<Proyecto> obtenerTodos();
    List<Proyecto> obtenerActivos();
    Proyecto actualizar(Long id, Proyecto proyecto);
    void eliminar(Long id);
}
