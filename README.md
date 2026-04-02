# Franchise API

REST API reactiva para gestionar franquicias, sucursales y productos, desarrollada con Spring WebFlux y MongoDB Atlas.

## Tecnologías

- Java 17
- Spring Boot 3.4.1 + Spring WebFlux (programación reactiva)
- MongoDB Atlas (base de datos en la nube)
- Docker + Docker Compose
- Terraform (Infrastructure as Code)
- JUnit 5 + Mockito + StepVerifier (pruebas unitarias)

## Arquitectura

El proyecto sigue los principios de **Clean Architecture**:
```
src/main/java/com/buitrago/franchise_api/
├── domain/
│   ├── model/          # Entidades de dominio
│   └── repository/     # Interfaces de repositorio
├── application/
│   └── usecase/        # Casos de uso (lógica de negocio)
└── infrastructure/
    ├── persistence/    # Implementación MongoDB
    │   ├── document/   # Documentos y mapper
    │   └── repository/ # Repositorios reactivos
    └── web/            # Capa HTTP
        ├── handler/    # Manejadores de peticiones
        ├── router/     # Definición de rutas
        └── dto/        # Objetos de transferencia
```

## Requisitos previos

- Java 17
- Maven 3.x
- Docker Desktop
- Cuenta en MongoDB Atlas (gratuita)

> **Nota:** La aplicación usa `spring-dotenv` para leer automáticamente las variables del archivo `.env`. No es necesaria ninguna configuración adicional.

## Configuración

1. Clona el repositorio:
```bash
git clone https://github.com/yuliambuitrago-lgtm/franchise-api.git
cd franchise-api
```

2. Crea el archivo `.env` en la raíz del proyecto basándote en `.env.example`:

**Windows:**
```cmd
copy .env.example .env
```
**Mac/Linux:**
```bash
cp .env.example .env
```

3. Edita el `.env` con tu URI de MongoDB Atlas:
```
MONGODB_URI=mongodb+srv://<usuario>:<password>@<cluster>.mongodb.net/franchisedb?retryWrites=true&w=majority
MONGODB_DATABASE=franchisedb
```

## Ejecución local

### Con Maven
```bash
mvn spring-boot:run
```

### Con Docker
```bash
docker-compose up --build
```

La API estará disponible en `http://localhost:8080`

## Endpoints

### Franquicias
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/franchises` | Crear franquicia |
| PATCH | `/api/franchises/{franchiseId}/name` | Actualizar nombre |

### Sucursales
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/franchises/{franchiseId}/branches` | Agregar sucursal |
| PATCH | `/api/franchises/{franchiseId}/branches/{branchId}/name` | Actualizar nombre |

### Productos
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/franchises/{franchiseId}/branches/{branchId}/products` | Agregar producto |
| DELETE | `/api/franchises/{franchiseId}/branches/{branchId}/products/{productId}` | Eliminar producto |
| PATCH | `/api/franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock` | Modificar stock |
| PATCH | `/api/franchises/{franchiseId}/branches/{branchId}/products/{productId}/name` | Actualizar nombre |
| GET | `/api/franchises/{franchiseId}/top-stock` | Producto con más stock por sucursal |

## Ejemplos de uso

### Crear franquicia
```bash
curl -X POST http://localhost:8080/api/franchises \
  -H "Content-Type: application/json" \
  -d '{"name": "Mi Franquicia"}'
```

### Agregar sucursal
```bash
curl -X POST http://localhost:8080/api/franchises/{franchiseId}/branches \
  -H "Content-Type: application/json" \
  -d '{"name": "Sucursal Norte"}'
```

### Agregar producto
```bash
curl -X POST http://localhost:8080/api/franchises/{franchiseId}/branches/{branchId}/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Producto A", "stock": 100}'
```

### Producto con más stock por sucursal
```bash
curl http://localhost:8080/api/franchises/{franchiseId}/top-stock
```

## Pruebas unitarias
```bash
mvn test
```

Cobertura: 13 tests sobre los casos de uso de franquicias, sucursales y productos.

## Infraestructura como código (Terraform)

La infraestructura de MongoDB Atlas está definida como código en la carpeta `terraform/`.

### Requisitos
- Terraform instalado
- API Key de MongoDB Atlas
- Organization ID de MongoDB Atlas

### Despliegue de infraestructura

1. Entra a la carpeta terraform:
```bash
cd terraform
```

2. Crea el archivo `terraform.tfvars` basándote en las variables definidas en `variables.tf`:
```hcl
mongodb_atlas_public_key  = "tu-public-key"
mongodb_atlas_private_key = "tu-private-key"
org_id                    = "tu-org-id"
project_name              = "franchise-project"
db_username               = "franchiseuser"
db_password               = "tu-password"
```

3. Inicializa y aplica:
```bash
terraform init
terraform plan
terraform apply
```

Terraform creará automáticamente:
- Proyecto en MongoDB Atlas
- Cluster M0 (gratuito) en AWS
- Usuario de base de datos
- Reglas de acceso IP

## Variables de entorno

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `MONGODB_URI` | URI de conexión a MongoDB Atlas | `mongodb+srv://user:pass@cluster.mongodb.net/db` |
| `MONGODB_DATABASE` | Nombre de la base de datos | `franchisedb` |