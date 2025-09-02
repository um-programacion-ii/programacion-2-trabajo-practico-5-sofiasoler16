# Prompts de [test]

## Prompt 1: Error en test
### Prompt Utilizado: 
Este test [codigo test] Me da este error: 'findById(java.lang.Long)' in 'org.springframework.data.repository.CrudRepository' cannot be applied to '(int)'
Como pongo bien el id para el findById?

### Respuesta Recibida: 
Bien ahí avanzando con los tests! Vamos por partes.

Por qué falla este test y cómo arreglarlo

Tu código:

Optional<Departamento> departamentoOptional = departamentoRepository.findById(1);


El CrudRepository<Departamento, Long> espera un Long, y le estás pasando un int. Además, no conviene “adivinar” el id (1): usá el id real que te devolvió save.

Versión corregida (y con aserciones):

@Test
void cuandoBuscarPorDescripcion_retornaDepartamentoPorDescripcion() {
    // Arrange
    Departamento departamento = new Departamento();
    departamento.setNombre("RH");
    departamento.setDescripcion("Departamento de Recursos Humanos");
    departamento = departamentoRepository.save(departamento);

    // Act: usando el ID real (tipo Long)
    Optional<Departamento> depPorId = departamentoRepository.findById(departamento.getId());

    // Assert
    assertTrue(depPorId.isPresent());
    assertEquals("RH", depPorId.get().getNombre());
    assertEquals("Departamento de Recursos Humanos", depPorId.get().getDescripcion());
}


Si además tenés un método tipo findByDescripcion(String descripcion) en el repo, podés probarlo directamente:

Optional<Departamento> depPorDesc = departamentoRepository.findByDescripcion("Departamento de Recursos Humanos");
assertTrue(depPorDesc.isPresent());
assertEquals("RH", depPorDesc.get().getNombre());


Si ese método no existe aún, podés agregarlo en tu DepartamentoRepository:

Optional<Departamento> findByDescripcion(String descripcion);
Optional<Departamento> findByNombre(String nombre);


### Modificaciones Realizadas:
- Cambie el id de int 1 a long 1L

### Explicación del Prompt:
Pregunto por que me da error el test

### Aprendizajes Obtenidos:
- plos id cuando estan como tipo Long no son numeros normales
