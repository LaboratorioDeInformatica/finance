package lab.info.com.finance.api.dto;

import lab.info.com.finance.model.entity.Usuario;
import lab.info.com.finance.model.enums.StatusLancamento;
import lab.info.com.finance.model.enums.TipoLancamento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LancamentoDTO {

    private Long id;
    private String descricao;
    private Integer mes ;
    private Integer ano ;
    private Long usuario;
    private BigDecimal valor;
    private LocalDate dataCadastro;
    private TipoLancamento tipo;
    private StatusLancamento status;
}
