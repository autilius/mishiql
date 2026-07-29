# 🐾 MishiQL v3.0.0: Motor de Consultas y Analítica Fluent-DSL para JSON y CSV en Java

MishiQL es un motor de consultas ligero e independiente diseñado bajo el paradigma de **Domain-Specific Language (DSL)**. Permite ejecutar proyecciones, filtrados dinámicos, ordenamientos y agregaciones analíticas sobre estructuras de datos no relacionales (`JsonNode` de Jackson y archivos `.csv`) utilizando una **Fluent API en Spanglish** altamente intuitiva, fluida y fuertemente tipada a nivel de compilación.

Originalmente concebido como el motor de infraestructura interno para la base de datos documental de *MishiMentor*, la **versión 3.0.0 (La Evolución Analítica)** lleva la herramienta a un nivel de infraestructura NoSQL en memoria. Ahora MishiQL no solo filtra y proyecta, sino que ingiere archivos heterogéneos (JSON y CSV) y ejecuta cálculos analíticos al vuelo con máxima precisión.

Bajo la estricta auditoría de calidad de **Mimi-chan (The Orange Boss)** y la supervisión táctica de **Kakashi (Jefe Supremo de Seguridad)**, MishiQL v3.0.0 garantiza un control riguroso de tipos y un manejo de excepciones limpio a través de `MishiQueryException`.

---

## ✨ Características Principales de la v3.0.0

* **NUEVO - Ingesta de Archivos CSV (`MishiQL.desdeCsv`):** Soporte nativo para lectura y transformación transparente de archivos `.csv` a nodos de Jackson en memoria, mapeando inteligentemente números (enteros y decimales), booleanos y texto.
* **NUEVO - Motor Analítico Integrado (`MishiAnalyticsEngine`):** Realiza operaciones de agregación directamente desde la Fluent API sin requerir procesamiento manual posterior:
  * `.cuentalos()`: Conteo directo de registros filtrados.
  * `.promedioDe("campo")`: Cálculo de promedios con redondeo automático a 2 decimales.
  * `.sumaDe("campo")`: Sumatoria de valores numéricos.
  * `.minimoDe("campo")` / `.maximoDe("campo")`: Obtención de valores extremos.
* **NUEVO - Manejo de Excepciones del Dominio (`MishiQueryException`):** Excepción `RuntimeException` unificada con firmas amigables (`🐾 ¡Miau! Error en MishiQL: ...`) para no ensuciar la Fluent API con bloques `try-catch` innecesarios.
* **Spanglish Fluent-DSL:** Diseña consultas complejas con una semántica natural y divertida (`.siElCampo().esIgualA().cuantalos()`) que elimina la verbosidad típica de Java.
* **Arquitectura Basada en Etapas (Stage-Driven):** El encadenamiento de métodos está gobernado por contratos estrictos de interfaces (`TraemeStage`, `BuscaleElStage`, `FiltrameStage`, `AcomodameStage`). El compilador de Java valida la sintaxis de tu consulta en tiempo de desarrollo.
* **Soporte Polimórfico de Entrada:** Puntos de acceso unificados a través de la fachada principal (`MishiQL.java`):
  1. *Memoria Plana:* `MishiQL.desde(List<JsonNode>)`
  2. *Texto Crudo:* `MishiQL.desdeTextoJson(String)`
  3. *Carpeta JSON:* `MishiQL.desdeCarpeta(String)`
  4. *Archivos CSV:* `MishiQL.desdeCsv(String)`
* **Abstracción del Árbol de Sintaxis Abstracta (AST):** Implementación del **Composite Pattern** para encapsular criterios de evaluación simples y compuestos.

---

## 🛠️ La Anatomía del Lenguaje (¿Cómo se usa?)

### 1. Consulta Tradicional (Proyección + Filtro + Ordenamiento)
```java
List<JsonNode> resultados = MishiQL.desdeCarpeta("/home/usuario/.mishi_vault/json")
        .traeme("nombre", "raza", "edad")             // Proyección selectiva de campos
        .siElCampo("raza").esIgualA("Siberiano")       // Criterio de filtrado dinámico
        .acomodadoPor("edad", ModoOrden.AL_DERECHO)   // Criterio de ordenación descriptivo
        .jALALO();                                    // El gran zarpazo: Ejecución del motor
```
### 2. Consultas Analíticas sobre CSV (¡Nuevo en v3.0!)
```java
// Contar cuántos Siberianos hay en un CSV
long totalSiberianos = MishiQL.desdeCsv("datos/gatos.csv")
        .siElCampo("raza").esIgualA("Siberiano")
        .cuentalos();

// Promedio de peso de la tropa felina
double pesoPromedio = MishiQL.desdeCsv("datos/gatos.csv")
        .promedioDe("peso_kg"); // Retorna 4.28 limpio y redondeado

// Suma de edad de gatos sin vacunar
double sumaEdades = MishiQL.desdeCsv("datos/gatos.csv")
        .siElCampo("vacunado").esIgualA("false")
        .sumaDe("edad");
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
    <version>3.0.0</version>
</dependency>
```

## 🛡️ Arquitectura del Motor v3.0
* `com.bugotruco.mishiql.core.api.MishiQL:` Fachada e interfaz principal encargada de resolver las estrategias de I/O (JSON, CSV, memoria).

* `com.bugotruco.mishiql.core.adapter.MishiCsvAdapter:` Adaptador encargado de la lectura, tipado automático e ingesta de archivos CSV hacia el modelo interno.

* `com.bugotruco.mishiql.core.analytics.MishiAnalyticsEngine:` Motor de operaciones agregadas de bajo nivel (SUM, AVG, MIN, MAX, COUNT) con gestión de precisión flotante.

* `com.bugotruco.mishiql.core.exception.MishiQueryException:` Excepción raíz del dominio para el aislamiento de fallos con traza clara.

* `com.bugotruco.mishiql.core.ast:` El núcleo del Árbol de Sintaxis Abstracta basado en el Composite Pattern.

* `com.bugotruco.mishiql.core.engine.MishiEngine:` El procesador de bajo nivel que ejecuta la orden final evaluando los nodos de Jackson.

## 🐾 Staff Oficial del Búnker MishiQL
* Salvador (Autilius) Granados Godínez — Senior Java Developer, Creador y Arquitecto del Proyecto.

* Mimi-chan — The Orange Boss & Auditora de Calidad (Abuela de la tropa).

* Yuna-chan — Senior Project Manager (Madre de la tropa).

* Kakashi-kun — Jefe Supremo de Seguridad y Mediador Táctico (El único héroe capaz de mantener la paz con 8 gatas a su alrededor). 🖤🐈‍⬛

## 🤝 Soporte y Comunidad del Búnker
Este motor es software de infraestructura libre desarrollado con fines de optimización técnica y abstracción arquitectónica en Java. Si este DSL te ha servido en tu laboratorio:
| Plataforma | Enlace |
| :--- | :--- |
| **☕ Cafecito** | [Invítame un café](https://ko-fi.com/bugotruco) |
| **📺 YouTube** | [Canal Oficial Bugotruco](https://www.youtube.com/@Bugotruco) |
| **💸 Donar** | [Apoya el búnker vía PayPal](https://www.paypal.com/paypalme/ChavaGranados) |

Creado con 💖 por Salvador (Autilius) Granados Godínez — Senior Java Developer enfocado en Clean Code, Software Architecture y Seguridad.

Supervisado rigurosamente por Yuna-chan (Senior Project Manager ☕🐾 — Madre de 7 nekos, blanca con manchas cafés y grises).


