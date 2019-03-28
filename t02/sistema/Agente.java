package sistema;

import ambiente.*;
import problemaEnuncT02.*;
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
    int plan[] = {N,N,N,NE,E,E,E,E,E,E,NE,N};
    double custo;
    static int ct = -1;
           
    public Agente(Model m) {
        this.model = m;
        prob = new Problema();
        //@todo T2: Aqui vc deve preencher a formulacao do problema 
        custo = 0;
        //@todo T2: crencas do agente a respeito do labirinto
        prob.criarLabirinto(9, 9);
        prob.labir.porParedeVertical(0, 1, 0);
        prob.labir.porParedeVertical(0, 0, 1);
        prob.labir.porParedeVertical(5, 8, 1);
        prob.labir.porParedeVertical(5, 5, 2);
        prob.labir.porParedeVertical(8, 8, 2);
        prob.labir.porParedeHorizontal(4, 7, 0);
        prob.labir.porParedeHorizontal(7, 7, 1);
        prob.labir.porParedeHorizontal(3, 5, 2);
        prob.labir.porParedeHorizontal(3, 5, 3);
        prob.labir.porParedeHorizontal(7, 7, 3);
        prob.labir.porParedeVertical(6, 7, 4);
        prob.labir.porParedeVertical(5, 6, 5);
        prob.labir.porParedeVertical(5, 7, 7);

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
        executarIr(plano[ct]);

        // Incrementa o custo acumulado
        if (Arrays.asList(N,L,S,O).contains(plano[ct])) {
            custo += 1;
        }
        else {
            custo += 1.5;
        }
        
        // Imprime o que foi pedido
        

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
        else {
            return est;
        }
    }
}    

