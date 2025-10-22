# 🏋️ Sistema de Gestión de Gimnasio

Una aplicación web completa desarrollada para la administración integral de un gimnasio moderno. Permite gestionar usuarios, membresías, equipos, productos, ventas y accesos con una interfaz intuitiva y funcionalidades avanzadas.

## 📋 Tabla de Contenidos

- [Características](#-características)
- [Tecnologías](#️-tecnologías)
- [Arquitectura](#-arquitectura)
- [Instalación](#-instalación)
- [Configuración](#️-configuración)
- [Uso](#-uso)
- [API Endpoints](#-api-endpoints)
- [Base de Datos](#️-base-de-datos)
- [Contribución](#-contribución)
- [Licencia](#-licencia)

## ✨ Características

### 👥 Gestión de Usuarios
- **Roles múltiples**: Administrador, Cliente, Entrenador
- **Autenticación segura** con Spring Security
- **Perfiles personalizables** con foto y datos completos
- **Gestión de contactos de emergencia**

### 🏋️ Gestión de Membresías
- **Tipos de membresía personalizables**
- **Control de duración y beneficios**
- **Seguimiento de estado activo/inactivo**
- **Límites de acceso configurables**

### 🛒 Tienda Online
- **Carrito de compras** funcional
- **Gestión de productos** por categorías
- **Sistema de checkout** completo
- **Historial de compras**

### 📊 Dashboard Administrativo
- **Estadísticas en tiempo real**
- **Reportes de ventas**
- **Gestión de equipos del gimnasio**
- **Control de accesos**

### 🤖 Asesor de IA
- **Integración con Ollama**
- **Recomendaciones personalizadas**
- **Sugerencias de entrenamiento**

### 💳 Sistema de Pagos
- **Múltiples métodos de pago**
- **Seguimiento de estados**
- **Historial detallado**

## 🛠️ Tecnologías

### Backend
- **Java 21** - Lenguaje de programación
- **Spring Boot 3.5** - Framework principal
- **Spring Security** - Autenticación y autorización
- **Spring Data JPA** - Persistencia de datos
- **Spring AI** - Integración con IA
- **Maven** - Gestión de dependencias

### Frontend
- **Thymeleaf** - Motor de plantillas
- **Bootstrap** - Framework CSS
- **HTML5/CSS3** - Estructura y estilos
- **JavaScript** - Interactividad

### Base de Datos
- **MySQL** - Base de datos principal
- **JPA/Hibernate** - ORM

### Herramientas
- **Lombok** - Reducción de código boilerplate
- **Ollama** - Modelo de IA local
- **Maven Wrapper** - Gestión de versiones

## 🏗 Arquitectura

El proyecto sigue una arquitectura en capas:

```
src/main/java/com/isai/gym/app/
├── controllers/          # Controladores REST y MVC
│   ├── admin/           # Endpoints administrativos
│   ├── cliente/         # Endpoints de cliente
│   └── publics/         # Endpoints públicos
├── entities/            # Entidades JPA
├── repository/          # Repositorios de datos
├── services/            # Lógica de negocio
│   └── impl/           # Implementaciones
├── dtos/               # Objetos de transferencia
├── config/             # Configuraciones
├── enums/              # Enumeraciones
└── validation/         # Validadores personalizados
```

## 🚀 Instalación

### Prerrequisitos
- Java 21 o superior
- MySQL 8.0 o superior
- Maven 3.6 o superior
- Ollama (opcional, para IA)

### Pasos de instalación

1. **Clonar el repositorio**
```bash
git clone https://github.com/tu-usuario/demo-proyecto-arquitectura.git
cd demo-proyecto-arquitectura
```

2. **Configurar la base de datos**
```sql
CREATE DATABASE gym_management;
CREATE USER 'gym_user'@'localhost' IDENTIFIED BY 'gym_password';
GRANT ALL PRIVILEGES ON gym_management.* TO 'gym_user'@'localhost';
FLUSH PRIVILEGES;
```

3. **Configurar variables de entorno**
```bash
# Windows
set SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/gym_management
set SPRING_DATASOURCE_USERNAME=gym_user
set SPRING_DATASOURCE_PASSWORD=gym_password

# Linux/Mac
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/gym_management
export SPRING_DATASOURCE_USERNAME=gym_user
export SPRING_DATASOURCE_PASSWORD=gym_password
```

4. **Instalar dependencias y ejecutar**
```bash
./mvnw clean install
./mvnw spring-boot:run
```

5. **Acceder a la aplicación**
- URL: http://localhost:9090
- Usuario admin por defecto: admin/admin123

## ⚙️ Configuración

### Perfiles de aplicación

- **Desarrollo**: `application-dev.properties`
- **Producción**: `application-prod.properties`

### Configuración de IA (Opcional)

Para habilitar el asesor de IA:

1. Instalar Ollama
2. Descargar el modelo llama3:
```bash
ollama pull llama3:latest
```

3. Configurar en `application.properties`:
```properties
spring.ai.ollama.chat.base-url=http://localhost:11434
spring.ai.ollama.chat.options.model=llama3:latest
```

## 📖 Uso

### Para Administradores
1. Acceder con credenciales de admin
2. Gestionar usuarios desde el panel administrativo
3. Configurar tipos de membresía
4. Monitorear estadísticas y ventas
5. Gestionar equipos del gimnasio

### Para Clientes
1. Registrarse en la plataforma
2. Completar perfil personal
3. Adquirir membresías
4. Comprar productos en la tienda
5. Consultar historial de accesos

### Para Entrenadores
1. Acceder con credenciales asignadas
2. Ver clientes asignados
3. Gestionar rutinas y seguimiento

## 🔗 API Endpoints

### Autenticación
- `GET /login` - Página de login
- `POST /login` - Autenticar usuario
- `GET /registro` - Página de registro
- `POST /registro` - Registrar nuevo usuario

### Administración
- `GET /admin/dashboard` - Dashboard administrativo
- `GET /admin/usuarios` - Lista de usuarios
- `GET /admin/membresias` - Gestión de membresías
- `GET /admin/productos` - Gestión de productos

### Cliente
- `GET /cliente/dashboard` - Dashboard del cliente
- `GET /cliente/tienda` - Tienda de productos
- `GET /cliente/carrito` - Carrito de compras
- `GET /cliente/perfil` - Perfil del usuario

## 🗄️ Base de Datos

### Entidades principales
- **Usuario**: Información de usuarios del sistema
- **Membresia**: Tipos de membresía disponibles
- **MembresiaCliente**: Relación usuario-membresía
- **Producto**: Productos de la tienda
- **Venta**: Transacciones realizadas
- **Equipo**: Equipos del gimnasio
- **AccesoGimnasio**: Registro de accesos

### Diagrama ER
```
Usuario ||--o{ MembresiaCliente
Membresia ||--o{ MembresiaCliente
Usuario ||--o{ Venta
Producto ||--o{ ItemVenta
Venta ||--o{ ItemVenta
Usuario ||--o{ AccesoGimnasio
```

## 🤝 Contribución

1. Fork el proyecto
2. Crear una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir un Pull Request

## 👨‍💻 Autor

**Jhonatan Isai**
- GitHub: [@Jhonatanisai8](https://github.com/Jhonatanisai8)

## 🙏 Agradecimientos

- Spring Boot Team por el excelente framework
- Thymeleaf por el motor de plantillas
- Bootstrap por los componentes UI
- Ollama por la integración de IA

---

⭐ Si este proyecto te ha sido útil, ¡no olvides darle una estrella!