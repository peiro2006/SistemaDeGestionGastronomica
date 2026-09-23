# Sistema de Gestion Gastronomica — Hambur-Besa

> Sistema de gestion gastronomica full stack para administrar pedidos, caja, productos, proveedores y cocina en tiempo real. Dirigido a equipos gastronomicos que necesitan gestionar sus operaciones desde un unico punto.

---

## Estado del Proyecto

| Criterio | Estado |
|---|---|
| **Versión** | 1.0.0 |
| **Backend** | Java 21 / Spring Boot 4.0.7 |
| **Frontend** | Angular 21 |
| **Base de Datos** | PostgreSQL 16+ (Neon DB) |
| **Estado del Build** | Compilacion exitosa |
| **Licencia** | Academico |

---

## Tecnologias Utilizadas

- **Backend:** Java 21, Spring Boot 4.0.7, Maven, Spring Data JPA, Lombok
- **Frontend:** Angular 21, TypeScript 5.9, Chart.js 4.5, ng2-charts, Vitest
- **Base de Datos:** PostgreSQL 16+ (Neon DB serverless)
- **Autenticacion:** JWT (HS256) con Spring Security
- **Seguridad:** CORS global, BCrypt password encoding
- **Testing:** JUnit 5 (backend), Vitest (frontend)

---

## Prerrequisitos

Asegurate de tener instalado lo siguiente antes de comenzar:

- Git — [git-scm.com](https://git-scm.com/)
- Java JDK 21 o superior — [Oracle](https://www.oracle.com/java/technologies/downloads/) o [Eclipse Temurin](https://adoptium.net/)
- Node.js v20 o superior con npm — [nodejs.org](https://nodejs.org/)
- PostgreSQL o conexion a Neon DB remota
- Maven (incluido con `./mvnw` dentro del proyecto)
- Angular CLI (`npm install -g @angular/cli`)

---

## Instalacion y Configuracion Local

### 1. Clonar el repositorio

```bash
git clone https://github.com/tuusuario/hambur-besa.git
cd hambur-besa/SistemaDeGestion
```

### 2. Configurar variables de entorno

Crea un archivo `.env` en la raiz del directorio `SistemaDeGestion/` basado en `.env.example`:

```env
DB_URL=jdbc:postgresql://host:5432/neondb?sslmode=require
DB_USERNAME=tu_usuario
DB_PASSWORD=tu_password
JWT_SECRET=tu_clave_secreta_min_32_caracteres
JWT_EXPIRATION=86400
```

> Alternativa rapida: Ejecuta `iniciar.bat` y completa las credenciales cuando se te soliciten. El script genera el archivo `.env` automaticamente.

### 3. Levantar la aplicacion

**Opcion recomendada — un solo comando:**

```bash
# Desde la carpeta SistemaDeGestion/
iniciar.bat
```

Esto abre dos ventanas: una para el backend (puerto 8080) y otra para el frontend (puerto 4200).

**Manualmente:**

```bash
# Terminal 1 — Backend
cd SistemaDeGestion
./mvnw spring-boot:run

# Terminal 2 — Frontend
cd frontend
npm install
npm start
```

La aplicacion estara disponible en:
- Frontend: http://localhost:4200
- Backend API: http://localhost:8080

---

## Estructura del Proyecto

```
hambur-besa/
├── SistemaDeGestion/                    # Backend (Spring Boot)
│   ├── src/main/java/com/example/SistemaDeGestion/
│   │   ├── controllers/                 # Controladores REST (Pedido, Producto, Caja, etc.)
│   │   ├── services/domain/             # Logica de negocio
│   │   ├── models/                      # Entidades JPA (Pedido, Producto, Usuario, Caja...)
│   │   ├── repositories/                # Repositorios JPA + Specs
│   │   ├── dtos/request/                # DTOs de peticion
│   │   ├── dtos/response/               # DTOs de respuesta
│   │   ├── mappers/                     # Mapeo entidad ↔ DTO
│   │   ├── configs/                     # Seguridad JWT, CORS, BaseResponse
│   │   ├── guards/                      # Filtros de autorización
│   │   └── SistemaDeGestionApplication.java
│   ├── src/main/resources/
│   │   ├── application.properties       # Configuración (usa variables .env)
│   │   ├── schema.sql                   # Script de inicialización de BD
│   │   └── .env                         # Variables de entorno (no versionado)
│   ├── .gitignore
│   ├── pom.xml
│   └── iniciar.bat                      # Script para iniciar backend + frontend
├── frontend/                            # Frontend (Angular)
│   ├── src/app/
│   │   ├── pages/                       # Paginas por rol (admin, empleado, usuario)
│   │   ├── components/                  # Componentes reutilizables (sidebar, carrito)
│   │   ├── services/                    # Servicios HTTP (pedidos, auth, recetas, etc.)
│   │   ├── models/                      # Interfaces TypeScript
│   │   ├── guards/                      # AuthGuard, AdminGuard, EmpleadoGuard
│   │   ├── app.routes.ts                # Configuración de rutas
│   │   └── styles.css
│   ├── package.json
│   └── tsconfig.json
├── .env.example                         # Plantilla de variables de entorno
└── README.md
```

---

## Autenticacion y Roles

El sistema cuenta con 3 roles con acceso diferenciado:

| Rol | Acceso |
|---|---|
| ADMIN | Todas las paginas (panel admin, KPI, gestión completa) |
| EMPLEADO | Pedidos, Cocina, Catálogo |
| USER | Mis pedidos, Catálogo |

Los guards de Angular (`authGuard`, `adminGuard`, `empleadoGuard`) protegen cada ruta. El backend utiliza autenticacion JWT con tokens generados al iniciar sesión.

---

## Endpoints Principales

Todos los endpoints estan en `http://localhost:8080`

### Pedidos
| Metodo | Ruta | Descripcion | Rol |
|---|---|---|---|
| GET | `/pedido` | Listar todos los pedidos | EMPLEADO / ADMIN |
| GET | `/pedido/mis-pedidos` | Mis pedidos | USER |
| POST | `/pedido` | Crear pedido | USER |
| PUT | `/pedido/{id}/estado` | Cambiar estado del pedido | EMPLEADO |

### Productos
| Metodo | Ruta | Descripcion | Rol |
|---|---|---|---|
| GET | `/Producto` | Listar productos activos | Todos |
| POST | `/Producto` | Crear producto | ADMIN |
| PUT | `/Producto/{id}` | Actualizar producto | ADMIN |

### Cajas
| Metodo | Ruta | Descripcion | Rol |
|---|---|---|---|
| GET | `/caja` | Listar cajas | ADMIN |
| POST | `/caja` | Crear caja | ADMIN |
| PUT | `/caja/{id}` | Actualizar caja | ADMIN |

### Proveedores
| Metodo | Ruta | Descripcion | Rol |
|---|---|---|---|
| GET | `/proveedor` | Listar proveedores | ADMIN |
| POST | `/proveedor` | Crear proveedor | ADMIN |

### KPI Dashboard
| Metodo | Ruta | Descripcion | Rol |
|---|---|---|---|
| GET | `/kpi` | Dashboard con métricas de ventas, marketing, RRHH y stock | ADMIN |

### Documentacion de la API
El backend genera documentacion Swagger UI disponible en `http://localhost:8080/swagger-ui.html` una vez iniciada la aplicacion. Para habilitarla, incluir la dependencia `springdoc-openapi-starter-webmvc-ui` en el `pom.xml`.

---

## Pruebas y Testing

### Backend
```bash
# Ejecutar todos los tests unitarios
./mvnw test

# Compilar sin tests
./mvnw compile -DskipTests

# Build limpio
./mvnw clean install
```

### Frontend
```bash
# Ejecutar tests unitarios con Vitest
npm run test

# Build de producción
npm run build

# Servidor de desarrollo
npm start
```

---

## Comandos Utiles

### Backend
```bash
./mvnw spring-boot:run          # Iniciar backend
./mvnw clean compile             # Compilar
./mvnw test                     # Tests unitarios
./mvnw clean install            # Build completo con tests
```

### Frontend
```bash
npm install                     # Instalar dependencias
npm start                       # Servidor de desarrollo (localhost:4200)
npm run build                   # Build de producción
npm run test                    # Tests unitarios con Vitest
npx ng lint                     # Linting del código
npx ng build --configuration production  # Build optimizado
```

### Script completo (Backend + Frontend)
```bash
cd SistemaDeGestion && ./iniciar.bat   # Backend y Frontend simultaneamente
```

### Formato de codigo
```bash
# Backend - verificar formato con Spring Boot
./mvnw verify

# Frontend - formatear código
npx prettier --write src/
```

---

## Features del Sistema

- Autenticacion JWT con 3 roles (ADMIN, EMPLEADO, USER)
- Gestion de pedidos con estados: Pendiente → En Preparación → Entregado / Cancelado
- Panel de Cocina con auto-refresh cada 10 segundos para monitoreo en TV/monitor
- Control de Caja con arqueo y movimiento de dinero
- Catálogo de Productos con stock, categorías y proveedores
- Gestion de Proveedores vinculados a productos
- Dashboard KPI con gráficos Chart.js (ventas, marketing, RRHH, stock)
- Recetas con insumos por producto
- Panel Admin con sidebar organizado por categorías de negocio
- Diseño responsivo para escritorio y TV/monitor de cocina

---

## Autores

Desarrollo del proyecto — Sistema de Gestion Gastronomica "Hambur-Besa"

- **Augusto Baricco** — Desarrollo Full Stack
- **Nazareno Peirone** — Desarrollo Full Stack
- **Matias Medina** — Desarrollo Full Stack

---

## Licencia

Este proyecto es para uso academico y de desarrollo.
