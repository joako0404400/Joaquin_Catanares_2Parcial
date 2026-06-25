package com.productos.controller;

import com.productos.exception.ProductoCodigoDuplicadoException;
import com.productos.model.Producto;
import com.productos.model.Proveedor;
import com.productos.repository.ProductoRepository;
import com.productos.repository.ProveedorRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoController {

    private final List<Producto> listaProductos;
    private final List<Proveedor> listaProveedores;
    private final ProductoRepository productoRepo;

    public ProductoController() {
        this.productoRepo     = new ProductoRepository();
        this.listaProveedores = new ProveedorRepository().cargar();
        List<Producto> aux;
        try {
            aux = productoRepo.cargar(listaProveedores);
        } catch (IOException e) {
            aux = new ArrayList<>();
        }
        this.listaProductos = aux;
    }

    public void agregar(Producto p) throws ProductoCodigoDuplicadoException, IOException {
        for (Producto prod : listaProductos) {
            if (prod.getCodigo().equalsIgnoreCase(p.getCodigo())) {
                throw new ProductoCodigoDuplicadoException(p.getCodigo());
            }
        }
        listaProductos.add(p);
        productoRepo.guardar(listaProductos);
    }

    public void modificar(String codigo, String marca, String modelo,
                          double precio, int stock, Proveedor proveedor,
                          String atributoExtra) throws IOException {
        for (Producto p : listaProductos) {
            if (p.getCodigo().equalsIgnoreCase(codigo)) {
                p.setMarca(marca);
                p.setModelo(modelo);
                p.setPrecio(precio);
                p.setStock(stock);
                p.setProveedor(proveedor);
                break;
            }
        }
        productoRepo.guardar(listaProductos);
    }

    public void eliminar(String codigo) throws IOException {
        listaProductos.removeIf(p -> p.getCodigo().equalsIgnoreCase(codigo));
        productoRepo.guardar(listaProductos);
    }

    public void exportarCaros(String ruta) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ruta))) {
            for (Producto p : listaProductos) {
                if (p.getPrecio() > 500000) {
                    writer.println(p.toString());
                }
            }
        }
    }

    public double calcularTotalInventario() {
        return listaProductos.stream()
                .mapToDouble(p -> p.getPrecio() * p.getStock())
                .sum();
    }

    public List<Producto>  getListaProductos()   { return listaProductos; }
    public List<Proveedor> getListaProveedores()  { return listaProveedores; }
}