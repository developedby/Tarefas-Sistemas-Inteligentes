package sistema;

import ambiente.*;
import problema.*;
import comuns.*;
import static comuns.PontosCardeais.*;
import java.util.*;

/**
 *
 * @author tacla
 * @author Nicolas Abril
 * @author Lucca Rawlyk
 */
public class Agente implements PontosCardeais {
    /* referência ao ambiente para poder atuar no mesmo*/
    Model model;
    Problema prob; // formulacao do problema
    Estado estAtu; // guarda o estado atual (posição atual do agente)
    /* @todo T2: fazer uma sequencia de acoes a ser executada em deliberar
       e armazena-la no atributo plan[]
    */
    int plan[] = {N,N,N,NE,L,L,L,L,L,L,NE,N};
    double custo;
    static int ct = -1;
           
    public Agente(Model m) {
        this.model = m;
        prob = new Problema();
        //@todo T2: Aqui vc deve preencher a formulacao do problema 
        custo = 0;
        //@todo T2: crencas do agente a respeito do labirinto
        prob.criarLabirinto(9, 9);
        prob.crencaLabir.porParedeVertical(0, 1, 0);
        prob.crencaLabir.porParedeVertical(0, 0, 1);
        prob.crencaLabir.porParedeVertical(5, 8, 1);
        prob.crencaLabir.porParedeVertical(5, 5, 2);
        prob.crencaLabir.porParedeVertical(8, 8, 2);
        prob.crencaLabir.porParedeHorizontal(4, 7, 0);
        prob.crencaLabir.porParedeHorizontal(7, 7, 1);
        prob.crencaLabir.porParedeHorizontal(3, 5, 2);
        prob.crencaLabir.porParedeHorizontal(3, 5, 3);
        prob.crencaLabir.porParedeHorizontal(7, 7, 3);
        prob.crencaLabir.porParedeVertical(6, 7, 4);
        prob.crencaLabir.porParedeVertical(5, 6, 5);
        prob.crencaLabir.porParedeVertical(5, 7, 7);

        //@todo T2: crencas do agente: Estado inicial, objetivo e atual
        prob.defEstIni(8,0);
        prob.defEstObj(2,8);
        estAtu = prob.estIni;

        // utilizar atributos da classe Problema
        
    }
    
    /**Escolhe qual ação (UMA E SOMENTE UMA) será executada em um ciclo de raciocínio
     * @return 1 enquanto o plano não acabar; -1 quando acabar
     */
    public int deliberar() {
        // Incrementa contador de ações
        ct++;
        
        // Executa a proxima acao do plano
        executarIr(plan[ct]);
        System.out.println("--- Ação executada! ---");
        estAtu = sensorPosicao();
        // Incrementa o custo acumulado
        if (Arrays.asList(N,L,S,O).contains(plan[ct])) {
            custo += 1;
        }
        else {
            custo += 1.5;
        }
        
        // Imprime o que foi pedido
        printaMapa();
        System.out.println("Estado atual: " + estAtu.getLin() + ", " + estAtu.getCol());
        System.out.println("Ações possíveis: " + Arrays.toString(prob.acoesPossiveis(estAtu)));
        System.out.println("ct = " + ct + " de " + (plan.length-1));
        System.out.println("Ação escolhida = " + acao[plan[ct]]);
        System.out.println("Custo até o momento: " + custo + "\n");

        // Verifica se terminou
        if (ct >= plan.length-1) {
            // Se terminou, verifica se alcançou o objetivo
            if (prob.testeObjetivo(sensorPosicao())) {
                System.out.println("\n\nO agente alcançou o objetivo!");
            }
            else {
                System.out.println("\n\nO agente não alcançou o objetivo :(");
            }
            return -1;
        }
        else {
            return 1;
        }
    }
    
    /**Funciona como um driver ou um atuador: envia o comando para
     * agente físico ou simulado (no nosso caso, simulado)
     * @param direcao N NE S SE ...
     * @return 1 se ok ou -1 se falha
     */
    public int executarIr(int direcao) {
        model.ir(direcao);
        return 1; 
    }   
    
    // Sensor
    public Estado sensorPosicao() {
        int pos[];
        pos = model.lerPos();
        return new Estado(pos[0], pos[1]);
    }

    public Estado suc(Estado est, int acao) {
        if (prob.acoesPossiveis(est)[acao] == 1) {
            if (acao == N){
                return new Estado(est.getLin()-1, est.getCol());
            }
            else if (acao == NE){
                return new Estado(est.getLin()-1, est.getCol()+1);
            }
            else if (acao == L){
                return new Estado(est.getLin(), est.getCol()+1);
            }
            else if (acao == SE){
                return new Estado(est.getLin()+1, est.getCol()+1);
            }
            else if (acao == S){
                return new Estado(est.getLin()+1, est.getCol());
            }
            else if (acao == SO){
                return new Estado(est.getLin()+1, est.getCol()-1);
            }
            else if (acao == O){
                return new Estado(est.getLin(), est.getCol()-1);
            }
            else if (acao == NO){
                return new Estado(est.getLin()-1, est.getCol()-1);
            }
        }
        return est;
    }

    public void printaMapa()
    {
        System.out.println("Mapa de acordo com a crença do agente:");
        // imprime números das colunas
        System.out.print("   ");
        for (int col = 0; col < prob.crencaLabir.getMaxCol(); col++) {
            System.out.printf(" %2d ", col);
        }
        System.out.print("\n");
        for (int lin = 0; lin < prob.crencaLabir.getMaxLin(); lin++) {
            System.out.print("   ");
            for (int col = 0; col < prob.crencaLabir.getMaxCol(); col++) {
                System.out.print("+---");
            }
            System.out.print("+\n");
            System.out.printf("%2d ", lin);
            for (int col = 0; col < prob.crencaLabir.getMaxCol(); col++) {
                if (prob.crencaLabir.parede[lin][col] == 1) {
                    System.out.print("|XXX");  // desenha parede
                } else if (estAtu.getLin() == lin && estAtu.getCol() == col) {
                    System.out.print("| A ");  // desenha agente
                } else if (prob.estObj.getLin() == lin && prob.estObj.getCol() == col) {
                    System.out.print("| G ");
                } else {
                    System.out.print("|   ");  // posicao vazia
                }
            }
            System.out.print("|");
            if (lin == (prob.crencaLabir.getMaxLin() - 1)) {
                System.out.print("\n   ");
                for (int x = 0; x < prob.crencaLabir.getMaxCol(); x++) {
                    System.out.print("+---");
                }
                System.out.println("+\n");
            }
            System.out.print("\n");
        }
    }
}    

