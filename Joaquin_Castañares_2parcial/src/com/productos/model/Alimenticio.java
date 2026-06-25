package com.productos.model;

import java.time.LocalDate;

public class Alimenticio extends Producto {

    private LocalDate fechaVencimiento;

    public Alimenticio(String codigo, String marca, String modelo,
                       double precio, int stock, Proveedor proveedor, LocalDate fechaVencimiento) {
        super(codigo, marca, modelo, precio, stock, proveedor);
        this.fechaVencimiento = fechaVencimiento;
    }

    public LocalDate getFechaVencimiento()                  { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fecha)        { this.fechaVencimiento = fecha; }

    @Override
    public String getTipo() { return "Alimenticio"; }

    @Override
    public String toCSV() {
        return String.format("Alimenticio,%s,%s,%s,%.2f,%d,%d,%s",
                getCodigo(), getMarca(), getModelo(),
                getPrecio(), getStock(),
                getProveedor().getId(), fechaVencimiento.toString());
    }
}