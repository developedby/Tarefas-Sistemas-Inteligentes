/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistema;

import ambiente.*;
import java.util.Scanner;

/**
 *
 * @author tacla
 */
public class Main {
    public static void main(String args[]) {
        // Cria o ambiente (modelo) = labirinto com suas paredes
        Model model = new Model(9, 9);
        model.labir.porParedeVertical(0, 1, 0);
        model.labir.porParedeVertical(0, 0, 1);
        model.labir.porParedeVertical(5, 8, 1);
        model.labir.porParedeVertical(5, 5, 2);
        model.labir.porParedeVertical(8, 8, 2);
        model.labir.porParedeHorizontal(4, 7, 0);
        model.labir.porParedeHorizontal(7, 7, 1);
        model.labir.porParedeHorizontal(3, 5, 2);
        model.labir.porParedeHorizontal(3, 5, 3);
        model.labir.porParedeHorizontal(7, 7, 3);
        model.labir.porParedeVertical(6, 7, 4);
        model.labir.porParedeVertical(5, 6, 5);
        model.labir.porParedeVertical(5, 7, 7);

        // Cria um agente
        

        Scanner fruitScanner = new Scanner(new File ("./frutasLabirinto.txt"))

        for (int cenario=0; cenario<100; cenario++) {
            for (int i=0; i<9; i++) {
                for (int j=0; j<0; j++) {
                    if (!model.labir.paredes[i][j]) {
                        String fruta_line = fruitScanner.nextLine();
                        model.labir.frutas[i][j] = fruta_line.split(",");
                    }
                }
            }

            do {
                int ini_lin = (int)(Math.random()*10);
                int ini_col = (int)(Math.random()*10);
            } while (model.labir.parede[ini_lin][ini_col]);
            Estado est_ini = new Estado(ini_lin, ini_col);

            do {
                int obj_lin = (int)(Math.random()*10);
                int obj_col = (int)(Math.random()*10);
            } while (model.labir.parede[obj_lin][obj_col]);
            Estado est_obj = new Estado(obj_lin, obj_col);

            Agente ag1 = new Agente(model, est_ini, est_obj, false);
            while (ag1.deliberar() != -1) {
                ;
            }
            Agente ag2 = new Agente(model, est_ini, est_obj, true);
            while (ag2.deliberar() != -1) {
                ;
            }
        }
    }
}
