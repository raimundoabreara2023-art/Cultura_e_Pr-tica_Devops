package br.com.raizes.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.com.raizes.dto.NucleoFamiliarDTO;
import br.com.raizes.model.Pessoa;
import br.com.raizes.model.Relacionamento;
import br.com.raizes.model.TipoRelacionamento;
import br.com.raizes.repository.PessoaRepository;
import br.com.raizes.repository.RelacionamentoRepository;

public class RelacionamentoService {

    private final RelacionamentoRepository repository;
    private final PessoaRepository pessoaRepository;

    // =========================================
    // CONSTRUTOR
    // =========================================

    public RelacionamentoService() {
        repository = new RelacionamentoRepository();
        pessoaRepository = new PessoaRepository();
    }

   
    // =========================================
    // SALVAR
    // =========================================

    public void salvar(Relacionamento relacionamento)
            throws SQLException {

        // -----------------------------------------
        // VALIDAR RELACIONAMENTO
        // -----------------------------------------
        validarRelacionamento(relacionamento);

        // -----------------------------------------
        // VERIFICAR DUPLICIDADE
        // -----------------------------------------
        boolean existe =
                repository.existeRelacionamento(
                        relacionamento.getPessoaOrigem().getId(),
                        relacionamento.getPessoaDestino().getId(),
                        relacionamento.getTipo().name()
                );

        if (existe) {
            throw new IllegalArgumentException(
                "Esse relacionamento já existe."
            );
        }

        // -----------------------------------------
        // SALVAR
        // -----------------------------------------
        repository.salvar(relacionamento);
    }


    // =========================================
    // SALVAR NÚCLEO FAMILIAR COMPLETO (EM LOTE)
    // =========================================

    public void salvarNucleoFamiliar(Pessoa pai, Pessoa mae, List<Pessoa> filhos) throws SQLException {
        if (pai == null && mae == null) {
            throw new IllegalArgumentException("Informe pelo menos o Pai ou a Mãe para o núcleo familiar.");
        }

        // 1. Salvar vínculo de Cônjuge (se ambos estiverem preenchidos)
        if (pai != null && mae != null) {
            Relacionamento relConjuge = new Relacionamento();
            relConjuge.setPessoaOrigem(pai);
            relConjuge.setPessoaDestino(mae);
            relConjuge.setTipo(TipoRelacionamento.CONJUGE);
            
            try {
                salvar(relConjuge);
            } catch (IllegalArgumentException e) {
                // Ignora se o vínculo conjugal já existir e prossegue para os filhos
            }
        }

        // 2. Salvar os vínculos dos Filhos selecionados
        if (filhos != null && !filhos.isEmpty()) {
            for (Pessoa filho : filhos) {
                // Vínculo com o Pai (se informado)
                if (pai != null) {
                    Relacionamento relPai = new Relacionamento();
                    relPai.setPessoaOrigem(pai);
                    relPai.setPessoaDestino(filho);
                    relPai.setTipo(TipoRelacionamento.PAI);
                    try {
                        salvar(relPai);
                    } catch (IllegalArgumentException e) {
                        // Ignora se a relação PAI->filho já estiver registada
                    }
                }

                // Vínculo com a Mãe (se informada)
                if (mae != null) {
                    Relacionamento relMae = new Relacionamento();
                    relMae.setPessoaOrigem(mae);
                    relMae.setPessoaDestino(filho);
                    relMae.setTipo(TipoRelacionamento.MAE);
                    try {
                        salvar(relMae);
                    } catch (IllegalArgumentException e) {
                        // Ignora se a relação MAE->filho já estiver registada
                    }
                }
            }
        }
    }


    // =========================================
    // BUSCAR POR ID
    // =========================================

    public Relacionamento buscarPorId(Long id)
            throws SQLException {

        if (id == null) {
            throw new IllegalArgumentException(
                "O ID do relacionamento não pode ser nulo."
            );
        }

        return repository.buscarPorId(id);
    }

    // =========================================
    // LISTAR TODOS
    // =========================================

    public List<Relacionamento> listarTodos()
            throws SQLException {
        return repository.listarTodos();
    }

    // =========================================
    // ATUALIZAR
    // =========================================

    public void atualizar(Relacionamento relacionamento)
            throws SQLException {

        if (relacionamento == null) {
            throw new IllegalArgumentException(
                "O relacionamento não pode ser nulo."
            );
        }

        if (relacionamento.getId() == null) {
            throw new IllegalArgumentException(
                "O ID do relacionamento não pode ser nulo."
            );
        }

        Relacionamento existente =
                repository.buscarPorId(
                    relacionamento.getId()
                );

        if (existente == null) {
            throw new IllegalArgumentException(
                "O relacionamento informado não existe."
            );
        }

        validarRelacionamento(relacionamento);

        repository.atualizar(relacionamento);
    }

    // =========================================
    // EXCLUIR
    // =========================================

    public void excluir(Long id)
            throws SQLException {

        if (id == null) {
            throw new IllegalArgumentException(
                "O ID do relacionamento não pode ser nulo."
            );
        }

        Relacionamento existente =
                repository.buscarPorId(id);

        if (existente == null) {
            throw new IllegalArgumentException(
                "O relacionamento informado não existe."
            );
        }

        repository.excluir(id);
    }

    // =========================================
    // VALIDAR RELACIONAMENTO
    // =========================================

    private void validarRelacionamento(
            Relacionamento relacionamento)
            throws SQLException {

        if (relacionamento == null) {
            throw new IllegalArgumentException(
                "O relacionamento não pode ser nulo."
            );
        }

        Pessoa origem =
                relacionamento.getPessoaOrigem();

        if (origem == null) {
            throw new IllegalArgumentException(
                "A pessoa de origem não pode ser nula."
            );
        }

        if (origem.getId() == null) {
            throw new IllegalArgumentException(
                "A pessoa de origem precisa possuir um ID."
            );
        }

        Pessoa destino =
                relacionamento.getPessoaDestino();

        if (destino == null) {
            throw new IllegalArgumentException(
                "A pessoa de destino não pode ser nula."
            );
        }

        if (destino.getId() == null) {
            throw new IllegalArgumentException(
                "A pessoa de destino precisa possuir um ID."
            );
        }

        if (origem.getId().equals(destino.getId())) {
            throw new IllegalArgumentException(
                "A pessoa de origem e a pessoa de destino " +
                "não podem ser a mesma pessoa."
            );
        }

        Pessoa origemBanco =
                pessoaRepository.buscarPorId(
                    origem.getId()
                );

        if (origemBanco == null) {
            throw new IllegalArgumentException(
                "A pessoa de origem não existe no banco."
            );
        }

        Pessoa destinoBanco =
                pessoaRepository.buscarPorId(
                    destino.getId()
                );

        if (destinoBanco == null) {
            throw new IllegalArgumentException(
                "A pessoa de destino não existe no banco."
            );
        }

        if (relacionamento.getTipo() == null) {
            throw new IllegalArgumentException(
                "O tipo do relacionamento não pode ser nulo."
            );
        }
    }

    // =========================================
    // LISTAR NÚCLEOS FAMILIARES (DTO)
    // =========================================

    public List<NucleoFamiliarDTO> listarNucleosFamiliares() throws SQLException {
        List<Relacionamento> todos = repository.listarTodos();
        
        // Estruturas auxiliares para agrupar as informações do núcleo
        Map<String, Long> idsRelacionamentoConjuge = new LinkedHashMap<>();
        Map<String, List<Pessoa>> mapaNucleosPessoas = new LinkedHashMap<>();
        Map<String, String> casaisIdentificados = new LinkedHashMap<>();
        Map<String, Pessoa> pessoasMap = new LinkedHashMap<>();

        // 1. Mapear todas as pessoas envolvidas para acesso rápido aos dados completos (ID, datas, etc.)
        for (Relacionamento r : todos) {
            if (r.getPessoaOrigem() != null) pessoasMap.put(r.getPessoaOrigem().getNome(), r.getPessoaOrigem());
            if (r.getPessoaDestino() != null) pessoasMap.put(r.getPessoaDestino().getNome(), r.getPessoaDestino());
        }

        // 2. Identificar todos os casais e guardar o ID do relacionamento de cônjuge
        for (Relacionamento r : todos) {
            if (r.getTipo() == TipoRelacionamento.CONJUGE) {
                String p1 = r.getPessoaOrigem().getNome();
                String p2 = r.getPessoaDestino().getNome();
                String chaveCasal = p1 + " & " + p2;
                casaisIdentificados.put(p1, chaveCasal);
                casaisIdentificados.put(p2, chaveCasal);
                mapaNucleosPessoas.putIfAbsent(chaveCasal, new ArrayList<>());
                idsRelacionamentoConjuge.put(chaveCasal, r.getId());
            }
        }

        // 3. Associar os filhos aos respetivos casais ou solos
        for (Relacionamento r : todos) {
            if (r.getTipo() == TipoRelacionamento.PAI || r.getTipo() == TipoRelacionamento.MAE) {
                String nomeProgenitor = r.getPessoaOrigem().getNome();
                Pessoa filho = r.getPessoaDestino();

                String chaveCasal = casaisIdentificados.get(nomeProgenitor);
                if (chaveCasal != null) {
                    List<Pessoa> filhos = mapaNucleosPessoas.get(chaveCasal);
                    if (filhos != null) {
                        boolean jaExiste = filhos.stream().anyMatch(f -> f.getId().equals(filho.getId()));
                        if (!jaExiste) {
                            filhos.add(filho);
                        }
                    }
                } else {
                    String chaveSolteiro = nomeProgenitor + " (Solo)";
                    mapaNucleosPessoas.putIfAbsent(chaveSolteiro, new ArrayList<>());
                    List<Pessoa> filhos = mapaNucleosPessoas.get(chaveSolteiro);
                    boolean jaExiste = filhos.stream().anyMatch(f -> f.getId().equals(filho.getId()));
                    if (!jaExiste) {
                        filhos.add(filho);
                    }
                    idsRelacionamentoConjuge.putIfAbsent(chaveSolteiro, r.getId());
                }
            }
        }

        // 4. Montar a lista final de DTOs preenchendo todos os campos necessários para a UI
        List<NucleoFamiliarDTO> resultado = new ArrayList<>();
        
        for (Map.Entry<String, List<Pessoa>> entry : mapaNucleosPessoas.entrySet()) {
            String chave = entry.getKey();
            List<Pessoa> listaFilhosPessoas = entry.getValue();

            // Ordena os filhos do mais velho para o mais novo
            listaFilhosPessoas.sort((f1, f2) -> {
                if (f1.getDataNascimento() == null && f2.getDataNascimento() == null) return 0;
                if (f1.getDataNascimento() == null) return 1;
                if (f2.getDataNascimento() == null) return -1;
                return f1.getDataNascimento().compareTo(f2.getDataNascimento());
            });

            List<String> listaNomes = new ArrayList<>();
            for (Pessoa f : listaFilhosPessoas) {
                listaNomes.add(f.getNome());
            }

            Long idRelacionamento = idsRelacionamentoConjuge.get(chave);
            Long paiId = null;
            String nomePai = null;
            java.time.LocalDate dataNascPai = null;
            Long maeId = null;
            String nomeMae = null;
            java.time.LocalDate dataNascMae = null;

            if (chave.contains(" & ")) {
                String[] partes = chave.split(" & ");
                String nPai = partes[0].trim();
                String nMae = partes[1].trim();

                Pessoa pPai = pessoasMap.get(nPai);
                if (pPai != null) {
                    paiId = pPai.getId();
                    nomePai = pPai.getNome();
                    dataNascPai = pPai.getDataNascimento();
                }

                Pessoa pMae = pessoasMap.get(nMae);
                if (pMae != null) {
                    maeId = pMae.getId();
                    nomeMae = pMae.getNome();
                    dataNascMae = pMae.getDataNascimento();
                }
            } else {
                String nomePessoaSolo = chave.replace(" (Solo)", "").trim();
                Pessoa pSolo = pessoasMap.get(nomePessoaSolo);
                if (pSolo != null) {
                    boolean éMae = false;
                    for (Relacionamento r : todos) {
                        if (r.getPessoaOrigem() != null && r.getPessoaOrigem().getId().equals(pSolo.getId()) 
                                && r.getTipo() == TipoRelacionamento.MAE) {
                            éMae = true;
                            break;
                        }
                    }

                    if (éMae) {
                        maeId = pSolo.getId();
                        nomeMae = pSolo.getNome();
                        dataNascMae = pSolo.getDataNascimento();
                    } else {
                        paiId = pSolo.getId();
                        nomePai = pSolo.getNome();
                        dataNascPai = pSolo.getDataNascimento();
                    }
                }
            }

            // Instancia o DTO utilizando o construtor completo
            NucleoFamiliarDTO dto = new NucleoFamiliarDTO(
                idRelacionamento, chave,
                paiId, nomePai, dataNascPai,
                maeId, nomeMae, dataNascMae,
                listaNomes
            );

            resultado.add(dto);
        }

        return resultado;
    }
}