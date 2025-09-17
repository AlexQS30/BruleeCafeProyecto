/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.trabajodisenopatrones.model;
 
// DTO (Data Transfer Object) para el reporte de ventas por vendedor.
public class ModeloVentaVendedorDTO {
    private String vendedor;
    private double totalVendido;

    public ModeloVentaVendedorDTO(String vendedor, double totalVendido) {
        this.vendedor = vendedor;
        this.totalVendido = totalVendido;
    }

    // Getters
    public String getVendedor() { return vendedor; }
    public double getTotalVendido() { return totalVendido; }
}