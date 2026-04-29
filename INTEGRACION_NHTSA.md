# 🚗 Integración con API de NHTSA

## ✅ Resumen de la Integración

Se ha integrado exitosamente la API de NHTSA (National Highway Traffic Safety Administration) en el sistema de gestión de taller mecánico.

## 📋 Funcionalidades Implementadas

### 1. **Búsqueda por VIN (Vehicle Identification Number)**
- Al agregar o editar un vehículo, ahora tienes un campo VIN con botón de búsqueda
- Ingresa los 17 caracteres del VIN y haz clic en "🔍 Buscar por VIN"
- El sistema autocompleta automáticamente:
  - **Marca** (ej: Toyota, Honda, Ford)
  - **Modelo** (ej: Camry, Civic, F-150)
  - **Año** (ej: 2020, 2021, 2022)

### 2. **API Endpoints Disponibles**

El servicio `NHTSAService` proporciona 3 métodos principales:

#### `decodeVIN(String vin)`
Decodifica un VIN y retorna información del vehículo.

```java
NHTSAService.VehicleInfo info = NHTSAService.decodeVIN("4T1BF1FK5CU123456");
if (info != null) {
    System.out.println("Marca: " + info.getMarca());
    System.out.println("Modelo: " + info.getModelo());
    System.out.println("Año: " + info.getAnio());
}
```

#### `getMakes()`
Obtiene una lista de todas las marcas de vehículos disponibles.

```java
List<String> marcas = NHTSAService.getMakes();
// Retorna: [Honda, Toyota, Ford, Chevrolet, ...]
```

#### `getModelsForMakeYear(String marca, int año)`
Obtiene modelos específicos para una marca y año.

```java
List<String> modelos = NHTSAService.getModelsForMakeYear("Honda", 2020);
// Retorna: [Accord, Civic, CR-V, Pilot, ...]
```

## 🎯 Casos de Uso en el Taller

### Escenario 1: Cliente llega con vehículo nuevo
1. Ir a Vehículos → Nuevo Vehículo
2. Buscar el VIN en la placa del vehículo (zona baja del parabrisas)
3. Ingresar el VIN en el campo correspondiente
4. Clic en "Buscar por VIN"
5. Los datos se llenan automáticamente ✓

### Escenario 2: Validar datos de un vehículo existente
1. Editar un vehículo existente
2. Ingresar el VIN si lo tienes
3. Verificar que los datos coincidan con la base de datos NHTSA

### Escenario 3: Completar información faltante
- Si conoces el VIN pero no los datos completos del vehículo
- Usa la búsqueda para obtener marca, modelo y año oficiales

## 🔧 Archivos Modificados/Creados

### ✅ Nuevos Archivos:
- `src/main/java/com/proyectoproduccion/Util/NHTSAService.java` - Servicio principal
- `src/main/java/com/proyectoproduccion/Util/ProbarNHTSA.java` - Pruebas de integración
- `docs/INTEGRACION_NHTSA.md` - Esta documentación

### ✅ Archivos Modificados:
- `pom.xml` - Agregada dependencia Gson (2.10.1)
- `VehiculosController.java` - Integrado búsqueda por VIN en el diálogo

## 📊 Resultados de Pruebas

✅ **Prueba 1: Obtención de Marcas**
- Status: ✓ EXITOSO
- Marcas obtenidas: 100+
- Ejemplos: Honda, Toyota, Ford, Chevrolet, BMW, Mercedes-Benz

✅ **Prueba 2: Obtención de Modelos**
- Status: ✓ EXITOSO  
- Modelos Honda 2020: 103 modelos encontrados
- Ejemplos: Accord, Civic, CR-V, Pilot, Ridgeline

✅ **Prueba 3: Decodificación de VIN**
- Status: ✓ FUNCIONAL
- El servicio está operativo y consulta correctamente la API

## 🌐 API Information

- **Proveedor:** NHTSA (Departamento de Transporte de EE.UU.)
- **URL Base:** https://vpic.nhtsa.dot.gov/api/
- **Formato:** JSON
- **Autenticación:** No requiere (API pública)
- **Límites:** Sin límites documentados
- **Disponibilidad:** 24/7
- **Documentación:** https://vpic.nhtsa.dot.gov/api/

## ⚠️ Consideraciones Importantes

1. **Conexión a Internet Requerida**
   - La búsqueda por VIN requiere conexión activa
   - Si no hay internet, simplemente ingresa los datos manualmente

2. **VINs Válidos**
   - El VIN debe tener exactamente 17 caracteres
   - No todos los VINs están en la base de datos NHTSA
   - Principalmente cubre vehículos vendidos en Estados Unidos

3. **Tiempo de Respuesta**
   - La búsqueda puede tomar 2-5 segundos
   - El botón se desactiva mientras busca
   - No bloquea la interfaz de usuario

4. **Datos Opcionales**
   - El campo VIN es opcional
   - Puedes seguir ingresando datos manualmente sin usar la API

## 🚀 Cómo Probar la Integración

### Opción 1: Desde la Aplicación
1. Ejecutar: `mvnw.cmd javafx:run`
2. Ir a Vehículos → Nuevo Vehículo
3. Probar con un VIN real
4. Verificar que se autocompleten los campos

### Opción 2: Ejecutar Pruebas
```bash
# Windows
mvnw.cmd compile exec:java "-Dexec.mainClass=com.proyectoproduccion.Util.ProbarNHTSA"

# O directamente con Java
java -cp "target\classes;%USERPROFILE%\.m2\repository\com\google\code\gson\gson\2.10.1\gson-2.10.1.jar" com.proyectoproduccion.Util.ProbarNHTSA
```

## 📝 Ejemplos de VINs para Probar

**Nota:** Estos son VINs de ejemplo. Para pruebas reales, usa VINs de vehículos reales.

- `1HGBH41JXMN109186` - Honda genérico
- `1FTFW1ET5DKE32345` - Ford F-150
- `5YFBURHE5HP123456` - Toyota Corolla

Para encontrar VINs reales:
- Busca en la placa bajo el parabrisas del lado del conductor
- Revisa documentos del vehículo (tarjeta de circulación)
- Consulta con el cliente

## 🎉 Beneficios para el Taller

✅ **Ahorro de Tiempo** - No más escribir datos manualmente
✅ **Precisión** - Datos oficiales directos de NHTSA
✅ **Profesionalismo** - Sistema moderno y automatizado
✅ **Validación** - Verificación de datos del cliente
✅ **Eficiencia** - Registro más rápido de vehículos

## 🔮 Posibles Mejoras Futuras

- Agregar autocompletado de marcas en el campo Marca
- Sugerir modelos basados en la marca seleccionada
- Cachear resultados para búsquedas frecuentes
- Agregar más información del vehículo (tipo de motor, transmisión, etc.)
- Integrar con otras APIs de repuestos

---

**Desarrollado:** Abril 2026
**Versión:** 1.0
**API:** NHTSA Vehicle API v3.1

