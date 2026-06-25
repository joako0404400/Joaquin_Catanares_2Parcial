package com.productos.model;

public class Electronico extends Producto {

    private int garantiaMeses;

    public Electronico(String codigo, String marca, String modelo,
                       double precio, int stock, Proveedor proveedor, int garantiaMeses) {
        super(codigo, marca, modelo, precio, stock, proveedor);
        this.garantiaMeses = garantiaMeses;
    }

    public int getGarantiaMeses()               { return garantiaMeses; }
    public void setGarantiaMeses(int garantia)  { this.garantiaMeses = garantia; }

    @Override
    public String getTipo() { return "Electronico"; }

    @Override
    public String toCSV() {
        return String.format("Electronico,%s,%s,%s,%.2f,%d,%d,%d",
                getCodigo(), getMarca(), getModelo(),
                getPrecio(), getStock(),
                getProveedor().getId(), garantiaMeses);
    }
}