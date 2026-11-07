import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class Trabalho2 {
    static int VAZIO = -1; // marca de posição livre
    static int TAMANHO = 1000; // número de posições da tabela
    static int QUANTIDADE = 700; // chaves por conjunto
    static int acessos = 0; // contador de acessos (item 4.5)
    static int colisoes = 0; // contador de colisões (item 4.5)
    static int duplicadasDescartadas = 0; // contador do gerador de chaves (item 5.2)

    /**
     * Calcula a posição inicial da chave através do resto da divisão por 1000.
     * Retorna: a posição calculada entre 0 e 999.
     */
    static int hashDivisao(int chave) {
        return chave % 1000;
    }

    /**
     * Calcula a posição da chave através do resto da divisão pelo primo 997.
     * Retorna: a posição calculada entre 0 e 996.
     */
    static int hashPrimo(int chave) {
        return chave % 997;
    }

    /**
     * Divide a chave em três partes, soma seus valores e aplica o módulo 1000.
     * Retorna: a posição calculada entre 0 e 999.
     */
    static int hashDobra(int chave) {
        int parteFinal = chave % 1000;
        int parteMeio = (chave / 1000) % 1000;
        int parteInicio = chave / 1000000;
        return (parteFinal + parteMeio + parteInicio) % 1000;
    }

    /**
     * Eleva a chave ao quadrado e extrai os dígitos das posições 6, 7 e 8.
     * Retorna: a posição calculada entre 0 e 999.
     */
    static int hashMeioQuadrado(int chave) {
        long quadrado = (long) chave * chave;
        return (int) ((quadrado / 100000) % 1000);
    }

    /**
     * Direciona a chave para a respectiva função hash com base no identificador
     * informado.
     * Retorna: o índice calculado pela função selecionada.
     */
    static int aplicaHash(int funcao, int chave) {
        if (funcao == 1) {
            return hashDivisao(chave);
        } else if (funcao == 2) {
            return hashPrimo(chave);
        } else if (funcao == 3) {
            return hashDobra(chave);
        } else {
            return hashMeioQuadrado(chave);
        }
    }

    /**
     * Converte o código numérico da função hash para o seu nome.
     * Retorna: o nome da função ("divisao", "primo", "dobra" ou "meioQuadrado").
     */
    static String nomeFuncao(int funcao) {
        if (funcao == 1) {
            return "divisao";
        } else if (funcao == 2) {
            return "primo";
        } else if (funcao == 3) {
            return "dobra";
        } else {
            return "meioQuadrado";
        }
    }

    /**
     * Converte o código numérico do conjunto de dados para sua respectiva letra.
     * Retorna: a letra representativa do conjunto ("A", "B", "C" ou "D").
     */
    static String nomeConjunto(int conjunto) {
        if (conjunto == 1) {
            return "A";
        } else if (conjunto == 2) {
            return "B";
        } else if (conjunto == 3) {
            return "C";
        } else {
            return "D";
        }
    }

    /**
     * Calcula o próximo índice da tabela por meio do método de rehash linear.
     * Retorna: a nova posição ajustada pelo tamanho da tabela.
     */
    static int rehash(int posicao) {
        return (posicao + 1) % TAMANHO;
    }

    /**
     * Aloca o vetor da tabela hash e preenche todas as posições com VAZIO.
     * Retorna: a tabela criada e inicializada.
     */
    static int[] criaTabela() {
        int[] tabela = new int[TAMANHO];
        for (int posicao = 0; posicao < TAMANHO; posicao++) {
            tabela[posicao] = VAZIO;
        }

        return tabela;
    }

    /**
     * Insere uma chave na tabela hash utilizando rehashing linear para resolução de
     * colisões.
     * Retorna: 1 em caso de inserção bem-sucedida, 0 se a chave for duplicada ou -1
     * se a tabela estiver cheia.
     */
    static int insere(int[] tabela, int funcao, int chave) {
        int posicao = aplicaHash(funcao, chave);
        int passos = 0;
        while (passos < TAMANHO) {
            acessos++;
            passos++;
            if (tabela[posicao] == VAZIO) {
                tabela[posicao] = chave;
                return 1;
            }

            if (passos == 1) {
                colisoes++;
            }

            if (tabela[posicao] == chave) {
                return 0;
            }

            posicao = rehash(posicao);
        }

        return -1;
    }

    /**
     * Procura por uma chave na tabela hash aplicando o mesmo percurso do rehashing
     * linear.
     * Retorna: o índice onde a chave foi encontrada ou -1 caso ela não esteja
     * presente.
     */
    static int busca(int[] tabela, int funcao, int chave) {
        int posicao = aplicaHash(funcao, chave);
        int passos = 0;
        while (passos < TAMANHO) {
            acessos++;
            passos++;
            if (tabela[posicao] == VAZIO) {
                return -1;
            }

            if (tabela[posicao] == chave) {
                return posicao;
            }

            posicao = rehash(posicao);
        }

        return -1;
    }

    /**
     * Gera um conjunto com a quantidade especificada de chaves únicas conforme o
     * padrão do conjunto.
     * Retorna: o vetor contendo as chaves geradas e sem duplicatas.
     */
    static int[] geraConjunto(Random sorteio, int conjunto, int quantidade) {
        int[] chaves = new int[quantidade];
        int gerados = 0;
        duplicadasDescartadas = 0;

        while (gerados < quantidade) {
            int candidata;

            if (conjunto == 1) {
                candidata = 1000000 + sorteio.nextInt(9000000);
            } else if (conjunto == 2) {
                candidata = (1000 + sorteio.nextInt(9000)) * 1000;
            } else if (conjunto == 3) {
                candidata = (100000 + sorteio.nextInt(900000)) * 10;
            } else {
                candidata = 1000001 + gerados;
            }
            boolean unica = true;
            for (int posicao = 0; posicao < gerados; posicao++) {
                if (chaves[posicao] == candidata) {
                    duplicadasDescartadas++;
                    unica = false;
                }
            }

            if (unica) {
                chaves[gerados] = candidata;
                gerados++;
            }
        }

        return chaves;
    }

    /**
     * Executa o experimento completo para um par de conjunto e função hash,
     * coletando métricas e gravando a linha resultante.
     * Retorna: nenhum valor.
     */
    static void executaCombinacao(PrintWriter arquivo, int conjunto, int[] chaves, int funcao) {
        int[] contagem = new int[TAMANHO];
        for (int posicao = 0; posicao < QUANTIDADE; posicao++) {
            contagem[aplicaHash(funcao, chaves[posicao])]++;
        }

        int usadas = 0;
        int maiorConcentracao = 0;
        int somaQuadrados = 0;
        for (int posicao = 0; posicao < TAMANHO; posicao++) {

            if (contagem[posicao] > 0) {
                usadas++;

            }
            if (contagem[posicao] > maiorConcentracao) {
                maiorConcentracao = contagem[posicao];
            }
            somaQuadrados += contagem[posicao] * contagem[posicao];

        }

        int[] tabela = criaTabela();
        acessos = 0;
        colisoes = 0;
        int maiorAcessos = 0;
        long inicio = System.nanoTime();
        for (int posicao = 0; posicao < QUANTIDADE; posicao++) {
            int acessosAntes = acessos;
            insere(tabela, funcao, chaves[posicao]);
            int acessosGastos = acessos - acessosAntes;
            if (acessosGastos > maiorAcessos) {
                maiorAcessos = acessosGastos;
            }
        }
        long fim = System.nanoTime();
        long tempoInsercao = (fim - inicio) / QUANTIDADE;
        int colisoesInsercao = colisoes;
        int acessosInsercao = acessos;

        acessos = 0;
        for (int posicao = 0; posicao < QUANTIDADE; posicao++) {
            busca(tabela, funcao, chaves[posicao]);
        }
        int acessosBusca = acessos;

        String linha = String.format("%s;%s;%d;%d;%d;%d;%d;%d;%d;%d;%.2f;%d", nomeConjunto(conjunto),
                nomeFuncao(funcao), QUANTIDADE,
                usadas, TAMANHO - usadas, maiorConcentracao, somaQuadrados, colisoesInsercao, acessosInsercao,
                maiorAcessos, (float) acessosBusca / QUANTIDADE, tempoInsercao);
        System.out.println(linha);
        arquivo.println(linha);
    }

    /**
     * Executa os de 17 testes de validação dos algoritmos e configurações.
     * Retorna: nenhum valor.
     */
    static void executaTestes() {
        int aprovados = 0;
        int totalTestes = 17;

        int[] chavesH = { 9837561, 1234567, 9999999, 1000000, 5654396, 4967000 };
        int[] esperadoDivisao = { 561, 567, 999, 0, 396, 0 };
        int[] esperadoPrimo = { 162, 281, 89, 9, 409, 943 };
        int[] esperadoDobra = { 407, 802, 7, 1, 55, 971 };
        int[] esperadoMeioQuadrado = { 64, 556, 800, 0, 941, 890 };
        for (int indice = 0; indice < 6; indice += 1) {
            int chave = chavesH[indice];
            boolean passou = (hashDivisao(chave) == esperadoDivisao[indice])
                    && (hashPrimo(chave) == esperadoPrimo[indice])
                    && (hashDobra(chave) == esperadoDobra[indice])
                    && (hashMeioQuadrado(chave) == esperadoMeioQuadrado[indice]);

            String nome = "H" + (indice + 1);
            if (passou) {
                System.out.println("TESTE " + nome + ": APROVADO");
                aprovados += 1;
            } else {
                System.out.println("TESTE " + nome + ": REPROVADO");
            }
        }

        boolean passouH7 = true;
        int[] conjuntoH7 = geraConjunto(new java.util.Random(1990), 1, 700);
        for (int indice = 0; indice < QUANTIDADE; indice += 1) {
            for (int funcao = 1; funcao <= 4; funcao += 1) {
                int posicaoHash = aplicaHash(funcao, conjuntoH7[indice]);
                if (posicaoHash < 0 || posicaoHash > 999) {
                    passouH7 = false;
                }
            }
        }
        if (passouH7) {
            System.out.println("TESTE H7: APROVADO");
            aprovados += 1;
        } else {
            System.out.println("TESTE H7: REPROVADO");
        }

        int[] tabela = criaTabela();
        acessos = 0;
        colisoes = 0;
        insere(tabela, 1, 5001);
        insere(tabela, 1, 7001);
        insere(tabela, 1, 9001);

        if (tabela[1] == 5001 && tabela[2] == 7001 && tabela[3] == 9001) {
            System.out.println("TESTE I1: APROVADO");
            aprovados += 1;
        } else {
            System.out.println("TESTE I1: REPROVADO");
        }

        if (colisoes == 2 && acessos == 6) {
            System.out.println("TESTE I2: APROVADO");
            aprovados += 1;
        } else {
            System.out.println("TESTE I2: REPROVADO");
        }

        acessos = 0;
        int posI3 = busca(tabela, 1, 9001);
        if (posI3 == 3 && acessos == 3) {
            System.out.println("TESTE I3: APROVADO");
            aprovados += 1;
        } else {
            System.out.println("TESTE I3: REPROVADO");
        }

        acessos = 0;
        int posI4 = busca(tabela, 1, 4001);
        if (posI4 == -1 && acessos == 4) {
            System.out.println("TESTE I4: APROVADO");
            aprovados += 1;
        } else {
            System.out.println("TESTE I4: REPROVADO");
        }

        int[] tabelaI5 = criaTabela();
        insere(tabelaI5, 1, 1000999);
        insere(tabelaI5, 1, 2000999);
        if (tabelaI5[999] == 1000999 && tabelaI5[0] == 2000999) {
            System.out.println("TESTE I5: APROVADO");
            aprovados += 1;
        } else {
            System.out.println("TESTE I5: REPROVADO");
        }

        acessos = 0;
        int retI6 = insere(tabelaI5, 1, 2000999);
        if (retI6 == 0 && acessos == 2) {
            System.out.println("TESTE I6: APROVADO");
            aprovados += 1;
        } else {
            System.out.println("TESTE I6: REPROVADO");
        }

        int[] espPrimeira = { 4429279, 1279000, 8292790, 1000001 };
        int[] espSegunda = { 3976163, 7163000, 3761630, 1000002 };
        int[] espTerceira = { 9187615, 7615000, 1876150, 1000003 };
        int[] espUltima = { 9691227, 7494000, 6289620, 1000700 };
        int[] espDescarte = { 0, 32, 0, 0 };

        for (int indice = 0; indice < 4; indice += 1) {
            duplicadasDescartadas = 0;
            int[] chaves = geraConjunto(new java.util.Random(1990), indice + 1, 700);

            boolean passou = (chaves[0] == espPrimeira[indice])
                    && (chaves[1] == espSegunda[indice])
                    && (chaves[2] == espTerceira[indice])
                    && (chaves[699] == espUltima[indice])
                    && (duplicadasDescartadas == espDescarte[indice]);

            String nome = "S" + (indice + 1);
            if (passou) {
                System.out.println("TESTE " + nome + ": APROVADO");
                aprovados += 1;
            } else {
                System.out.println("TESTE " + nome + ": REPROVADO");
            }
        }

        System.out.println("TESTES: " + aprovados + " de " + totalTestes + " aprovados");
    }

    /**
     * Ponto de entrada da aplicação que executa os testes iniciais e salva os
     * resultados de todas as combinações do experimento em um arquivo CSV.
     * Retorna: nenhum valor.
     */
    public static void main(String[] args) throws IOException {
        executaTestes();
        PrintWriter arquivo = new PrintWriter("resultados_trabalho2.csv");
        arquivo.println(
                "conjunto;funcao;chaves;posicoesUsadas;posicoesNuncaUsadas;maiorConcentracao;somaQuadrados;colisoes;acessosInsercaoTotal;maiorAcessosUmaInsercao;acessosBuscaSucessoMedia;tempoInsercaoNsPorOp");
        for (int conjunto = 1; conjunto < 5; conjunto++) {
            Random sorteio = new Random(1990);
            int[] chaves = geraConjunto(sorteio, conjunto, QUANTIDADE);

            for (int funcao = 1; funcao < 5; funcao++) {
                executaCombinacao(arquivo, conjunto, chaves, funcao);
            }
        }

        arquivo.close();
    }
}
