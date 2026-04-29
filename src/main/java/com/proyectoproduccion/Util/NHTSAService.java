package com.proyectoproduccion.Util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para consumir la API de NHTSA (National Highway Traffic Safety Administration)
 * API Documentation: https://vpic.nhtsa.dot.gov/api/
 */
public class NHTSAService {

    private static final String BASE_URL = "https://vpic.nhtsa.dot.gov/api/vehicles";
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    /**
     * Decodifica un VIN (Vehicle Identification Number) y obtiene información del vehículo
     * @param vin VIN del vehículo (17 caracteres)
     * @return VehicleInfo con los datos del vehículo o null si hay error
     */
    public static VehicleInfo decodeVIN(String vin) {
        try {
            if (vin == null || vin.trim().isEmpty()) {
                System.err.println("VIN vacío");
                return null;
            }

            String url = BASE_URL + "/decodevin/" + vin.trim() + "?format=json";
            System.out.println("Consultando API NHTSA: " + url);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return parseVehicleInfo(response.body());
            } else {
                System.err.println("Error en la API: " + response.statusCode());
                return null;
            }

        } catch (Exception e) {
            System.err.println("Error al consultar NHTSA API: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Obtiene una lista de marcas de vehículos
     * @return Lista de nombres de marcas
     */
    public static List<String> getMakes() {
        try {
            String url = BASE_URL + "/GetAllMakes?format=json";
            System.out.println("Consultando marcas: " + url);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return parseMakes(response.body());
            } else {
                System.err.println("Error en la API: " + response.statusCode());
                return new ArrayList<>();
            }

        } catch (Exception e) {
            System.err.println("Error al consultar marcas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene modelos para una marca y año específico
     * @param make Marca del vehículo
     * @param year Año del vehículo
     * @return Lista de nombres de modelos
     */
    public static List<String> getModelsForMakeYear(String make, int year) {
        try {
            if (make == null || make.trim().isEmpty()) {
                return new ArrayList<>();
            }

            String url = BASE_URL + "/GetModelsForMakeYear/make/" + 
                         make.trim().replace(" ", "%20") + "/modelyear/" + year + "?format=json";
            System.out.println("Consultando modelos: " + url);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return parseModels(response.body());
            } else {
                System.err.println("Error en la API: " + response.statusCode());
                return new ArrayList<>();
            }

        } catch (Exception e) {
            System.err.println("Error al consultar modelos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Métodos privados para parsear JSON

    private static VehicleInfo parseVehicleInfo(String jsonResponse) {
        try {
            JsonObject root = gson.fromJson(jsonResponse, JsonObject.class);
            JsonArray results = root.getAsJsonArray("Results");

            if (results == null || results.isEmpty()) {
                return null;
            }

            VehicleInfo info = new VehicleInfo();

            for (int i = 0; i < results.size(); i++) {
                JsonObject item = results.get(i).getAsJsonObject();
                String variable = item.get("Variable").getAsString();
                String value = item.get("Value").getAsString();

                if (value == null || value.isEmpty() || value.equals("null")) {
                    continue;
                }

                switch (variable) {
                    case "Make":
                        info.setMarca(value);
                        break;
                    case "Model":
                        info.setModelo(value);
                        break;
                    case "Model Year":
                        try {
                            info.setAnio(Integer.parseInt(value));
                        } catch (NumberFormatException ignored) {}
                        break;
                    case "Vehicle Type":
                        info.setTipo(value);
                        break;
                    case "Manufacturer Name":
                        info.setFabricante(value);
                        break;
                }
            }

            return info.getMarca() != null ? info : null;

        } catch (Exception e) {
            System.err.println("Error al parsear respuesta: " + e.getMessage());
            return null;
        }
    }

    private static List<String> parseMakes(String jsonResponse) {
        List<String> makes = new ArrayList<>();
        try {
            JsonObject root = gson.fromJson(jsonResponse, JsonObject.class);
            JsonArray results = root.getAsJsonArray("Results");

            if (results != null) {
                for (int i = 0; i < results.size() && i < 100; i++) { // Limitar a 100
                    JsonObject item = results.get(i).getAsJsonObject();
                    String makeName = item.get("Make_Name").getAsString();
                    if (makeName != null && !makeName.isEmpty()) {
                        makes.add(makeName);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al parsear marcas: " + e.getMessage());
        }
        return makes;
    }

    private static List<String> parseModels(String jsonResponse) {
        List<String> models = new ArrayList<>();
        try {
            JsonObject root = gson.fromJson(jsonResponse, JsonObject.class);
            JsonArray results = root.getAsJsonArray("Results");

            if (results != null) {
                for (int i = 0; i < results.size(); i++) {
                    JsonObject item = results.get(i).getAsJsonObject();
                    String modelName = item.get("Model_Name").getAsString();
                    if (modelName != null && !modelName.isEmpty()) {
                        models.add(modelName);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al parsear modelos: " + e.getMessage());
        }
        return models;
    }

    /**
     * Clase interna para almacenar información del vehículo
     */
    public static class VehicleInfo {
        private String marca;
        private String modelo;
        private int anio;
        private String tipo;
        private String fabricante;

        public String getMarca() {
            return marca;
        }

        public void setMarca(String marca) {
            this.marca = marca;
        }

        public String getModelo() {
            return modelo;
        }

        public void setModelo(String modelo) {
            this.modelo = modelo;
        }

        public int getAnio() {
            return anio;
        }

        public void setAnio(int anio) {
            this.anio = anio;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public String getFabricante() {
            return fabricante;
        }

        public void setFabricante(String fabricante) {
            this.fabricante = fabricante;
        }

        @Override
        public String toString() {
            return "VehicleInfo{" +
                    "marca='" + marca + '\'' +
                    ", modelo='" + modelo + '\'' +
                    ", anio=" + anio +
                    ", tipo='" + tipo + '\'' +
                    ", fabricante='" + fabricante + '\'' +
                    '}';
        }
    }
}

