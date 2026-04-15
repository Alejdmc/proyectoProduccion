package com.proyectoproduccion.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase para diagnosticar problemas de conexión a la base de datos
 */
public class ProbarConexion {

    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("  DIAGNÓSTICO DE CONEXIÓN A BASE DE DATOS");
        System.out.println("===========================================\n");

        String database = "taller_db";
        
        // Probar diferentes combinaciones de host, puerto y credenciales
        String[][] hosts = {
            {"localhost", "3306"},
            {"127.0.0.1", "3306"},
            {"127.0.0.1", "3307"},  // Puerto alternativo común
            {"::1", "3306"},        // IPv6 localhost
        };

        String[][] credenciales = {
            {"root", ""},           // root sin contraseña
            {"root", "root"},       // root con contraseña "root"
            {"root", "admin"},      // root con contraseña "admin"
            {"root", "password"},   // root con contraseña "password"
            {"root", "mysql"},      // root con contraseña "mysql"
        };

        boolean conexionExitosa = false;
        String hostExitoso = "";
        String puertoExitoso = "";
        String usuarioExitoso = "";
        String passwordExitoso = "";

        System.out.println("Probando diferentes combinaciones de host, puerto y credenciales...\n");

        for (String[] hostConfig : hosts) {
            String host = hostConfig[0];
            String puerto = hostConfig[1];
            String url = "jdbc:mysql://" + host + ":" + puerto + "/" + database + "?useSSL=false&serverTimezone=UTC";

            for (String[] cred : credenciales) {
                String usuario = cred[0];
                String password = cred[1];
                
                String displayPassword = password.isEmpty() ? "(vacía)" : "***";
                System.out.print("Probando -> " + host + ":" + puerto + " | " + 
                               "Usuario: '" + usuario + "' | Contraseña: '" + displayPassword + "' ... ");

                try (Connection conn = DriverManager.getConnection(url, usuario, password)) {
                    if (conn != null && !conn.isClosed()) {
                        System.out.println("✓ ÉXITO");
                        hostExitoso = host;
                        puertoExitoso = puerto;
                        usuarioExitoso = usuario;
                        passwordExitoso = password;
                        conexionExitosa = true;
                        break;
                    }
                } catch (SQLException e) {
                    String errorMsg = e.getMessage();
                    if (errorMsg.contains("Communications link failure")) {
                        System.out.println("✗ No hay servidor MySQL en " + host + ":" + puerto);
                    } else if (errorMsg.contains("Access denied")) {
                        System.out.println("✗ Credenciales incorrectas");
                    } else if (errorMsg.contains("Unknown database")) {
                        System.out.println("⚠ MySQL corriendo pero BD 'taller_db' no existe");
                        // Aún así guardamos la configuración que funciona
                        hostExitoso = host;
                        puertoExitoso = puerto;
                        usuarioExitoso = usuario;
                        passwordExitoso = password;
                        conexionExitosa = true;
                        break;
                    } else {
                        System.out.println("✗ Error: " + errorMsg);
                    }
                }
            }
            
            if (conexionExitosa) {
                break;
            }
        }

        System.out.println("\n===========================================");
        
        if (conexionExitosa) {
            System.out.println("  ✓ CONEXIÓN EXITOSA");
            System.out.println("===========================================");
            System.out.println("\nConfiguración correcta:");
            System.out.println("  Host: " + hostExitoso);
            System.out.println("  Puerto: " + puertoExitoso);
            System.out.println("  Usuario: " + usuarioExitoso);
            System.out.println("  Contraseña: " + (passwordExitoso.isEmpty() ? "(vacía)" : passwordExitoso));
            System.out.println("\nURL completa:");
            System.out.println("  jdbc:mysql://" + hostExitoso + ":" + puertoExitoso + "/taller_db");
            System.out.println("\n💡 Usa estos valores en la pantalla de login de la aplicación");
        } else {
            System.out.println("  ✗ NO SE PUDO CONECTAR");
            System.out.println("===========================================");
            System.out.println("\nPosibles causas:");
            System.out.println("1. MySQL no está ejecutándose");
            System.out.println("   → Inicia XAMPP o el servicio MySQL");
            System.out.println("2. MySQL usa un puerto diferente");
            System.out.println("   → Verifica en MySQL Workbench qué puerto usa");
            System.out.println("3. Las credenciales son diferentes");
            System.out.println("   → Anota usuario y contraseña de MySQL Workbench");
            System.out.println("\nPara verificar:");
            System.out.println("- Abre MySQL Workbench");
            System.out.println("- Verifica que puedes conectarte");
            System.out.println("- Click en la tuerca ⚙️ de tu conexión");
            System.out.println("- Anota: Hostname, Port, Username");
        }

        System.out.println("\n===========================================\n");
    }
}


