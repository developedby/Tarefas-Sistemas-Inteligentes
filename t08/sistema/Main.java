/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistema;

import ambiente.*;
import problema.*;
import java.util.Scanner;

import java.io.*;

/**
 *
 * @author tacla
 */
public class Main {
    public static void main(String args[]) throws FileNotFoundException  {
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

        File frutas_file = new File ("./frutasLabirinto.txt");
        Scanner fruitScanner = new Scanner(frutas_file);

        for (int cenario=0; cenario<100; cenario++) {
            for (int i=0; i<9; i++) {
                for (int j=0; j<9; j++) {
                    if (model.labir.parede[i][j] == 0) {
                        String fruta_line = fruitScanner.nextLine();
                        String[] fruta_line_separated = fruta_line.split(",");
                        for (int k=0; k<6; k++) {
                            model.labir.frutas[i][j][k] = fruta_line_separated[k].toCharArray()[0];
                        }
                    }
                }
            }

            int ini_lin;
            int ini_col;
            do {
                ini_lin = (int)(Math.random()*9);
                ini_col = (int)(Math.random()*9);
            } while (model.labir.parede[ini_lin][ini_col] != 0);
            Estado est_ini = new Estado(ini_lin, ini_col);

            int obj_lin;
            int obj_col;
            do {
                obj_lin = (int)(Math.random()*9);
                obj_col = (int)(Math.random()*9);
            } while (model.labir.parede[obj_lin][obj_col] != 0);
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
