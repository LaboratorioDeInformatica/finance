package lab.info.com.finance.service;

import lab.info.com.finance.model.entity.Lancamento;
import lab.info.com.finance.model.enums.StatusLancamento;

import java.util.List;

public interface LancamentoService {

    Lancamento salvar(Lancamento lancamento);
    Lancamento atualizar(Lancamento lancamento);
    void deletar(Lancamento lancamento);

    List<Lancamento> buscar(Lancamento lancamentoFiltro);
    void atualizarStatus(Lancamento lancamento,  StatusLancamento status);

    void validar(Lancamento lancamento);

    java.util.Optional<Lancamento> obterPorId(Long id);

    double obterSaldoPorUsuario(Long id);



}
