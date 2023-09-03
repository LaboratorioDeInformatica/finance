package lab.info.com.finance.model.repository;

import lab.info.com.finance.model.entity.Lancamento;
import lab.info.com.finance.model.entity.Usuario;
import lab.info.com.finance.model.enums.StatusLancamento;
import lab.info.com.finance.model.enums.TipoLancamento;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LancamentoRepositoryTest {

    @Autowired
    LancamentoRepository repository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    @DisplayName("Deve salvar um lancamento")
    public void devePersistirUmLancamentoNaBaseDeDados(){
        //cenario
        Lancamento lancamento = criarLancamento();
        //acao
        Lancamento lancamentoSalvo = repository.save(lancamento);
        //verificacao
        Assertions.assertThat(lancamentoSalvo.getId()).isNotNull();
    }

    @Test
    @DisplayName("Deve remover um lancamento")
    public void deveRemoverUmLancamentoNaBaseDeDados(){
        //cenario
        Lancamento lancamento = criarLancamento();
        entityManager.persist(lancamento);
        lancamento = entityManager.find(Lancamento.class, lancamento.getId());
        //acao
        repository.delete(lancamento);
        //verificacao
        Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());
        Assertions.assertThat(lancamentoInexistente).isNull();
    }

    @Test
    @DisplayName("Deve atualizar um lancamento")
    public void deveAtualizarUmLancamentoNaBaseDeDados(){
        //cenario
        Lancamento lancamento = criarLancamento();
        entityManager.persist(lancamento);
        lancamento.setAno(2022);
        lancamento.setDescricao("lancamento atualizado");
        lancamento.setMes(2);
        lancamento.setTipo(TipoLancamento.RECEITA);
        lancamento.setStatus(StatusLancamento.CANCELADO);
        lancamento.setValor(BigDecimal.valueOf(100));
        //acao
        repository.save(lancamento);
        //verificacao
        Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());
        Assertions.assertThat(lancamentoAtualizado.getAno()).isEqualTo(2022);
        Assertions.assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("lancamento atualizado");
        Assertions.assertThat(lancamentoAtualizado.getMes()).isEqualTo(2);
        Assertions.assertThat(lancamentoAtualizado.getTipo()).isEqualTo(TipoLancamento.RECEITA);
        Assertions.assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);
        Assertions.assertThat(lancamentoAtualizado.getValor()).isEqualTo(BigDecimal.valueOf(100));
    }

    @Test
    @DisplayName("Deve buscar um lancamento por id")
    public void deveBuscarUmLancamentoPorId(){
        //cenario
        Lancamento lancamento = criarLancamento();
        entityManager.persist(lancamento);
        //acao
        Lancamento lancamentoEncontrado = repository.findById(lancamento.getId()).orElse(null);
        //verificacao
        Assertions.assertThat(lancamentoEncontrado).isNotNull();
    }

    @Test
    @DisplayName("Deve obter o saldo de um usuario")
    public void deveObterOSaldoDeUmUsuario(){
        //cenario
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario);
        Lancamento lancamento = criarLancamento();
        lancamento.setUsuario(usuario);
        entityManager.persist(lancamento);
        //acao
        BigDecimal saldo = repository.obterSaldoPorUsuario(lancamento.getUsuario().getId(), TipoLancamento.DESPESA, StatusLancamento.EFETIVADO);
        //verificacao
        Assertions.assertThat(saldo).isEqualTo(BigDecimal.valueOf(10,2));
    }

    private Lancamento criarLancamento() {
        return Lancamento.builder().ano(2021).mes(1).descricao("lancamento qualquer").valor(BigDecimal.valueOf(10,2)).tipo(TipoLancamento.DESPESA).status(StatusLancamento.EFETIVADO).build();
    }

    private Usuario criarUsuario() {
        return Usuario.builder().nome("usuario").email("email@email.com")
                .senha("senha").build();
    }
}
