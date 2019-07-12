package sistema;

import ambiente.*;
import problema.*;
import comuns.*;
import static comuns.PontosCardeais.*;
import java.util.*;
import arvore.*;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.rule.Variable;

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
    float custo;
    static int ct = -1;
    boolean fuzzy;
    int energia_restante = 3;
    int pontuacao;
    FIS fis;

    public Agente(Model m, Estado estIni, Estado estObj, boolean fuzzy) {
        this.model = m;
        this.fuzzy = fuzzy;
        
        if (fuzzy) {
            this.fis = FIS.load("./comeFruta.fcl",true);
        }

        this.prob = new Problema();
        this.custo = 0;
        //crencas do agente a respeito do labirinto
        prob.criarLabirinto(9, 9);
        prob.crencaLabir = this.model.labir;

        //@todo T2: crencas do agente: Estado inicial, objetivo e atual
        prob.estIni = estIni;
        prob.estObj = estObj;
        this.estAtu = prob.estIni;

        this.plano = criaPlano(prob);
    }

    /**Escolhe qual ação (UMA E SOMENTE UMA) será executada em um ciclo de raciocínio
     * @return 1 enquanto o plano não acabar; -1 quando acabar
     */
    public int deliberar() {
        // Incrementa contador de ações
        ct++;

        //this.model.desenhar();

        // Executa a proxima acao do plano
        executarIr(plano[this.ct]);
        energia_restante -= 1.5;

        if(this.fuzzy) {
            fis.setVariable("distancia", plano.length - ct);
            fis.setVariable("energia_restante", energia_restante);
            char[] fruta_atual = prob.crencaLabir.frutas[estAtu.getLin()][estAtu.getLin()];
            fis.setVariable("energia_fruta", classificaFruta(fruta_atual));
            fis.evaluate();
            // AQUI !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            double fome = fis.getVariable("fome"); // Acho que nao é asism
            // ACIMA AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
            if Math.random() < fome) {
                comeFruta(this.estAtu)
            }
        } else {
            if (Math.random() >= 0.5) {
                comeFruta(this.estAtu);
            }
        }

        System.out.println("--- Ação executada! ---");
        estAtu = sensorPosicao();
        // Incrementa o custo acumulado
        if (Arrays.asList(N,L,S,O).contains(plano[ct])) {
            custo += 1;
        }
        else {
            custo += 1.5;
        }

        // Imprime o que foi pedido
        // printaMapa();
        System.out.println("Estado atual: " + estAtu.getLin() + ", " + estAtu.getCol());
        System.out.println("Ações possíveis: " + Arrays.toString(prob.acoesPossiveis(estAtu)));
        System.out.println("ct = " + ct + " de " + (plano.length-1));
        System.out.println("Ação executada = " + acao[plano[ct]]);
        System.out.println("Custo até o momento: " + custo + "\n");

        // Verifica se morreu
        if (this.energia_restante <= 0) {
            this.pontuacao = 50;
            return -1;
        }

        // Verifica se chegou no fim
        if (ct >= plano.length-1) {
            // Se terminou, verifica se alcançou o objetivo
            if (prob.testeObjetivo(sensorPosicao())) {
                System.out.println("\n\nO agente alcançou o objetivo!");
            }
            else {
                System.out.println("\n\nO agente não alcançou o objetivo :(");
            }
            this.pontuacao = this.energia_restante;
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

    public int[] criaPlano(Problema prob) {
        TreeNode arvore_busca = new TreeNode(null);
        boolean alcancou_objetivo = false;
        fnComparator comparador_nos = new fnComparator();
        PriorityQueue<TreeNode> fronteira = new PriorityQueue<TreeNode>(81, comparador_nos);
        Set<Estado> ja_explorados = new HashSet<Estado>(); // Estados ja explorados
        TreeNode no_caminho = null; // variavel para reconstruir o caminho

        int ct_nos = 0;
        int ct_ja_explorados = 0;
        int ct_descartados_front = 0;

        // Inicializa a cabeca da arvore
        arvore_busca.setState(prob.estIni);
        arvore_busca.setGn(0);
        arvore_busca.setHn(calculaHeuristica(arvore_busca.getState(), prob.estObj));
        arvore_busca.setAction(-1);
        fronteira.add(arvore_busca);
        ct_nos++;

        // Executa a busca
        while (!fronteira.isEmpty() && !alcancou_objetivo) {
            TreeNode explorado = fronteira.poll();
            ja_explorados.add(explorado.getState());
            //System.out.printf("Estado tirado da fronteira: (%d, %d)\n", explorado.getState().getLin(), explorado.getState().getCol());
            // Se chegou no objetivo, para
            if (explorado.getState().igualAo(prob.estObj)) {
                alcancou_objetivo = true;
                no_caminho = explorado;
            }
            else {
                int[] acoes = prob.acoesPossiveis(explorado.getState());
                // Para cada estado diferente diretamente alcancavel
                for (int i=0; i<acoes.length; i++) {
                    if (acoes[i] != -1) {
                        // Constroi o filho
                        TreeNode filho = explorado.addChild();
                        ct_nos++;
                        filho.setState(prob.suc(explorado.getState(), i));
                        filho.setGn(explorado.getGn() + custoAcao(i));
                        filho.setHn(calculaHeuristica(filho.getState(), prob.estObj));
                        filho.setAction(i);

                        // Adiciona na fronteira se for vantajoso
                        // Se nao foi explorado ainda
                        if (buscaEstado(filho.getState(), ja_explorados) == null) {
                            //System.out.printf("Estado ainda nao explorado: (%d, %d)\n", filho.getState().getLin(), filho.getState().getCol());
                            TreeNode elem_ja_na_fronteira = buscaTreeNode(filho.getState(), fronteira);
                            // Se nao ta na fronteira
                            if (elem_ja_na_fronteira == null) {
                                fronteira.add(filho);
                                //System.out.printf("Estado novo encontrado: (%d, %d)\n", filho.getState().getLin(), filho.getState().getCol());
                            }
                            // Se o novo cara é mais barato
                            else {
                                ct_descartados_front++;
                                if (filho.getFn() < elem_ja_na_fronteira.getFn()) {
                                    fronteira.remove(elem_ja_na_fronteira);
                                    fronteira.add(filho);
                                    //System.out.printf("Estado substituido na fronteira: (%d, %d)\n", filho.getState().getLin(), filho.getState().getCol());
                                }
                            }
                        }
                        else {
                            ct_ja_explorados++;
                        }
                    }
                }
            }
        }

        if (alcancou_objetivo == false) {
            System.out.println("Não conseguiu encontrar o objetivo!");
            System.exit(1);
        }

        System.out.printf("ct_nos: %d\n", ct_nos);
        System.out.printf("ct_ja_explorados: %d\n", ct_ja_explorados);
        System.out.printf("ct_descartados_front: %d\n", ct_descartados_front);

        List<Integer> plano_lista = new ArrayList<Integer>();
        // Reconstroi caminho (ultimo passo no comeco)
        while (no_caminho.getParent() != null) {
            plano_lista.add(no_caminho.getAction());
            no_caminho = no_caminho.getParent();
        }
        Collections.reverse(plano_lista);
        return plano_lista.stream().mapToInt(Integer::intValue).toArray();
    }

    public float custoAcao(int acao) {
        if (acao==N || acao==L || acao==O || acao==S) {
            return 1f;
        }
        else if (acao==NE || acao==SE || acao==SO || acao==NO) {
            return 1.5f;
        }
        else {
            return 0f;
        }
    }

    public TreeNode buscaTreeNode(Estado st, Iterable<TreeNode> itbl) {
        Iterator iter = itbl.iterator();
        while(iter.hasNext()) {
            TreeNode node = (TreeNode)iter.next();
            if (node.getState().igualAo(st)) {
                return node;
            }
        }
        return null;
    }

    public Estado buscaEstado(Estado searched, Iterable<Estado> itbl) {
        Iterator iter = itbl.iterator();
        while(iter.hasNext()) {
            Estado st = (Estado)iter.next();
            if (st.igualAo(searched)) {
                return st;
            }
        }
        return null;
    }

    public float calculaHeuristica (Estado st, Estado obj) {
        int dist_col = Math.abs(st.getCol()-obj.getCol());
        int dist_lin = Math.abs(st.getLin()-obj.getLin());
        return Math.min(dist_col, dist_lin)*1.5f + Math.abs(dist_col - dist_lin)*1f;
    }

    public int classificaFruta(char[] c)
    {
        int valor = -1;
        if (c[1] == 'R') {
            if (c[3] == 'R') {
                if (c[2] == 'R') {
                    valor = 4;
                } else if (c[2] == 'G') {
                    valor = 2;
                } else if (c[2] == 'B') {
                    valor = 2;
                }
            } else if (c[3] == 'G') {
                if (c[2] == 'R') {
                    valor = 2;
                } else if (c[2] == 'G') {
                    valor = 2;
                } else if (c[2] == 'B') {
                    valor = 0;
                }
            } else if (c[3] == 'B') {
                if (c[2] == 'R') {
                    valor = 2;
                } else if (c[2] == 'G') {
                    valor = 4;
                } else if (c[2] == 'B') {
                    valor = 2;
                }
            }
        } else if (c[1] == 'G') {
            if (c[0] == 'K') {
                if (c[3] == 'R') {
                    if (c[2] == 'R') {
                        valor = 2;
                    } else if (c[2] == 'G') {
                        valor = 2;
                    } else if (c[2] == 'B') {
                        valor = 0;
                    }
                } else if (c[3] == 'G') {
                    if (c[2] == 'R') {
                        valor = 2;
                    } else if (c[2] == 'G') {
                        valor = 4;
                    } else if (c[2] == 'B') {
                        valor = 0;
                    }
                } else if (c[3] == 'B') {
                    if (c[2] == 'R') {
                        valor = 0;
                    } else if (c[2] == 'G') {
                        valor = 2;
                    } else if (c[2] == 'B') {
                        valor = 2;
                    }
                }
            } else if (c[0] == 'W') {
                valor = 0;
            }
        } else if (c[1] == 'B') {
            if (c[2] == 'R') {
                if (c[3] == 'R') {
                    valor = 2;
                } else if (c[3] == 'G') {
                    valor = 0;
                } else if (c[3] == 'B') {
                    valor = 2;
                }
            } else if (c[2] == 'G') {
                if (c[3] == 'R') {
                    valor = 0;
                } else if (c[3] == 'G') {
                    valor = 2;
                } else if (c[3] == 'B') {
                    valor = 2;
                }
            } else if (c[2] == 'B') {
                if (c[3] == 'R') {
                    valor = 2;
                } else if (c[3] == 'G') {
                    valor = 2;
                } else if (c[3] == 'B') {
                    valor = 4;
                }
            }
        }

        return valor;
    }

    public void comeFruta (Estado estado) {
        int valorFruta = prob.crencaLabir.frutas[estado.getLin()][estado.getCol()][5];
        if (valorFruta == 0) {
            energia_restante = 0;
        } else {
            energia_restante += valorFruta;
        }
    }
}
