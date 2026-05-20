// ===============================================
// 📋 CONSULTAS ÚTILES PARA AUDITORÍA - MongoDB
// ===============================================
// Base de datos: taller_db
// Colección: auditoria
// ===============================================

// 1. VER TODOS LOS REGISTROS DE AUDITORÍA
db.auditoria.find().pretty()

// 2. VER ÚLTIMOS 10 REGISTROS
db.auditoria.find().sort({ _id: -1 }).limit(10).pretty()

// 3. VER SOLO CREACIONES (CREATE)
db.auditoria.find({ operacion: "CREATE" }).pretty()

// 4. VER SOLO ACTUALIZACIONES (UPDATE)
db.auditoria.find({ operacion: "UPDATE" }).pretty()

// 5. VER SOLO ELIMINACIONES (DELETE)
db.auditoria.find({ operacion: "DELETE" }).pretty()

// ===============================================
// FILTRAR POR TABLA
// ===============================================

// 6. Ver todas las operaciones en CLIENTES
db.auditoria.find({ tabla: "clientes" }).pretty()

// 7. Ver todas las operaciones en VEHÍCULOS
db.auditoria.find({ tabla: "vehiculos" }).pretty()

// 8. Ver todas las operaciones en ÓRDENES
db.auditoria.find({ tabla: "ordenes" }).pretty()

// ===============================================
// BUSCAR POR REGISTRO ESPECÍFICO
// ===============================================

// 9. Ver historial de un cliente específico (ID = 5)
db.auditoria.find({
    tabla: "clientes",
    registroId: "5"
}).pretty()

// 10. Ver cambios en un vehículo (ID = 8)
db.auditoria.find({
    tabla: "vehiculos",
    registroId: "8"
}).pretty()

// 11. Ver historial de una orden (ID = 12)
db.auditoria.find({
    tabla: "ordenes",
    registroId: "12"
}).pretty()

// ===============================================
// FILTRAR POR FECHA/HORA
// ===============================================

// 12. Ver operaciones del día de hoy (ajusta la fecha)
db.auditoria.find({
    timestamp: { $regex: "^2026-05-20" }
}).pretty()

// 13. Ver operaciones de un mes específico
db.auditoria.find({
    timestamp: { $regex: "^2026-05" }
}).pretty()

// 14. Ver operaciones de una hora específica
db.auditoria.find({
    timestamp: { $regex: "2026-05-20 14:" }
}).pretty()

// ===============================================
// ESTADÍSTICAS Y REPORTES
// ===============================================

// 15. Contar total de operaciones
db.auditoria.countDocuments()

// 16. Contar operaciones por tipo
db.auditoria.aggregate([
    { $group: {
        _id: "$operacion",
        total: { $sum: 1 }
    }}
])

// 17. Contar operaciones por tabla
db.auditoria.aggregate([
    { $group: {
        _id: "$tabla",
        total: { $sum: 1 }
    }}
])

// 18. Ver las 5 tablas con más actividad
db.auditoria.aggregate([
    { $group: {
        _id: "$tabla",
        operaciones: { $sum: 1 }
    }},
    { $sort: { operaciones: -1 } },
    { $limit: 5 }
])

// 19. Ver registros más modificados
db.auditoria.aggregate([
    { $match: { operacion: "UPDATE" } },
    { $group: {
        _id: { tabla: "$tabla", registroId: "$registroId" },
        cambios: { $sum: 1 }
    }},
    { $sort: { cambios: -1 } },
    { $limit: 10 }
])

// ===============================================
// ANÁLISIS DE CAMBIOS (UPDATE)
// ===============================================

// 20. Ver qué clientes fueron modificados
db.auditoria.find({
    tabla: "clientes",
    operacion: "UPDATE"
}).pretty()

// 21. Ver cambios con datos anteriores y nuevos
db.auditoria.find({
    operacion: "UPDATE",
    datosAnteriores: { $exists: true },
    datosNuevos: { $exists: true }
}).pretty()

// 22. Ver cambios en el estado de órdenes
db.auditoria.find({
    tabla: "ordenes",
    operacion: "UPDATE",
    "datosAnteriores.estado": { $exists: true },
    "datosNuevos.estado": { $exists: true }
}).pretty()

// ===============================================
// ANÁLISIS DE ELIMINACIONES
// ===============================================

// 23. Ver todos los registros eliminados
db.auditoria.find({
    operacion: "DELETE"
}).pretty()

// 24. Ver clientes eliminados
db.auditoria.find({
    tabla: "clientes",
    operacion: "DELETE"
}).pretty()

// 25. Recuperar datos de un vehículo eliminado (ID = 10)
db.auditoria.findOne({
    tabla: "vehiculos",
    operacion: "DELETE",
    registroId: "10"
})

// ===============================================
// BÚSQUEDAS AVANZADAS
// ===============================================

// 26. Buscar operaciones que involucren un email específico
db.auditoria.find({
    $or: [
        { "datosAnteriores.email": "ejemplo@email.com" },
        { "datosNuevos.email": "ejemplo@email.com" }
    ]
}).pretty()

// 27. Buscar operaciones que involucren una placa específica
db.auditoria.find({
    $or: [
        { "datosAnteriores.placa": "ABC123" },
        { "datosNuevos.placa": "ABC123" }
    ]
}).pretty()

// 28. Ver órdenes con total mayor a 500,000
db.auditoria.find({
    tabla: "ordenes",
    $or: [
        { "datosAnteriores.total": { $gt: 500000 } },
        { "datosNuevos.total": { $gt: 500000 } }
    ]
}).pretty()

// ===============================================
// EXPORTAR Y LIMPIAR
// ===============================================

// 29. Contar registros por fecha
db.auditoria.aggregate([
    { $project: {
        fecha: { $substr: ["$timestamp", 0, 10] }
    }},
    { $group: {
        _id: "$fecha",
        total: { $sum: 1 }
    }},
    { $sort: { _id: -1 } }
])

// 30. Eliminar auditorías anteriores a una fecha (¡CUIDADO!)
// db.auditoria.deleteMany({
//     timestamp: { $lt: "2026-01-01" }
// })

// ===============================================
// ÍNDICES PARA MEJORAR RENDIMIENTO
// ===============================================

// 31. Crear índices recomendados
db.auditoria.createIndex({ operacion: 1 })
db.auditoria.createIndex({ tabla: 1 })
db.auditoria.createIndex({ registroId: 1 })
db.auditoria.createIndex({ timestamp: -1 })
db.auditoria.createIndex({ usuario: 1 })

// 32. Ver índices existentes
db.auditoria.getIndexes()

// ===============================================
// BACKUP Y RESTORE
// ===============================================

// 33. Comando para exportar auditoría (ejecutar en PowerShell/Terminal)
/*
mongoexport --host 172.30.16.104 `
    --db taller_db `
    --collection auditoria `
    --out auditoria_backup.json
*/

// 34. Comando para importar auditoría (ejecutar en PowerShell/Terminal)
/*
mongoimport --host 172.30.16.104 `
    --db taller_db `
    --collection auditoria `
    --file auditoria_backup.json
*/

// ===============================================
// REPORTES PERSONALIZADOS
// ===============================================

// 35. Reporte diario de actividad
db.auditoria.aggregate([
    { $match: {
        timestamp: { $regex: "^2026-05-20" }
    }},
    { $group: {
        _id: {
            tabla: "$tabla",
            operacion: "$operacion"
        },
        total: { $sum: 1 }
    }},
    { $sort: { "_id.tabla": 1, "_id.operacion": 1 } }
])

// 36. Ver cambios en precios de órdenes
db.auditoria.aggregate([
    { $match: {
        tabla: "ordenes",
        operacion: "UPDATE"
    }},
    { $project: {
        registroId: 1,
        totalAnterior: "$datosAnteriores.total",
        totalNuevo: "$datosNuevos.total",
        diferencia: {
            $subtract: ["$datosNuevos.total", "$datosAnteriores.total"]
        }
    }},
    { $match: { diferencia: { $ne: 0 } } }
])

// 37. Clientes más activos (más operaciones)
db.auditoria.aggregate([
    { $match: { tabla: "clientes" } },
    { $group: {
        _id: "$registroId",
        operaciones: { $sum: 1 },
        ultimoNombre: { $last: "$datosNuevos.nombre" }
    }},
    { $sort: { operaciones: -1 } },
    { $limit: 10 }
])

// 38. Vehículos que cambiaron de dueño
db.auditoria.find({
    tabla: "vehiculos",
    operacion: "UPDATE",
    $expr: {
        $ne: ["$datosAnteriores.clienteId", "$datosNuevos.clienteId"]
    }
}).pretty()

// 39. Órdenes que cambiaron de estado
db.auditoria.find({
    tabla: "ordenes",
    operacion: "UPDATE",
    $expr: {
        $ne: ["$datosAnteriores.estado", "$datosNuevos.estado"]
    }
}).pretty()

// 40. Resumen completo de auditoría
db.auditoria.aggregate([
    { $facet: {
        "totalOperaciones": [
            { $count: "total" }
        ],
        "porTipo": [
            { $group: {
                _id: "$operacion",
                cantidad: { $sum: 1 }
            }}
        ],
        "porTabla": [
            { $group: {
                _id: "$tabla",
                cantidad: { $sum: 1 }
            }}
        ],
        "ultimasOperaciones": [
            { $sort: { _id: -1 } },
            { $limit: 5 },
            { $project: {
                operacion: 1,
                tabla: 1,
                registroId: 1,
                timestamp: 1
            }}
        ]
    }}
])

// ===============================================
// FIN DE CONSULTAS ÚTILES
// ===============================================

