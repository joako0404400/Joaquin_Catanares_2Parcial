package com.productos.repository;

import com.productos.model.Proveedor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ProveedorRepository {

    private static final String FILE_NAME = "proveedores.json";

    public List<Proveedor> cargar() {
        List<Proveedor> lista = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(FILE_NAME)) {
            JSONArray array = (JSONArray) parser.parse(reader);
            for (Object obj : array) {
                JSONObject json = (JSONObject) obj;
                int id = ((Long) json.get("id")).intValue();
                String razonSocial = (String) json.get("razonSocial");
                String telefono    = (String) json.get("telefono");
                String email       = (String) json.get("email");
                String ciudad      = (String) json.get("ciudad");
                lista.add(new Proveedor(id, razonSocial, telefono, email, ciudad));
            }
        } catch (Exception e) {
            System.err.println("Error al cargar proveedores: " + e.getMessage());
        }
        return lista;
    }
}