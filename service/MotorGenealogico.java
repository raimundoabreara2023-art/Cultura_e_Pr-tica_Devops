package br.com.raizes.service;
import java.util.ArrayList;
import java.util.List;
import br.com.raizes.model.Pessoa;
import br.com.raizes.model.Relacionamento;
import br.com.raizes.model.TipoRelacionamento;

public class MotorGenealogico {

    private final ArvoreService arvoreService;

    // =========================================
    // CONSTRUTOR
    // =========================================

    public MotorGenealogico(ArvoreService arvoreService) {
        this.arvoreService = arvoreService;
    }

    // =========================================
    // BUSCAR PAIS
    // =========================================

    public List<Pessoa> buscarPais(Pessoa pessoa) {

        List<Pessoa> pais = new ArrayList<>();

        if (pessoa == null) {
            return pais;
        }

        for (Relacionamento relacionamento :
                arvoreService.listarRelacionamentos()) {

            if (relacionamento.getPessoaDestino().equals(pessoa)) {

                TipoRelacionamento tipo = relacionamento.getTipo();

                if (tipo == TipoRelacionamento.PAI
                        || tipo == TipoRelacionamento.MAE) {

                    pais.add(relacionamento.getPessoaOrigem());
                }
            }
        }

        return pais;
    }

    // =========================================
    // BUSCAR FILHOS
    // =========================================

    public List<Pessoa> buscarFilhos(Pessoa pessoa) {

        List<Pessoa> filhos = new ArrayList<>();

        if (pessoa == null) {
            return filhos;
        }

        for (Relacionamento relacionamento :
                arvoreService.listarRelacionamentos()) {

            if (relacionamento.getPessoaOrigem().equals(pessoa)) {

                TipoRelacionamento tipo = relacionamento.getTipo();

                if (tipo == TipoRelacionamento.PAI
                        || tipo == TipoRelacionamento.MAE) {

                    filhos.add(relacionamento.getPessoaDestino());
                }
            }
        }

        return filhos;
    }

    // =========================================
    // BUSCAR IRMÃOS
    // =========================================

    public List<Pessoa> buscarIrmaos(Pessoa pessoa) {

        List<Pessoa> irmaos = new ArrayList<>();

        if (pessoa == null) {
            return irmaos;
        }

        // 1. Encontrar os pais da pessoa
        List<Pessoa> pais = buscarPais(pessoa);

        // 2. Para cada pai/mãe, encontrar seus filhos
        for (Pessoa pai : pais) {

            List<Pessoa> filhosDoPai = buscarFilhos(pai);

            for (Pessoa filho : filhosDoPai) {

                // 3. Não adicionar a própria pessoa
                if (!filho.equals(pessoa)) {

                    // 4. Evitar duplicados
                    if (!irmaos.contains(filho)) {
                        irmaos.add(filho);
                    }
                }
            }
        }

        return irmaos;
    }


    // =========================================
    // BUSCAR BISAVÓS
    // =========================================

    public List<Pessoa> buscarBisavos(Pessoa pessoa) {

        List<Pessoa> bisavos = new ArrayList<>();

        if (pessoa == null) {
            return bisavos;
        }

        // 1. Encontramos os avós da pessoa
        List<Pessoa> avos = buscarAvos(pessoa);

        // 2. Para cada avô ou avó,
        //    encontramos os pais deles
        for (Pessoa avoOuAvo : avos) {

            List<Pessoa> paisDosAvos = buscarPais(avoOuAvo);

            // 3. Os pais dos avós são os bisavós
            for (Pessoa bisavo : paisDosAvos) {

                // 4. Evitar duplicados
                if (!bisavos.contains(bisavo)) {
                    bisavos.add(bisavo);
                }
            }
        }

        return bisavos;
    }

    // =========================================
    // BUSCAR AVÓS
    // =========================================

    public List<Pessoa> buscarAvos(Pessoa pessoa) {

        List<Pessoa> avos = new ArrayList<>();

        if (pessoa == null) {
            return avos;
        }

        // 1. Encontrar os pais da pessoa
        List<Pessoa> pais = buscarPais(pessoa);

        // 2. Encontrar os pais de cada pai/mãe
        for (Pessoa pai : pais) {

            List<Pessoa> paisDoPai = buscarPais(pai);

            for (Pessoa avo : paisDoPai) {

                // 3. Evitar avós duplicados
                if (!avos.contains(avo)) {
                    avos.add(avo);
                }
            }
        }

        return avos;
    }

    // =========================================
    // BUSCAR NETOS
    // =========================================

    public List<Pessoa> buscarNetos(Pessoa pessoa) {

        List<Pessoa> netos = new ArrayList<>();

        if (pessoa == null) {
            return netos;
        }

        // 1. Encontrar os filhos da pessoa
        List<Pessoa> filhos = buscarFilhos(pessoa);

        // 2. Para cada filho, encontrar os filhos dele
        for (Pessoa filho : filhos) {

            List<Pessoa> filhosDoFilho = buscarFilhos(filho);

            // 3. Adicionar os filhos dos filhos
            for (Pessoa neto : filhosDoFilho) {

                // 4. Evitar duplicados
                if (!netos.contains(neto)) {
                    netos.add(neto);
                }
            }
        }

        return netos;
    }

    // =========================================
    // BUSCAR TIOS E TIAS
    // =========================================

    public List<Pessoa> buscarTios(Pessoa pessoa) {

        List<Pessoa> tios = new ArrayList<>();

        if (pessoa == null) {
            return tios;
        }

        // 1. Encontrar os pais da pessoa
        List<Pessoa> pais = buscarPais(pessoa);

        // 2. Para cada pai/mãe, encontrar os irmãos
        for (Pessoa paiOuMae : pais) {

            List<Pessoa> irmaosDosPais = buscarIrmaos(paiOuMae);

            // 3. Os irmãos dos pais são os tios/tias
            for (Pessoa irmao : irmaosDosPais) {

                if (!tios.contains(irmao)) {
                    tios.add(irmao);
                }
            }
        }

        return tios;
    }

    // =========================================
    // BUSCAR SOBRINHOS
    // =========================================

    public List<Pessoa> buscarSobrinhos(Pessoa pessoa) {

        List<Pessoa> sobrinhos = new ArrayList<>();

        if (pessoa == null) {
            return sobrinhos;
        }

        // 1. Primeiro encontramos os irmãos da pessoa
        List<Pessoa> irmaos = buscarIrmaos(pessoa);

        // 2. Para cada irmão, buscamos os filhos
        for (Pessoa irmao : irmaos) {

            List<Pessoa> filhos = buscarFilhos(irmao);

            // 3. Os filhos dos irmãos são os sobrinhos
            sobrinhos.addAll(filhos);
        }

        return sobrinhos;
    }

    // =========================================
    // BUSCAR SOBRINHOS CONJUGUE
    // =========================================
    public List<Pessoa> buscarSobrinhosLadoConjuge(Pessoa pessoa) {

        List<Pessoa> sobrinhos = new ArrayList<>();

        if (pessoa == null) {
            return sobrinhos;
        }

        // 1. Encontramos o cônjuge da pessoa
        Pessoa conjuge = buscarConjuge(pessoa);

        if (conjuge == null) {
            return sobrinhos;
        }

        // 2. Encontramos os irmãos do cônjuge
        List<Pessoa> irmaosDoConjuge = buscarIrmaos(conjuge);

        // 3. Buscamos os filhos desses irmãos
        for (Pessoa irmao : irmaosDoConjuge) {

            List<Pessoa> filhos = buscarFilhos(irmao);

            // 4. Os filhos dos irmãos do cônjuge
            //    são os sobrinhos pelo lado do cônjuge
            sobrinhos.addAll(filhos);
        }

        return sobrinhos;
    }

    // =========================================
    // BUSCAR TODOS OS SOBRINHOS
    // =========================================
    public List<Pessoa> buscarTodosSobrinhos(Pessoa pessoa) {

        List<Pessoa> sobrinhos = new ArrayList<>();

        if (pessoa == null) {
            return sobrinhos;
        }

        // Sobrinhos dos irmãos da própria pessoa
        sobrinhos.addAll(buscarSobrinhos(pessoa));

        // Sobrinhos dos irmãos do cônjuge
        sobrinhos.addAll(buscarSobrinhosLadoConjuge(pessoa));

        return sobrinhos;
    }
    
    // =========================================
    // BUSCAR TODOS OS PRIMOS
    // =========================================
    public List<Pessoa> buscarPrimos(Pessoa pessoa) {

        List<Pessoa> primos = new ArrayList<>();

        if (pessoa == null) {
            return primos;
        }

        // Primos pelo lado do pai
        for (Pessoa primo : buscarPrimosLadoPai(pessoa)) {

            if (!primos.contains(primo)) {
                primos.add(primo);
            }
        }

        // Primos pelo lado da mãe
        for (Pessoa primo : buscarPrimosLadoMae(pessoa)) {

            if (!primos.contains(primo)) {
                primos.add(primo);
            }
        }

        return primos;
    }

    // =========================================
    // BUSCAR PRIMOS LADO DO PAI
    // =========================================

    public List<Pessoa> buscarPrimosLadoPai(Pessoa pessoa) {

        List<Pessoa> primos = new ArrayList<>();

        if (pessoa == null) {
            return primos;
        }

        // 1. Encontramos os pais da pessoa
        List<Pessoa> pais = buscarPais(pessoa);

        // 2. Procuramos o pai
        for (Pessoa paiOuMae : pais) {

            if (paiOuMae.getSexo() != null
                    && paiOuMae.getSexo().equalsIgnoreCase("M")) {

                // 3. Encontramos os irmãos do pai
                List<Pessoa> irmaosDoPai = buscarIrmaos(paiOuMae);

                // 4. Os filhos dos irmãos do pai são os primos
                for (Pessoa tioOuTia : irmaosDoPai) {

                    List<Pessoa> filhos = buscarFilhos(tioOuTia);

                    // 5. Evitar duplicados
                    for (Pessoa primo : filhos) {

                        if (!primos.contains(primo)) {
                            primos.add(primo);
                        }
                    }
                }
            }
        }

        return primos;
    }

    // =========================================
    // BUSCAR PRIMOS LADO DA MÃE
    // =========================================

    public List<Pessoa> buscarPrimosLadoMae(Pessoa pessoa) {

        List<Pessoa> primos = new ArrayList<>();

        if (pessoa == null) {
            return primos;
        }

        // 1. Encontramos os pais da pessoa
        List<Pessoa> pais = buscarPais(pessoa);

        // 2. Procuramos a mãe
        for (Pessoa paiOuMae : pais) {

            if (paiOuMae.getSexo() != null
                    && paiOuMae.getSexo().equalsIgnoreCase("F")) {

                // 3. Encontramos os irmãos da mãe
                List<Pessoa> irmaosDaMae = buscarIrmaos(paiOuMae);

                // 4. Os filhos dos irmãos da mãe são os primos
                for (Pessoa tioOuTia : irmaosDaMae) {

                    List<Pessoa> filhos = buscarFilhos(tioOuTia);

                    // 5. Evitar duplicados
                    for (Pessoa primo : filhos) {

                        if (!primos.contains(primo)) {
                            primos.add(primo);
                        }
                    }
                }
            }
        }

        return primos;
    }

    // =========================================
    // BUSCAR CÔNJUGE
    // =========================================

    public Pessoa buscarConjuge(Pessoa pessoa) {

        if (pessoa == null) {
            return null;
        }

        for (Relacionamento relacionamento :
                arvoreService.listarRelacionamentos()) {

            if (relacionamento.getTipo() == TipoRelacionamento.CONJUGE) {

                if (relacionamento.getPessoaOrigem().equals(pessoa)) {
                    return relacionamento.getPessoaDestino();
                }

                if (relacionamento.getPessoaDestino().equals(pessoa)) {
                    return relacionamento.getPessoaOrigem();
                }
            }
        }

        return null;
    }

}