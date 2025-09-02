# Prompts de [desarrollo]

## Prompt 1: Como deberia ser el Pomxml
### Prompt Utilizado: 
El pom.xml de mi trabajo anterior es asi [pom del tp4], como deberia adaptarlo si quiero agregar las dependencias: springframework, h2database, mysql, postgresql, lombook y mockito?


### Respuesta Recibida: 
¡Vamos, Sofi! Acá tenés un pom.xml base ya adaptado con Spring (web + JPA), H2, MySQL, PostgreSQL, Lombok y Mockito.
[pom.xml]

### Modificaciones Realizadas:
- Use el pom.xml para saber como hacer el de este tp
- Poner se necesitan las dependencias 

### Explicación del Prompt:
Queria saber como hacer el pom.xml con la estructura correcta para las dependencias

### Aprendizajes Obtenidos:
- como estructurar un pom.xml
- como agregar dependencias

---

## Prompt 2: Error al correr app en modo desarrollo con H2
### Prompt Utilizado: 
Por que cuando pruebo correr la app en modo desarrollo con H2 me da error? [ El error de la terminal ]

### Respuesta Recibida: 
Eso que te sale es un error de paquetes (packages) desalineados.

El compilador está intentando compilar un archivo en
src/main/java/**org**/um/tp5/repository/EmpleadoRepository.java
pero adentro de ese archivo vos importás com.um.tp5.domain.*. En tu proyecto (el zip que subiste) las entidades están en package com.um.tp5.domain y los repos en package com.um.tp5.repository. O sea: todo está bajo com.um.tp5.*, no org.um.tp5.*.

Por eso Maven te dice:

package com.um.tp5.domain does not exist


(no las encuentra desde esa “otra” raíz org/...).

Cómo arreglarlo (rápido y seguro)

Ubicá cualquier carpeta/archivo bajo src/main/java/org/um/tp5/
En tu terminal, dentro del módulo del TP:

cd tp5-gestion-empleados
find src/main/java -path "*/org/um/tp5/*" -type f -print


Si aparece src/main/java/org/um/tp5/repository/EmpleadoRepository.java (u otros), esa es la causa.

Mové todo lo que esté en org/um/tp5 a com/um/tp5
(y asegurate de que la primera línea del archivo diga package com.um.tp5.loquesea;)

mkdir -p src/main/java/com/um/tp5
si hay subcarpetas (repository, service, etc.), crealas también:
mkdir -p src/main/java/com/um/tp5/repository

mover (ajustá por cada archivo que te liste el find)
mv src/main/java/org/um/tp5/repository/EmpleadoRepository.java src/main/java/com/um/tp5/repository/


Verificá el package en cada clase
Abrí los .java que moviste y confirmá que la primera línea sea coherente:

Repositorios: package com.um.tp5.repository;

Entidades: package com.um.tp5.domain;

Controladores: package com.um.tp5.controllers;

Main app: package com.um.tp5;

Si alguna clase dice package org.um.tp5...;, cambiala a com.um.tp5...;.

Limpieza y build

mvn clean test


Si compila y pasan los tests, ya podés correr:

mvn spring-boot:run -Dspring-boot.run.profiles=dev

¿Por qué pasó?

Java no “obliga” a que la ruta coincida con el package, pero en proyectos Maven/IDE es estándar y el compilador arma el classpath por esas rutas. Si tenés clases con package com.um.tp5... pero físicamente guardadas en src/main/java/org/um/tp5/..., terminás con un árbol duplicado y el compilador no encuentra las referencias.


### Explicación del Prompt:
Pregunto por que al correr el app en modo desarrollo con H2 me da error.

### Modificaciones Realizadas:
- No hubo modificaciones en el código, solo comandos por terminal


### Aprendizajes Obtenidos:
- como arreglar el error
---

## Prompt 3: @temporal
### Prompt Utilizado: 
Que significa el uso de @temporal?

### Respuesta Recibida: 

Bien, Sofi 🙌, te explico el uso de @Temporal en JPA/Hibernate:

¿Qué es?

@Temporal es una anotación que se aplica sobre atributos de tipo java.util.Date o java.util.Calendar en entidades JPA para indicar cómo se debe mapear el valor en la base de datos.

¿Por qué existe?

En Java, la clase Date guarda fecha y hora juntas, pero en SQL podés querer guardar solo:

la fecha (DATE),

la hora (TIME),

o ambas (TIMESTAMP).

Sin @Temporal, JPA no sabría si debe persistir solo la fecha, solo la hora o el timestamp completo.

Sintaxis
@Temporal(TemporalType.DATE)
private Date fechaNacimiento;

Tipos posibles (TemporalType)

TemporalType.DATE → solo la fecha (año, mes, día).
→ Se mapea a SQL DATE.

TemporalType.TIME → solo la hora (hora, minuto, segundo).
→ Se mapea a SQL TIME.

TemporalType.TIMESTAMP → fecha y hora completas.
→ Se mapea a SQL TIMESTAMP.

Ejemplo real

En tu TP, si tuvieras:

@Entity
public class Empleado {

    @Temporal(TemporalType.DATE)
    private Date fechaIngreso;

    @Temporal(TemporalType.TIMESTAMP)
    private Date ultimaActualizacion;
}


fechaIngreso → se guarda como DATE (ej: 2025-09-02).

ultimaActualizacion → se guarda como TIMESTAMP (ej: 2025-09-02 14:36:12).

Nota con Java 8+

Si usás java.time (LocalDate, LocalDateTime, etc.), no necesitás @Temporal, porque esas clases ya representan explícitamente si llevan fecha, hora o ambas. Por eso muchos proyectos nuevos prefieren LocalDate/LocalDateTime en lugar de Date.

👉 Resumen: @Temporal le dice a JPA qué parte de un Date o Calendar querés guardar en la base de datos (fecha, hora o ambas).

¿Querés que te muestre dónde en tu proyecto estás usando @Temporal y cómo quedaría si lo migrás a LocalDate o LocalDateTime?

### Explicación del Prompt:
Pregunto para que se usa @temporal

### Modificaciones Realizadas:
- No hubo modificaciones en el código


### Aprendizajes Obtenidos:
- para que se usa @temporal

---

## Prompt 4: Error con LocalDate.now
### Prompt Utilizado: 
POr que en el ProyectoServiceImpl me pasa este error con LocalDate.now?

### Respuesta Recibida: 
Ya vi tu error 👀. El problema no está en el ProyectoServiceImpl, sino en la firma del método del repositorio.

En tu ProyectoServiceImpl estás llamando a:

proyectoRepository.findActivos(LocalDate.now());


Eso significa que en tu ProyectoRepository deberías tener un método que reciba un LocalDate como parámetro.
Pero seguramente lo definiste así (sin parámetros o con otra firma):

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
    // probablemente lo tenés distinto
    @Query("SELECT p FROM Proyecto p WHERE p.fechaFin > CURRENT_DATE")
    List<Proyecto> findActivos();
}


👉 Eso explica el error: estás intentando pasar LocalDate.now(), pero tu método no espera ningún argumento.

Soluciones posibles

Tenés dos opciones correctas:

🔹 Opción 1 — Usar CURRENT_DATE directo en JPQL (sin parámetros)
@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

    @Query("SELECT p FROM Proyecto p WHERE p.fechaFin > CURRENT_DATE")
    List<Proyecto> findActivos();
}


Y en el service:

@Override
@Transactional(readOnly = true)
public List<Proyecto> obtenerActivos() {
    return proyectoRepository.findActivos();
}

🔹 Opción 2 — Pasar la fecha como parámetro
@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

    @Query("SELECT p FROM Proyecto p WHERE p.fechaFin > :fechaActual")
    List<Proyecto> findActivos(@Param("fechaActual") LocalDate fecha);
}


Y en el service:

@Override
@Transactional(readOnly = true)
public List<Proyecto> obtenerActivos() {
    return proyectoRepository.findActivos(LocalDate.now());
}

Recomendación

La más limpia es la Opción 1 con CURRENT_DATE, porque delegás a la base de datos la noción de “hoy”.

### Explicación del Prompt:
Pregunto por que tengo un error en LocalDate.now

### Modificaciones Realizadas:
- Cambie el codigo para incluir la fecha como parámetro

### Aprendizajes Obtenidos:
- para usar LocalDate.now debo tener un metodo que use date como parametro
