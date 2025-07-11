/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uisil.proyecto_juego_cartas.model;

import com.uisil.proyecto_juego_cartas.model.Carta;

/**
 *
 * @author Thoka
 */
public class CartaNormal extends Carta {
    private String imagenRuta;

    public CartaNormal(int id) {
        super(id);
        this.imagenRuta = "/com/uisil/proyecto_juego_cartas/img/Carta_" + (id + 1) + ".png";
    }

    public String getImagenRuta() {
        return imagenRuta;
    }
}
