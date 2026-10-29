# GAME HUB — Tienda de videojuegos

Aplicación web para explorar videojuegos, gestionar un carrito y administrar el catálogo de una tienda. Desarrollada con Java 21, Spring Boot, Thymeleaf, Spring Security y MySQL.

## Contenido

- [Funcionalidades](#funcionalidades)
- [Configuración y ejecución](#configuración-y-ejecución)
- [Cuenta de administrador](#cuenta-de-administrador)
- [Validaciones y edición](#validaciones-y-edición)
- [Soporte del proyecto](#soporte-del-proyecto)
- [Comprobar la aplicación](#comprobar-la-aplicación)
- [Historial de cambios](#historial-de-cambios)

## Funcionalidades

| Sección | Funciones disponibles |
| --- | --- |
| Inicio y catálogo | Novedades, búsqueda, filtros, paginación y detalle de cada videojuego. |
| Cuentas | Registro, inicio y cierre de sesión, contraseñas protegidas con BCrypt y acceso según el rol. |
| Carrito | Agregar juegos, modificar cantidades, eliminar productos, vaciar el carrito y consultar subtotales y total. Requiere iniciar sesión y se conserva durante la sesión. |
| Administración | Gestión de juegos, categorías, plataformas y usuarios, con acceso exclusivo para administradores. |
| Dashboard | Totales del catálogo y usuarios, unidades disponibles, alertas de inventario, juegos recientes y accesos rápidos. |
| Soporte | Preguntas frecuentes, formulario de consultas, bandeja para administradores y documentos de términos y privacidad. |

La interfaz se adapta a escritorio y móvil, mantiene una identidad oscura con naranja e incluye portadas de respaldo cuando una imagen no está disponible.

**Alcance actual:** el carrito permite organizar productos, pero no reserva stock. La confirmación de pedidos y los pagos todavía no están habilitados. Tampoco hay un formulario para cambiar o recuperar contraseñas.

## Configuración y ejecución

### Requisitos

- Java 21.
- Maven.
- MySQL en ejecución y una base de datos para la aplicación.

El proyecto Maven está en `Tienda/`. Ejecuta los comandos desde la carpeta principal, donde se encuentran este README y el archivo `.env`.

### Variables de entorno

Abre `.env` y completa las credenciales de tu MySQL:

| Variable | Uso |
| --- | --- |
| `DB_URL` | Dirección de la base. Si se omite, se usa la base `tienda` en MySQL local, puerto `3306`. |
| `DB_USERNAME` | Usuario de MySQL. Obligatorio. |
| `DB_PASSWORD` | Contraseña de MySQL. Obligatoria. |
| `ADMIN_EMAIL` | Correo para crear un administrador nuevo. Opcional, junto con `ADMIN_PASSWORD`. |
| `ADMIN_PASSWORD` | Contraseña del administrador que se creará. Debe cumplir las reglas de contraseña. |
| `ADMIN_NAME` | Nombre del nuevo administrador. Opcional; por defecto, `Administrador`. |

Si ya tienes una cuenta de administrador, puedes dejar vacíos `ADMIN_EMAIL` y `ADMIN_PASSWORD`. El archivo `.env` está excluido de Git y no debe compartirse con credenciales reales.

### Iniciar la aplicación

Desde VS Code, utiliza la configuración Java existente, que carga el `.env` de la carpeta principal. Desde Bash:

```bash
set -a
source .env
set +a
mvn -f Tienda/pom.xml spring-boot:run
```

Abre [http://localhost:8080](http://localhost:8080).

Spring Boot no carga `.env` automáticamente desde la terminal; por eso se exportan las variables antes de ejecutar Maven. Conserva las comillas simples en sus valores. Si una contraseña contiene una comilla simple, configura esa variable desde el entorno del IDE.

Para comprobar únicamente la compilación:

```bash
mvn -f Tienda/pom.xml compile
```

### Preparar MySQL

Si todavía no tienes la base ni un usuario para la aplicación, un administrador de MySQL puede ejecutar lo siguiente. Sustituye la contraseña del ejemplo antes de usarlo:

```sql
CREATE DATABASE IF NOT EXISTS tienda CHARACTER SET utf8mb4;
CREATE USER 'tienda_app'@'localhost' IDENTIFIED BY 'REEMPLAZAR_POR_UNA_CLAVE_PROPIA';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, INDEX, REFERENCES
    ON tienda.* TO 'tienda_app'@'localhost';
```

La aplicación usa `spring.jpa.hibernate.ddl-auto=update` para crear o actualizar las tablas al iniciar. Por eso el usuario necesita los permisos de estructura indicados. Si las tablas de categorías y plataformas están vacías, se cargan sus valores iniciales.

## Cuenta de administrador

- Al iniciar se crean los roles `ADMIN` y `USER` si no existen. Las cuentas del formulario de registro reciben el rol `USER`.
- No se crea una cuenta de administrador predeterminada. Para crearla, configura `ADMIN_EMAIL` y `ADMIN_PASSWORD` antes de iniciar.
- Si el correo configurado ya existe, se conservan su nombre, contraseña y rol. Este mecanismo tampoco convierte una cuenta existente en administrador.
- Una vez creada la cuenta, puedes quitar esas variables del `.env` y del entorno de la terminal antes del siguiente inicio.

Las variables de administrador sirven para crear una cuenta; no restablecen la contraseña de una cuenta existente.

## Validaciones y edición

### Contraseñas

Las cuentas nuevas, incluido el administrador creado por configuración, requieren:

- Un mínimo de **8 caracteres**.
- Al menos **una mayúscula**, **un número** y **un símbolo**, como `*`, `#` o `/`.
- Un máximo de **72 bytes UTF-8**, por el límite de BCrypt.

En el registro, la confirmación debe coincidir. Estas reglas no modifican las contraseñas de las cuentas existentes.

### Formularios

| Formulario | Validaciones principales |
| --- | --- |
| Registro | Nombre obligatorio de hasta 100 caracteres, correo válido de hasta 150 y contraseña con confirmación. |
| Edición de usuarios | Nombre, correo y rol válido. Este formulario no cambia la contraseña. |
| Categorías | Nombre obligatorio de hasta 50 caracteres y sin duplicados. |
| Plataformas | Nombre obligatorio de hasta 30 caracteres y sin duplicados. |
| Juegos | Título de hasta 150 caracteres, descripción, fecha, categoría y plataforma válidas; stock entero no negativo y precio no negativo con hasta 8 cifras enteras y 2 decimales. |
| Consultas de ayuda | Nombre de hasta 100 caracteres, correo válido de hasta 150, asunto de hasta 120 y mensaje de 10 a 2000 caracteres. Todos son obligatorios. |

### Duplicados y permisos

- Se eliminan los espacios de los extremos en nombres de usuarios, categorías y plataformas, y en los correos. Los correos se normalizan a minúsculas.
- No se permiten correos, categorías o plataformas duplicados, sin distinguir mayúsculas. Por ejemplo, `PC` y ` pc ` se consideran la misma plataforma.
- Al editar, puedes conservar el valor del propio registro, pero no usar uno que pertenezca a otro.
- Los errores aparecen junto al campo correspondiente y se conservan los datos no sensibles. Las contraseñas deben escribirse otra vez.
- Un administrador no puede eliminar su propia cuenta, quitarse su rol ni dejar la tienda sin administradores. Si modifica su propio correo, se actualiza su sesión.
- Los registros inexistentes muestran una página 404; los parámetros malformados, una página 400. Los errores inesperados se muestran sin detalles internos.

## Soporte del proyecto

### Documentos y Centro de ayuda

Los enlaces están en el footer:

| Recurso | Ruta | Acceso |
| --- | --- | --- |
| Términos y condiciones | `/soporte/terminos` | Público. |
| Política de privacidad | `/soporte/privacidad` | Público. |
| Centro de ayuda | `/soporte/ayuda` | Público, con o sin sesión. |
| Consultas recibidas | `/admin/soporte` | Solo administradores. |

Los términos y la política se abren en modales. Pueden cerrarse con el botón, la tecla Escape o el fondo; después, el foco vuelve al enlace de origen. También tienen páginas propias como alternativa sin JavaScript.

### Consultas

El Centro de ayuda incluye preguntas frecuentes y un formulario con nombre, correo, asunto y mensaje. Al enviarlo correctamente:

1. La consulta se guarda en MySQL, en la tabla `consultas_soporte`, con fecha y hora de Lima.
2. Se muestra un número de referencia en pantalla.
3. El administrador puede leerla desde **Dashboard → Consultas de ayuda**, en un listado de diez registros por página.

El envío está protegido con CSRF. No se envían confirmaciones ni respuestas automáticas por correo.

Los textos de soporte describen las funciones disponibles y deben actualizarse cuando cambie el uso de los datos o se habiliten pedidos y pagos.

## Comprobar la aplicación

Inicia la aplicación con tu `.env` y revisa estos recorridos directamente en la base indicada en `DB_URL`. Las operaciones de creación, edición y eliminación guardan cambios en esa misma base; no necesitas activar otro perfil.

| Área | Qué comprobar |
| --- | --- |
| Inicio y catálogo | Buscar un juego, combinar filtros, abrir su detalle y revisar la visualización en móvil. |
| Registro y acceso | Crear una cuenta válida e iniciar sesión. Intentar registrar un correo repetido y una contraseña que no cumpla los requisitos. |
| Carrito | Agregar un juego, cambiar cantidades, comprobar el límite de stock, eliminar productos y vaciar la selección. Revisar subtotales y total. |
| Administración | Crear y editar juegos, categorías y plataformas. Enviar campos vacíos, nombres duplicados y valores negativos para revisar los mensajes. |
| Usuarios | Editar una cuenta conservando su correo e intentar usar el de otra. Comprobar las protecciones del administrador. |
| Dashboard | Contrastar los totales con MySQL. Revisar que el stock bajo corresponda a juegos con entre 1 y 3 unidades y comprobar los enlaces de edición. |
| Soporte | Abrir y cerrar las modales, enviar un formulario incompleto y registrar una consulta válida. Comprobar su referencia y leerla como administrador. |
| Errores y permisos | Abrir `/juegos/abc` para comprobar el error 400 y un ID inexistente para el 404. Intentar acceder a `/admin/soporte` sin sesión de administrador. |

## Historial de cambios

### v2.4 — Soporte y enlaces del footer

- Se añadieron modales y páginas de términos y condiciones y política de privacidad, con textos orientados al cliente.
- Se incorporó el Centro de ayuda con preguntas frecuentes, validación del formulario y registro de consultas en MySQL.
- Se añadió la bandeja de consultas para administradores, con paginación y acceso desde el dashboard.

### v2.3 — Dashboard e inventario

- Se añadieron indicadores de unidades disponibles, juegos agotados y stock bajo.
- Se incorporaron hasta cinco juegos que necesitan atención y los cinco últimos registrados, con accesos a edición.
- Se adaptó el panel a móvil y se añadieron estados para inventario vacío y ausencia de alertas.

### v2.2 — Validaciones y manejo de errores

- Se reforzaron las reglas de contraseña y la validación de usuarios, categorías y plataformas.
- Se mejoraron los mensajes por campo, el control de duplicados y las protecciones del administrador.
- Se incorporaron totales reales al dashboard y páginas de error sin detalles internos.

### v2.1 — Renovación visual de la tienda

- Se renovaron el inicio, el catálogo y las tarjetas de videojuegos, manteniendo la identidad oscura con naranja.
- Se mejoraron los filtros, la visualización de precios y disponibilidad y las portadas de respaldo.
- Se adaptaron la navegación y el menú a móvil, con mejoras de teclado, foco y tamaño de los botones.
