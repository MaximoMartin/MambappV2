# MambApp V2 📱

## Descripción General

**MambApp V2** es una aplicación móvil desarrollada en **Kotlin** y **Jetpack Compose** para la gestión integral de monitoreos médicos. La aplicación está diseñada para profesionales de la salud que necesitan registrar, gestionar y consultar información detallada sobre procedimientos de monitoreo neurológico.

## 🏗️ Arquitectura del Proyecto

### Patrón de Arquitectura
- **MVVM (Model-View-ViewModel)** con Repository Pattern
- **Clean Architecture** con separación clara de responsabilidades
- **Jetpack Compose** para UI moderna y declarativa
- **Room Database** para persistencia local

### Estructura de Directorios
```
├── data/
│   ├── dao/           # Data Access Objects para Room
│   ├── database/      # Configuración de Room Database
│   ├── entities/      # Entidades de datos (8 entidades principales)
│   └── repository/    # Capa de repositorio para abstracción de datos
├── di/
│   └── AppContainer   # Inyección de dependencias manual
├── navigation/
│   └── NavigationRoutes # Centralización de rutas de navegación
├── ui/
│   ├── components/    # Componentes UI reutilizables
│   ├── screens/       # Pantallas principales de la aplicación
│   ├── state/         # Estados de formularios
│   └── theme/         # Configuración de tema Material3
├── utils/
└── viewmodel/         # ViewModels para cada entidad
```

## 🏥 Funcionalidades Principales

### 1. Gestión de Monitoreos
**Entidad Principal:** `Monitoreo`
- **Registro completo** de procedimientos de monitoreo neurológico
- **Campos principales:**
  - Número de registro único
  - Fechas (realizado, presentado, cobrado)
  - DNI del paciente
  - IDs de profesionales involucrados (médico, técnico, solicitante)
  - Ubicación y patología
  - Detalles clínicos (anestesia, complicaciones, cambios motores)
  - Equipo utilizado (opcional)

### 2. Sistema de Snapshots Inmutables
**Innovación clave:** La aplicación implementa un sistema de "snapshots" que preserva la información histórica:
- `medicoSnapshot`, `tecnicoSnapshot`, `solicitanteSnapshot`
- `lugarSnapshot`, `patologiaSnapshot`, `equipoSnapshot`
- `pacienteNombre`, `pacienteApellido`, `pacienteEdad`, `pacienteMutual`

Esto garantiza que los registros históricos mantengan la información exacta del momento del procedimiento, incluso si los datos maestros cambian posteriormente.

### 3. Gestión de Recursos Médicos
La aplicación maneja **8 entidades principales:**

1. **Paciente** - Datos personales y mutual
2. **Médico** - Profesionales médicos
3. **Técnico** - Personal técnico especializado
4. **Solicitante** - Quien solicita el procedimiento
5. **Lugar** - Ubicaciones donde se realizan los procedimientos
6. **Patología** - Tipos de patologías neurológicas
7. **Equipo** - Equipos médicos utilizados
8. **Monitoreo** - Registro principal del procedimiento

### 4. Interfaz de Usuario Moderna
- **Material Design 3** con soporte para tema claro/oscuro
- **Animaciones fluidas** (gradientes animados en pantalla principal)
- **Navegación intuitiva** con Navigation Compose
- **Formularios dinámicos** con validación en tiempo real
- **Diálogos personalizados** para entrada rápida de datos

## 🚀 Pantallas Principales

### HomeScreen
- Pantalla principal con diseño atractivo
- Gradientes animados adaptativos al tema
- Tres acciones principales: Nuevo Monitoreo, Ver Registros, Gestionar Recursos

### MonitoreoScreen (Formulario)
- **Formulario complejo** dividido en secciones:
  - Fechas (realizado, presentado, cobrado)
  - Información del paciente
  - Selección de profesionales
  - Ubicación y patología
  - Detalles clínicos
  - Selección de equipo
- **Validaciones robustas** de fechas y datos obligatorios
- **Modo creación y edición** con prellenado de datos

### ListScreen
- Lista de todos los monitoreos realizados
- Información resumida con snapshots históricos
- Navegación a detalles y edición

### ResourceMenuScreen
- Centro de gestión para todas las entidades
- Acceso rápido a listas especializadas por tipo de recurso

### Pantallas de Recursos Especializadas
- Una pantalla dedicada para cada entidad
- Funciones CRUD completas
- Interfaz optimizada por tipo de datos

## 🛠️ Stack Tecnológico

### Tecnologías Core
- **Kotlin** - Lenguaje principal
- **Jetpack Compose** - UI moderna y declarativa
- **Material3** - Design system
- **Navigation Compose** - Navegación entre pantallas

### Persistencia
- **Room Database** - Base de datos local
- **SQLite** - Motor de base de datos subyacente
- **Coroutines** - Operaciones asíncronas

### Arquitectura
- **ViewModel** - Gestión de estado UI
- **StateFlow** - Flujos reactivos de datos
- **Repository Pattern** - Abstracción de datos
- **Manual DI** via AppContainer

### Build System
- **Gradle KTS** - Build scripts en Kotlin
- **Version Catalogs** - Gestión centralizada de dependencias
- **Kapt** - Procesamiento de anotaciones para Room

## 🔧 Configuración del Proyecto

### Requisitos Mínimos
- **Android API 24** (Android 7.0)
- **Target SDK 35** (Android 15)
- **Java 17**
- **Kotlin Compiler Extension 1.5.13**

### Dependencias Principales
```kotlin
// Compose BOM - Gestión de versiones
implementation(platform(libs.androidx.compose.bom))

// UI y Compose
implementation(libs.androidx.core.ktx)
implementation(libs.androidx.activity.compose)
implementation(libs.androidx.ui)
implementation(libs.androidx.material3)

// Navegación
implementation(libs.androidx.navigation.compose)

// Base de datos
implementation("androidx.room:room-runtime:2.6.0")
implementation("androidx.room:room-ktx:2.6.0")
kapt("androidx.room:room-compiler:2.6.0")

// Iconos extendidos
implementation("androidx.compose.material:material-icons-extended")
```

## 🗃️ Modelo de Datos

### Relaciones entre Entidades
```
Monitoreo (1:N)
├── Paciente (por DNI)
├── Médico (por ID)
├── Técnico (por ID)
├── Solicitante (por ID)
├── Lugar (por ID)
├── Patología (por ID)
└── Equipo (por ID, opcional)
```

### Características Especiales del Modelo
- **Auto-incremento** en entidades de catálogo
- **DNI como PK** en Paciente
- **Campos opcionales** para flexibilidad
- **Snapshots históricos** para preservar contexto temporal

## 🎯 Funcionalidades Destacadas

### 1. Sistema de Validación Robusto
- Validación de fechas no futuras
- Verificación de existencia de pacientes
- Campos obligatorios claramente definidos

### 2. Estado de Formulario Avanzado
- `MonitoreoFormState` con gestión reactiva
- Soporte para diálogos dinámicos
- Estados individuales para cada campo

### 3. Navegación Tipo-Segura
- Paso de objetos complejos entre pantallas
- SavedStateHandle para persistencia temporal
- Rutas centralizadas y organizadas

### 4. UI Adaptativa
- Soporte completo para modo oscuro
- Animaciones suaves y profesionales
- Componentes reutilizables y modulares

## 🚧 Consideraciones de Desarrollo

### Aspectos a Mejorar para Producción
- **Migración de BD**: Remover `fallbackToDestructiveMigration()`
- **Inyección de Dependencias**: Considerar Hilt/Dagger
- **Testing**: Ampliar cobertura de pruebas
- **Networking**: Integrar sincronización remota
- **Backup**: Implementar respaldo de datos

### Oportunidades de Expansión
- **Reportes y Analytics**
- **Exportación de datos**
- **Sincronización multi-dispositivo**
- **Gestión de usuarios y permisos**

## 👨‍💻 Conclusión Técnica

MambApp V2 es una aplicación **excepcionalmente bien arquitecturada** que demuestra:
- **Dominio sólido** del desarrollo Android moderno
- **Patterns arquitectónicos** correctamente implementados
- **UI/UX profesional** con Jetpack Compose
- **Gestión de datos compleja** con Room y snapshots históricos
- **Escalabilidad** preparada para crecimiento futuro

La aplicación está lista para uso en entornos médicos reales y cuenta con todas las bases necesarias para evolucionar hacia una solución empresarial completa.

---

**Desarrollado con ❤️ usando las mejores prácticas de Android Development** 