package com.productos.repository;

import com.productos.model.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductoRepository {

    private static final String FILE_NAME = "productos.csv";

    public void guardar(List<Producto> productos) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Producto p : productos) {
                writer.println(p.toCSV());
            }
        }
    }

    public List<Producto> cargar(List<Proveedor> proveedores) throws IOException {
        List<Producto> lista = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return lista;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] d = linea.split(",");
                if (d.length < 7) continue;

                String tipo    = d[0];
                String codigo  = d[1];
                String marca   = d[2];
                String modelo  = d[3];
                double precio  = Double.parseDouble(d[4]);
                int stock      = Integer.parseInt(d[5]);
                int idProv     = Integer.parseInt(d[6]);

                Proveedor prov = proveedores.stream()
                        .filter(p -> p.getId() == idProv)
                        .findFirst().orElse(null);

                switch (tipo) {
                    case "Electronico" -> lista.add(new Electronico(codigo, marca, modelo,
                            precio, stock, prov, Integer.parseInt(d[7])));
                    case "Alimenticio" -> lista.add(new Alimenticio(codigo, marca, modelo,
                            precio, stock, prov, LocalDate.parse(d[7])));
                    default -> {}
                }
            }
        }
        return lista;
    }
}