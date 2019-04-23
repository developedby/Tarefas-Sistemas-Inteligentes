package sistema;

import ambiente.*;
import problema.*;
import comuns.*;
import static comuns.PontosCardeais.*;
import java.util.*;
import arvore.*;

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

    int[] plano;
    double custo;
    static int ct = -1;
           
    public Agente(Model m, int alg) {
        this.model = m;
        prob = new Problema();
 
        custo = 0;
        //crencas do agente a respeito do labirinto
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

        plano = criaPlano(prob, alg);
    }
    
    /**Escolhe qual ação (UMA E SOMENTE UMA) será executada em um ciclo de raciocínio
     * @return 1 enquanto o plano não acabar; -1 quando acabar
     */
    public int deliberar() {
        // Incrementa contador de ações
        ct++;
        
        // Executa a proxima acao do plano
        executarIr(plano[ct]);
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


    public void printaMapa() {
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

    public int[] criaPlano(Problema prob, int alg) {
        // Custo uniforme
        if (alg == 1) { 
            TreeNode arvore_busca = new TreeNode(null);
            boolean alcancou_objetivo = false;
            PriorityQueue<TreeNode> fronteira = new PriorityQueue<TreeNode>(81, (a,b) -> b.gn - a.gn); // Ordena pelo menor custo
            Set<int> ja_explorados = new Set<int>; // Estados ja explorados
            TreeNode no_caminho; // variavel para reconstruir o caminho

            // Inicializa a cabeca da arvore
            arvore_busca.setState(prob.estIni);
            arvore_busca.setGn(0);
            arvore_busca.setAction(null);
            fronteira.add(arvore_busca);

            // Executa o Custo Uniforme
            while (!fronteira.isEmpty() && !alcancou_objetivo) {
                TreeNode explorado = fronteira.poll();
                // Se chegou no objetivo, para
                if (explorado.st == prob.estObj) {
                    alcancou_objetivo = true;
                    no_caminho = explorado;
                }
                else {
                    ja_explorados.add(explorado);
                    int[] acoes = prob.acoesPossiveis(explorado.st);
                    // Para cada estado diferente diretamente alcancavel
                    for (int i=0; i<acoes.size(); i++) {
                        if (acoes[i] == 1) {
                            // Constroi o filho
                            TreeNode filho = explorado.addChild();
                            filho.setState(prob.suc(explorado.st, i));
                            filho.setGn(explorado.getGn() + getCusto(i));
                            filho.setAction(i);

                            // Adiciona na fronteira se for vantajoso
                            // Se nao foi explorado ainda
                            if (!ja_explorados.contains(filho.st)) { 
                                TreeNode elem_ja_na_fronteira = procuraEstadoNaFronteira(fronteira, filho.st);
                                // Se nao ta na fronteira
                                if (elem_ja_na_fronteira == null) { 
                                    fronteira.add(filho);
                                } else {
                                    // Se o novo cara é mais barato
                                    if (filho.gn < elem_ja_na_fronteira.gn) {
                                        fronteira.remove(elem_ja_na_fronteira);
                                        fronteira.add(filho);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            ArrayList<int> plano_lista = new ArrayList<int>()
            // Reconstroi caminho
            while (no_caminho != null) {
                plano_lista.add(0, no_caminho.acao);
                no_caminho = no_caminho.parent;
            }
            return plano_lista.toArray(new int[plano_lista.size()]);
        }

        // A* fazendo uma diagonal e uma reta
        else if (alg == 2) { 
        
        }

        // A* fazendo duas retas
        else if (alg == 3) { 

        }

        // Usuario entrou com lixo
        else {
            System.out.println("Algoritmo escolhido não é válido!");
            System.exit();
        }
    }

    public double getCusto(int acao) {
        if (acao==N || acao==L || acao=O || acao==S) {
            return 1;
        }
        else if (acao==NE || acao==SE || acao==SO || acao==NO) {
            return 1.5;
        }
        else {
            return 0;
        }
    }

    public TreeNode procuraEstadoNaFronteira (PriorityQueue<TreeNode> fronteira, Estado st) {
        Iterator iter = fronteira.iterator();
        while(iter.hasNext()) {
            TreeNode node = iter.next();
            if (node.st == st) {
                return node;
            }
        }
        return null;
    }
}    
