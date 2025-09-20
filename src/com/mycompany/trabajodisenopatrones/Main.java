
package com.mycompany.trabajodisenopatrones;

import com.mycompany.trabajodisenopatrones.ui.LoginUI;

/**
 * Punto de entrada de la aplicación.
 * Su única responsabilidad es iniciar la interfaz de usuario principal.
 */
public class Main {
    public static void main(String[] args) {
        // Muestra la ventana de Login para iniciar el flujo del programa.
        LoginUI login = new LoginUI();
        login.setVisible(true);
    }
}