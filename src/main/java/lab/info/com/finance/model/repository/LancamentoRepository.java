package lab.info.com.finance.model.repository;

import lab.info.com.finance.model.entity.Lancamento;
import lab.info.com.finance.model.enums.TipoLancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    @Query(value = "select sum(l.valor) from Lancamento l " +
            " inner join l.usuario u " +
            " where u.id = :id " +
            " and l.tipo = :tipo group by u ")
    BigDecimal obterSaldoPorUsuario(@Param("id") Long id, @Param("tipo") TipoLancamento tipo);
}
