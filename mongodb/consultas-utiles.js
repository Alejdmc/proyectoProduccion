// ========================================
// CONSULTAS ÚTILES MONGODB
// Sistema de Gestión de Taller Mecánico
// ========================================

// Conectarse a la base de datos
use taller_db

// ========================================
// 1. CONSULTAS BÁSICAS
// ========================================

// Contar documentos en cada colección
db.clientes.countDocuments()
db.vehiculos.countDocuments()
db.ordenes.countDocuments()

// Ver todos los clientes
db.clientes.find().pretty()

// Ver todos los vehículos
db.vehiculos.find().pretty()

// Ver todas las órdenes
db.ordenes.find().pretty()

// ========================================
// 2. CONSULTAS DE BÚSQUEDA
// ========================================

// Buscar cliente por ID
db.clientes.findOne({ _id: 1 })

// Buscar cliente por nombre (parcial)
db.clientes.find({ nombre: /Carlos/ })

// Buscar cliente por email
db.clientes.findOne({ email: "carlos.rodriguez@email.com" })

// Buscar vehículo por placa
db.vehiculos.findOne({ placa: "ABC123" })

// Buscar vehículos por marca
db.vehiculos.find({ marca: "Toyota" })

// Buscar vehículos de un cliente específico
db.vehiculos.find({ cliente_id: 1 })

// ========================================
// 3. CONSULTAS DE ÓRDENES
// ========================================

// Ver órdenes en proceso
db.ordenes.find({ estado: "EN_PROCESO" })

// Ver órdenes recibidas
db.ordenes.find({ estado: "RECIBIDO" })

// Ver órdenes entregadas
db.ordenes.find({ estado: "ENTREGADO" })

// Buscar órdenes por cliente
db.ordenes.find({ cliente_id: 1 })

// Buscar órdenes por vehículo
db.ordenes.find({ vehiculo_id: 1 })

// Órdenes con total mayor a $500,000
db.ordenes.find({ total: { $gt: 500000 } })

// Órdenes con total menor a $300,000
db.ordenes.find({ total: { $lt: 300000 } })

// Órdenes en un rango de precios
db.ordenes.find({
  total: { $gte: 300000, $lte: 700000 }
})

// ========================================
// 4. CONSULTAS CON JOINS (AGGREGATION)
// ========================================

// Vehículos con información del cliente
db.vehiculos.aggregate([
  {
    $lookup: {
      from: "clientes",
      localField: "cliente_id",
      foreignField: "_id",
      as: "cliente_info"
    }
  },
  { $unwind: "$cliente_info" },
  {
    $project: {
      placa: 1,
      marca: 1,
      modelo: 1,
      anio: 1,
      "cliente_info.nombre": 1,
      "cliente_info.telefono": 1
    }
  }
])

// Órdenes con información completa (cliente + vehículo)
db.ordenes.aggregate([
  {
    $lookup: {
      from: "clientes",
      localField: "cliente_id",
      foreignField: "_id",
      as: "cliente"
    }
  },
  {
    $lookup: {
      from: "vehiculos",
      localField: "vehiculo_id",
      foreignField: "_id",
      as: "vehiculo"
    }
  },
  { $unwind: "$cliente" },
  { $unwind: "$vehiculo" },
  {
    $project: {
      _id: 1,
      estado: 1,
      total: 1,
      fecha_entrada: 1,
      descripcion: 1,
      "cliente.nombre": 1,
      "cliente.telefono": 1,
      "vehiculo.placa": 1,
      "vehiculo.marca": 1,
      "vehiculo.modelo": 1
    }
  }
])

// ========================================
// 5. ESTADÍSTICAS Y REPORTES
// ========================================

// Total facturado por estado
db.ordenes.aggregate([
  {
    $group: {
      _id: "$estado",
      total_facturado: { $sum: "$total" },
      cantidad_ordenes: { $count: {} }
    }
  }
])

// Promedio de facturación por orden
db.ordenes.aggregate([
  {
    $group: {
      _id: null,
      promedio: { $avg: "$total" },
      minimo: { $min: "$total" },
      maximo: { $max: "$total" }
    }
  }
])

// Clientes con más órdenes
db.ordenes.aggregate([
  {
    $group: {
      _id: "$cliente_id",
      total_ordenes: { $count: {} },
      total_gastado: { $sum: "$total" }
    }
  },
  {
    $lookup: {
      from: "clientes",
      localField: "_id",
      foreignField: "_id",
      as: "cliente_info"
    }
  },
  { $unwind: "$cliente_info" },
  {
    $project: {
      nombre: "$cliente_info.nombre",
      total_ordenes: 1,
      total_gastado: 1
    }
  },
  { $sort: { total_ordenes: -1 } }
])

// Vehículos por marca
db.vehiculos.aggregate([
  {
    $group: {
      _id: "$marca",
      cantidad: { $count: {} }
    }
  },
  { $sort: { cantidad: -1 } }
])

// Órdenes por mes
db.ordenes.aggregate([
  {
    $group: {
      _id: {
        $dateToString: {
          format: "%Y-%m",
          date: { $toDate: "$fecha_entrada" }
        }
      },
      total_ordenes: { $count: {} },
      total_facturado: { $sum: "$total" }
    }
  },
  { $sort: { _id: 1 } }
])

// ========================================
// 6. CONSULTAS DE ACTUALIZACIÓN
// ========================================

// Actualizar teléfono de un cliente
db.clientes.updateOne(
  { _id: 1 },
  { $set: { telefono: "+57 300 999 8888" } }
)

// Cambiar estado de una orden
db.ordenes.updateOne(
  { _id: 3 },
  { $set: { estado: "EN_PROCESO" } }
)

// Actualizar múltiples campos de un vehículo
db.vehiculos.updateOne(
  { placa: "ABC123" },
  {
    $set: {
      modelo: "Corolla Cross",
      anio: 2024
    }
  }
)

// ========================================
// 7. CONSULTAS DE ELIMINACIÓN
// ========================================

// Eliminar una orden específica (use con precaución)
// db.ordenes.deleteOne({ _id: 99 })

// Eliminar órdenes antiguas (ejemplo: más de 1 año)
// db.ordenes.deleteMany({
//   fecha_entrada: {
//     $lt: new Date("2025-01-01")
//   }
// })

// ========================================
// 8. ÍNDICES
// ========================================

// Ver índices existentes
db.clientes.getIndexes()
db.vehiculos.getIndexes()
db.ordenes.getIndexes()

// Crear índices para mejorar rendimiento
db.clientes.createIndex({ email: 1 })
db.clientes.createIndex({ nombre: 1 })

db.vehiculos.createIndex({ placa: 1 }, { unique: true })
db.vehiculos.createIndex({ cliente_id: 1 })
db.vehiculos.createIndex({ marca: 1 })

db.ordenes.createIndex({ cliente_id: 1 })
db.ordenes.createIndex({ vehiculo_id: 1 })
db.ordenes.createIndex({ estado: 1 })
db.ordenes.createIndex({ fecha_entrada: -1 })

// Índice compuesto para búsquedas complejas
db.ordenes.createIndex({ cliente_id: 1, estado: 1 })

// ========================================
// 9. BÚSQUEDAS AVANZADAS
// ========================================

// Buscar órdenes de vehículos Toyota
db.ordenes.aggregate([
  {
    $lookup: {
      from: "vehiculos",
      localField: "vehiculo_id",
      foreignField: "_id",
      as: "vehiculo"
    }
  },
  { $unwind: "$vehiculo" },
  {
    $match: {
      "vehiculo.marca": "Toyota"
    }
  }
])

// Buscar clientes que gastaron más de $1,000,000
db.ordenes.aggregate([
  {
    $group: {
      _id: "$cliente_id",
      total_gastado: { $sum: "$total" }
    }
  },
  {
    $match: {
      total_gastado: { $gt: 1000000 }
    }
  },
  {
    $lookup: {
      from: "clientes",
      localField: "_id",
      foreignField: "_id",
      as: "cliente"
    }
  },
  { $unwind: "$cliente" }
])

// Vehículos sin órdenes de servicio
db.vehiculos.aggregate([
  {
    $lookup: {
      from: "ordenes",
      localField: "_id",
      foreignField: "vehiculo_id",
      as: "ordenes"
    }
  },
  {
    $match: {
      ordenes: { $size: 0 }
    }
  }
])

// ========================================
// 10. VALIDACIÓN DE DATOS
// ========================================

// Verificar integridad: vehículos sin cliente
db.vehiculos.find({
  cliente_id: {
    $nin: db.clientes.distinct("_id")
  }
})

// Verificar órdenes sin cliente
db.ordenes.find({
  cliente_id: {
    $nin: db.clientes.distinct("_id")
  }
})

// Verificar órdenes sin vehículo
db.ordenes.find({
  vehiculo_id: {
    $nin: db.vehiculos.distinct("_id")
  }
})

// ========================================
// 11. BACKUP Y RESTORE
// ========================================

// Comando para backup (ejecutar en terminal, no en mongosh)
// mongodump --db=taller_db --out=./backup

// Comando para restore (ejecutar en terminal)
// mongorestore --db=taller_db ./backup/taller_db

// ========================================
// 12. UTILIDADES
// ========================================

// Ver estadísticas de la base de datos
db.stats()

// Ver espacio usado por cada colección
db.clientes.stats()
db.vehiculos.stats()
db.ordenes.stats()

// Listar todas las colecciones
show collections

// Cambiar a otra base de datos
use otra_bd

// Listar todas las bases de datos
show dbs

// ========================================
// FIN DE CONSULTAS
// ========================================

print("✓ Consultas cargadas correctamente")
print("Usa estas consultas como referencia para tu sistema de taller mecánico")

