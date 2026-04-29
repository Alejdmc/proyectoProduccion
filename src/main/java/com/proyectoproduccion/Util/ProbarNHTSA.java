                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    package com.proyectoproduccion.Util;

/**
 * Clase de prueba para verificar la integración con la API de NHTSA
 */
public class ProbarNHTSA {

    public static void main(String[] args) {
        System.out.println("=== PRUEBA DE INTEGRACIÓN CON API NHTSA ===\n");

        // Prueba 1: Decodificar VIN
        System.out.println("📋 Prueba 1: Decodificar VIN");
        System.out.println("----------------------------");
        
        // VIN de ejemplo (Toyota Camry)
        String vinPrueba = "4T1BF1FK5CU123456";
        System.out.println("Buscando información para VIN: " + vinPrueba);
        
        NHTSAService.VehicleInfo info = NHTSAService.decodeVIN(vinPrueba);
        
        if (info != null && info.getMarca() != null) {
            System.out.println("✓ Información encontrada:");
            System.out.println("  - Marca: " + info.getMarca());
            System.out.println("  - Modelo: " + info.getModelo());
            System.out.println("  - Año: " + info.getAnio());
            System.out.println("  - Tipo: " + info.getTipo());
            System.out.println("  - Fabricante: " + info.getFabricante());
        } else {
            System.out.println("✗ No se encontró información");
        }

        System.out.println("\n📋 Prueba 2: Obtener primeras 10 marcas");
        System.out.println("----------------------------");
        
        var marcas = NHTSAService.getMakes();
        if (!marcas.isEmpty()) {
            System.out.println("✓ Marcas encontradas: " + marcas.size());
            System.out.println("Primeras 10 marcas:");
            for (int i = 0; i < Math.min(10, marcas.size()); i++) {
                System.out.println("  " + (i + 1) + ". " + marcas.get(i));
            }
        } else {
            System.out.println("✗ No se pudieron obtener marcas");
        }

        System.out.println("\n📋 Prueba 3: Obtener modelos de Honda 2020");
        System.out.println("----------------------------");
        
        var modelos = NHTSAService.getModelsForMakeYear("Honda", 2020);
        if (!modelos.isEmpty()) {
            System.out.println("✓ Modelos encontrados: " + modelos.size());
            System.out.println("Primeros 5 modelos:");
            for (int i = 0; i < Math.min(5, modelos.size()); i++) {
                System.out.println("  " + (i + 1) + ". " + modelos.get(i));
            }
        } else {
            System.out.println("✗ No se pudieron obtener modelos");
        }

        System.out.println("\n=== FIN DE PRUEBAS ===");
    }
}

