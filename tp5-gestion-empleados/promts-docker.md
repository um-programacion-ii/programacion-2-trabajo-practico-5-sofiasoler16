# Prompts de [docker]

## Prompt 1: Como deberia ser el Pomxml
### Prompt Utilizado: 
Por qué no funcionaba ./mvnw spring-boot:run? Me dice: "No such file or directory"

### Respuesta Recibida: 
Eso es bastante común, Sofi 😉. Te paso los motivos:

1. No tenés el wrapper de Maven en tu proyecto

El comando ./mvnw spring-boot:run usa Maven Wrapper, que son estos archivos en la raíz del proyecto:

mvnw
mvnw.cmd
.mvn/


mvnw → script de shell (Linux/Mac).

mvnw.cmd → script para Windows.

.mvn/ → carpeta con configuración.

👉 Si esos archivos no existen en tu carpeta tp5-gestion-empleados/, entonces ./mvnw no se puede ejecutar y te tira No such file or directory.

2. Tu proyecto actual solo tiene pom.xml

Para correrlo, tenés que usar el Maven instalado en tu sistema:

mvn spring-boot:run -Dspring-boot.run.profiles=dev

Eso es lo que estuviste haciendo y está perfecto ✅.

3. Cómo agregar el wrapper (opcional)

Si querés usar ./mvnw (para no depender de tener Maven instalado en cada PC), podés generarlo una vez:

mvn -N wrapper:wrapper


Eso te crea:

./mvnw
./mvnw.cmd
.mvn/wrapper/maven-wrapper.jar
.mvn/wrapper/maven-wrapper.properties


y a partir de ahí ya vas a poder usar:

./mvnw spring-boot:run -Dspring-boot.run.profiles=dev


✅ Resumen:

Si no tenés mvnw → usá mvn.

Si querés usar mvnw → generá el wrapper con mvn -N wrapper:wrapper.

### Modificaciones Realizadas:
- Debia usar Maven Wrapper o seguir usando el comando con mvn

### Explicación del Prompt:
Pregunto por que no funcionaba ./mvnw spring-boot:run

### Aprendizajes Obtenidos:
- para usar ./mvnw spring-boot:run debes tener el wrapper de Maven 

---

## Prompt 2: Como deberia ser el Pomxml
### Prompt Utilizado: 
Por que me pasa esto? sofia@sofia-Inspiron-3501:~/Documentos/programacion2/programacion-2-trabajo-practico-5-sofiasoler16/tp5-gestion-empleados$ sudo docker compose down [sudo] password for sofia: [+] Running 2/2 ✘ Container empleados_postgres Error w... 14.2s ✘ Container empleados_mysql Error whil... 14.3s Error response from daemon: cannot stop container: 4e0683e26fd55990bab7a38b64645248e530bdbff1fa3374895bc69a3af0def6: permission denied

### Respuesta Recibida: 

Ese “permission denied” al hacer docker compose down suele pasar por desajustes de permisos/daemon (mezcla de root vs usuario normal, o el daemon quedó raro). Ve por partes, de más simple a más “bruto”:

Prueba sin sudo (mismo usuario que los levantó):

docker compose ps
docker compose down


Si los levantaste sin sudo alguna vez, a veces el down con sudo habla con otro daemon y no “ve” bien los contenedores.

Intenta forzar el stop/remove por nombre (con sudo):

sudo docker ps -a
sudo docker stop empleados_mysql empleados_postgres
sudo docker rm -f empleados_mysql empleados_postgres


Reinicia el daemon de Docker (suele arreglar el “permission denied”):

sudo systemctl restart docker
luego
docker ps -a


y vuelve a intentar el docker rm -f ... si siguen apareciendo.

Comprueba tu sesión/grupo docker (para no depender siempre de sudo):

groups           # ¿aparece "docker"?
si no:
sudo usermod -aG docker "$USER"
newgrp docker    # o cierra y abre sesión


Verifica que hablas con el mismo daemon/contexto:

docker context ls
docker info | grep -i rootless


Si estás en Docker “rootless” como usuario y a veces usas sudo, puedes tropezar con ese error. Mantén consistencia: o todo con tu usuario + grupo docker, o todo con sudo.

Con eso normalmente queda. Si vuelve a salir “permission denied” incluso tras reiniciar Docker, pega la salida de:

docker ps -a
docker inspect empleados_mysql --format '{{.HostConfig.SecurityOpt}} {{.HostConfig.UsernsMode}}'


y lo vemos.

### Modificaciones Realizadas:
- 

### Explicación del Prompt:
Pregunto por que me da error al hacer docker compose down

### Aprendizajes Obtenidos:
- para hacer docker compose down debes tener permisos de root. Puede haber desajustes de permisos/daemon
- debo estar en el mismo contexto de Docker


---
## Prompt 3: Como deberia ser el Pomxml
### Prompt Utilizado: 
Cómo vacio la base o reinicio los contenedores para evitar datos demasiado grandes al hacer pruebas con curl?

### Respuesta Recibida: 

Pasa por dos cosas (la 1 es la clave):

1) Estás serializando relaciones bidireccionales → JSON “infinito”

Tus entidades tienen ciclos:

Empleado → departamento → empleados → (vuelve a) empleado → ...

Empleado ↔ proyectos (muchos-a-muchos)

Cuando Jackson intenta convertir eso a JSON para /api/empleados, entra al ciclo y genera una respuesta enorme hasta que jq rebienta con “Exceeds depth limit”.

Arreglo rápido (romper el ciclo en JSON)

Tienes tres opciones. Cualquiera sirve; te doy la más simple primero.

Opción A — Ignorar colecciones en el JSON (simple y efectiva)
// Departamento.java
@OneToMany(mappedBy = "departamento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
@com.fasterxml.jackson.annotation.JsonIgnore
private List<Empleado> empleados = new ArrayList<>();

// Proyecto.java
@ManyToMany(mappedBy = "proyectos")
@com.fasterxml.jackson.annotation.JsonIgnore
private Set<Empleado> empleados = new HashSet<>();


Con esto, /api/empleados incluirá el departamento (solo sus campos simples), pero no volverá a listar a todos los empleados del departamento, y lo mismo con proyectos.

Tip: si querés conservar algunos campos del departamento en la respuesta del empleado, puedes además poner en Empleado:

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "departamento_id")
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"empleados"}) // oculta la colección al serializar
private Departamento departamento;

Opción B — Referencias administradas (más detallada)
// Departamento.java
@OneToMany(mappedBy = "departamento", cascade = CascadeType.ALL)
@com.fasterxml.jackson.annotation.JsonManagedReference("dept-emps")
private List<Empleado> empleados = new ArrayList<>();

// Empleado.java
@ManyToOne
@com.fasterxml.jackson.annotation.JsonBackReference("dept-emps")
private Departamento departamento;


Y algo similar para Empleado.proyectos / Proyecto.empleados con otra “key” (p. ej. "emp-proy").

Opción C — Identidades por id (DTO-like sin DTO)
// En las tres entidades:
@com.fasterxml.jackson.annotation.JsonIdentityInfo(
    generator = com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator.class,
    property = "id"
)


Jackson emitirá referencias por id y evita ciclos. Es práctico, pero la forma del JSON cambia (aparecen referencias).

Si quieres tener control total de lo que devuelves, lo mejor es usar DTOs en los controladores (mapear entidad → DTO y elegir campos); pero para tu entrega, con A) basta.

2) Tu base puede estar “cargada” por pruebas anteriores

Si ya hiciste muchos POST o corriste tests que llenan la H2/MySQL/Postgres, el GET /api/empleados traerá muchos registros. Con el ciclo roto (punto 1) ya no explota, pero si quieres empezar “limpio”:

Reset rápido de la base (Docker)
parate en la carpeta del proyecto (donde está docker-compose.yml)
docker compose down -v   # baja contenedores y borra volúmenes (datos)
docker compose up -d     # los levanta de nuevo, vacíos

Reset en H2 (profile dev)

En application-dev.yml usa:

spring:
  jpa:
    hibernate:
      ddl-auto: create-drop


Cada arranque arranca vacío.

¿Por qué tu GET /api/empleados/1 | jq también falla?

Porque ese único empleado incluye el departamento, que incluía todos los empleados (incluido el mismo), y así sucesivamente. Es el mismo ciclo, solo que más chico.

Sobre tu POST de ejemplo

Que el departamento en la respuesta salga con nombre = null es normal si mandaste solo { "id": 1 } y no lo re-hidrataste. No es un error: guardaste por id y Jackson solo serializa lo que está cargado. Si quieres devolver el departamento completo, puedes:

Hacer un fetch explícito en el service (por id) antes de devolver.

O devolver un DTO con sólo departamentoId y/o departamentoNombre.

Pasos recomendados ahora (rápidos)

Rompe el ciclo con la Opción A (añade @JsonIgnore en Departamento.empleados y Proyecto.empleados, y opcionalmente @JsonIgnoreProperties({"empleados"}) en Empleado.departamento).

Reinicia la app con el profile que uses (dev, mysql, postgres).

(Opcional) Si usas Docker: docker compose down -v && docker compose up -d para empezar vacío.

Reintenta:

curl -s http://localhost:8081/api/empleados | jq
curl -s http://localhost:8081/api/empleados/1 | jq


Si aún te devuelve muchísimo, pruébalo paginado (si implementas paginación) o filtra con jq:

curl -s http://localhost:8081/api/empleados | jq '.[] | {id, nombre, email, departamento: (.departamento?.id)}'


¿Querés que te deje el snippet exacto para tus 3 entidades con las anotaciones mínimas (opción A) para que copies y pegues?

### Modificaciones Realizadas:
- Modificaciones en Empleado.java
- Modificaciones en Departamento.java
- Modificaciones en Proyecto.java

### Explicación del Prompt:
Pregunto como arreglar un error de datos para poder usar los curl correctamente

### Aprendizajes Obtenidos:
Como evitar que el ciclo roto (punto 1) explote