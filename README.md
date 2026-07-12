# 🐾 MishiQL v2.0: Motor de Consultas Fluent-DSL para Árboles JSON en Java

MishiQL es un motor de consultas ligero e independiente diseñado bajo el paradigma de **Domain-Specific Language (DSL)**. Permite ejecutar proyecciones, filtrados dinámicos y ordenamientos sobre estructuras de datos no relacionales (`JsonNode` de Jackson) utilizando una **Fluent API en Spanglish** altamente intuitiva, fluida y fuertemente tipada a nivel de compilación.

Originalmente concebido como el motor de infraestructura interno para la base de datos documental de *MishiMentor*, la **versión 2.0 (La Emancipación)** independiza por completo el núcleo del sistema. Ahora MishiQL funciona de manera 100% autónoma como una librería híbrida: puede procesar colecciones en memoria, integrarse como puente estructural a orquestadores externos o devorar archivos planos `.json` directamente desde el sistema de archivos de tu entorno distribuido.

Bajo la estricta auditoría de calidad de **Mimi-chan (The Orange Boss)**, MishiQL v2.0 garantiza un control riguroso de tipos en tiempo de diseño, evitando estados inválidos mediante el uso de interfaces de transición por etapas (*Stage-Driven Architecture*).

---

## ✨ Características Principales de la v2.0

* **Spanglish Fluent-DSL (Nuevo):** Diseña consultas complejas con una semántica natural y divertida (`.traeme().siElCampo().esIgualA().jALALO()`) que elimina la verbosidad típica de las consultas nativas en Java.
* **Arquitectura Basada en Etapas (Stage-Driven):** El encadenamiento de métodos está gobernado por contratos estrictos de interfaces (`TraemeStage`, `BuscaleElStage`, `FiltrameStage`, `AcomodameStage`). El compilador de Java valida la sintaxis de tu consulta en tiempo de desarrollo; es imposible poner un filtro antes de seleccionar los campos.
* **Autonomía del Sistema de Archivos (Nuevo):** Capacidad nativa de mapear carpetas del usuario en disco. El motor procesa de forma asíncrona y defensiva archivos planos `.json`, extrayendo tanto objetos únicos `{}` como arreglos homogéneos `[{}]`.
* **Soporte Híbrido de Entrada:** Tres puntos de acceso unificados a través de una fachada limpia (`MishiQL.java`):
    1. *Memoria Plana:* `MishiQL.desde(List<JsonNode>)` para colecciones vivas.
    2. *Texto Crudo:* `MishiQL.desdeTextoJson(String)` para payloads e integraciones de API.
    3. *Disco Local:* `MishiQL.desdeCarpeta(String)` para almacenamiento persistente autónomo.
* **Abstracción del Árbol de Sintaxis Abstracta (AST):** Implementación interna del **Composite Pattern** para encapsular criterios de evaluación simples (`MishiCriterioSimple`) y compuestos (`MishiCriterioCompuesto`), permitiendo la extensión futura a operadores lógicos complejos anidados (`AND`/`OR`) sin romper el motor de ejecución.
* **Cero Fugas de Memoria:** Uso centralizado y estático del `ObjectMapper` de Jackson, optimizando el consumo de recursos en la JVM durante auditorías masivas de registros.

---

## 🛠️ La Anatomía del Lenguaje (¿Cómo se usa?)

Olvídate de la rigidez de los CRUDs tradicionales. Con MishiQL, las consultas recuperan la flexibilidad de los lenguajes declarativos:

```java
List<JsonNode> resultados = MishiQL.desdeCarpeta("/home/usuario/.mishi_vault/json")
        .traeme("nombre", "raza", "edad")             // Proyección selectiva de campos
        .siElCampo("raza").esIgualA("Siberiano")       // Criterio de filtrado dinámico
        .acomodadoPor("edad", ModoOrden.AL_DERECHO)   // Criterio de ordenación descriptivo
        .jALALO();                                    // El gran zarpazo: Ejecución del motor 
```

## 💡 Casos de Uso
* **Ecosistemas Agénticos Locales:** Infraestructura de almacenamiento rápido para registrar prompts, embeddings y logs de IA en formato JSON plano.

* **Librería Core Reutilizable:** Empaqueta MishiQL como un .jar local e inyéctalo en cualquier microservicio (Spring Boot, Quarkus) o herramientas de CLI que manejen persistencia documental ligera.

* **Auditoría de Datos Rápida:** Analiza repositorios de configuración o bitácoras en caliente sin necesidad de montar una infraestructura pesada de base de datos (NoSQL/Relacional).

## 🚀 Instalación y Despliegue Local
Requisitos del Entorno
* Java Development Kit (JDK): v17 o superior.

* Gestor de Proyectos: Apache Maven 3.6+.

Compilación e Instalación en el Repositorio Local (.m2)
Clona el repositorio en tu máquina de desarrollo y ejecuta en la terminal de tu entorno Linux:
```mvn clean install```

Integración en tu pom.xml
Para consumir el DSL en cualquier otro desarrollo de tu búnker, añade la dependencia correspondiente:
```
<dependency>
    <groupId>com.bugotruco</groupId>
    <artifactId>mishiql</artifactId>
    <version>2.0.0</version>
</dependency>
```

## 🛡️ Arquitectura del Motor v2.0
El software se diseñó bajo un desacoplamiento estricto de responsabilidades, respetando los principios **SOLID**:

* ```com.bugotruco.mishiql.core.api.MishiQL:``` **Fachada e itf principal encargada de resolver las estrategias de I/O y Entrada/Salida.**

* ```com.bugotruco.mishiql.core.api.MishiQueryBuilder:``` **El constructor fluido que implementa las etapas de la Fluent API y resguarda el estado de la consulta en caliente.**

* ```com.bugotruco.mishiql.core.ast:``` **El núcleo del Árbol de Sintaxis. Contiene la abstracción de MishiCriterio y modela el comportamiento del Composite Pattern para aislar la lógica de los operadores (esIgualA, noEsIgualA, esMayorA, etc.).**

* ```com.bugotruco.mishiql.core.engine.MishiEngine:``` **El procesador de bajo nivel que ejecuta la orden final evaluando los nodos de Jackson contra el AST estructurado.**

## 🤝 Soporte y Comunidad del Búnker
Este motor es software de infraestructura libre desarrollado con fines de optimización técnica y abstracción arquitectónica en Java. Si este DSL te ha servido para mandar a volar los CRUDs aburridos y deseas apoyar el desarrollo en el laboratorio:

| Plataforma | Enlace |
| :--- | :--- |
| **☕ Cafecito** | [Invítame un café](https://ko-fi.com/bugotruco) |
| **📺 YouTube** | [Canal Oficial Bugotruco](https://www.youtube.com/@Bugotruco) |
| **💸 Donar** | [Apoya el búnker vía PayPal](https://www.paypal.com/paypalme/ChavaGranados) |

Creado con 💖 por Salvador (Autilius) Granados Godínez — Senior Java Developer enfocado en Clean Code, Software Architecture y Seguridad.

Supervisado rigurosamente por Yuna-chan (Senior Project Manager ☕🐾 — Madre de 7 nekos, blanca con manchas cafés y grises).


