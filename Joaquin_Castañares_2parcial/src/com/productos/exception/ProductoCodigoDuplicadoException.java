package com.productos.exception;

public class ProductoCodigoDuplicadoException extends Exception {

    private final String codigo;

    public ProductoCodigoDuplicadoException(String codigo) {
        super("Error: Ya existe un producto con el código '" + codigo + "' en el sistema.");
        this.codigo = codigo;
    }

    public String getCodigo() { return codigo; }
}