package lab.info.com.finance.service;

import lab.info.com.finance.exceptions.RegraNegocioException;
import lab.info.com.finance.model.entity.Lancamento;
import lab.info.com.finance.model.entity.Usuario;
import lab.info.com.finance.model.enums.StatusLancamento;
import lab.info.com.finance.model.enums.TipoLancamento;
import lab.info.com.finance.model.repository.LancamentoRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LancamentoServiceTest {

    @SpyBean
    LancamentoService service;

    @MockBean
    LancamentoRepository repository;

     @Test
     @DisplayName("Deve salvar um lancamento")
     public void devePersistirUmLancamentoNaBaseDeDados(){
         //cenario
         Lancamento lancamento = criarLancamento();
         lancamento.setId(1l);
         Mockito.doNothing().when(service).validar(Mockito.any(Lancamento.class));
         lancamento.setStatus(StatusLancamento.PENDENTE);
         Mockito.when(repository.save(Mockito.any(Lancamento.class))).thenReturn(lancamento);
         //acao
         Lancamento lancamentoSalvo = service.salvar(new Lancamento());
         //verificacao
         Assertions.assertThat(lancamentoSalvo.getId()).isNotNull();
            Assertions.assertThat(lancamentoSalvo.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
     }

        @Test
        @DisplayName("Deve lançar erro ao tentar salvar um lancamento com status pendente")
        public void naoDeveSalvarUmLancamentoComStatusPendente(){
            //cenario
            Lancamento lancamento = criarLancamento();
            Mockito.doThrow(RegraNegocioException.class).when(service).validar(Mockito.any(Lancamento.class));
            //acao
            org.junit.jupiter.api.Assertions.assertThrows(RegraNegocioException.class, () -> service.salvar(lancamento));
            //verificacao
            Assertions.catchThrowableOfType( () -> service.salvar(lancamento), RegraNegocioException.class);
            Mockito.verify(repository, Mockito.never()).save(lancamento);
        }

        @Test
        @DisplayName("Deve atualizar um lancamento")
        public void deveAtualizarUmLancamento(){
            //cenario
            Lancamento lancamento = criarLancamento();
            lancamento.setId(1L);
            lancamento.setStatus(StatusLancamento.PENDENTE);
            Mockito.doNothing().when(service).validar(lancamento);
            Mockito.when(repository.save(lancamento)).thenReturn(lancamento);
            //acao
            Lancamento atualizado = service.atualizar(lancamento);
            //verificacao
            Mockito.verify(repository, Mockito.times(1)).save(lancamento);
            Assertions.assertThat(atualizado.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
        }

        @Test
        @DisplayName("Deve lançar erro ao tentar atualizar um lancamento sem id")
        public void deveLancarErroAoTentarAtualizarUmLancamentoSemId(){
            //cenario
            Lancamento lancamento = criarLancamento();
            //acao
            org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class, () -> service.atualizar(lancamento));
            //verificacao
            Mockito.verify(repository, Mockito.never()).save(lancamento);
        }

        @Test
        @DisplayName("Deve deletar um lancamento")
        public void deveDeletarUmLancamento(){
            //cenario
            Lancamento lancamento = criarLancamento();
            lancamento.setId(1L);
            //acao
            service.deletar(lancamento);
            //verificacao
            Mockito.verify(repository).delete(lancamento);
        }

        @Test
        @DisplayName("Deve lançar erro ao tentar deletar um lancamento sem id")
        public void deveLancarErroAoTentarDeletarUmLancamentoSemId(){
            //cenario
            Lancamento lancamento = criarLancamento();
            //acao
            org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class, () -> service.deletar(lancamento));
            //verificacao
            Mockito.verify(repository, Mockito.never()).delete(lancamento);
        }

        @Test
        @DisplayName("Deve filtrar lancamentos")
        public void deveFiltrarLancamentos(){
            //cenario
            Lancamento lancamento = criarLancamento();
            lancamento.setId(1L);
            List<Lancamento> lista = Arrays.asList(lancamento);
            Mockito.when(repository.findAll(Mockito.any(org.springframework.data.domain.Example.class))).thenReturn(lista);

            //acao
            List<Lancamento> lancamentos = service.buscar(lancamento);
            //verificacao
            Mockito.verify(repository).findAll(Mockito.any(org.springframework.data.domain.Example.class));
            Assertions.assertThat(lancamentos).hasSize(1);
            Assertions.assertThat(lancamentos).contains(lancamento);

        }

        @Test
        @DisplayName("Deve atualizar o status de um lancamento")
        public void deveAtualizarOStatusDeUmLancamento(){
            //cenario
            Lancamento lancamento = criarLancamento();
            lancamento.setId(1L);
            lancamento.setStatus(StatusLancamento.PENDENTE);
            StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
            Mockito.doReturn(lancamento).when(service).atualizar(lancamento);
            //acao
            service.atualizarStatus(lancamento, novoStatus);
            //verificacao
            Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
            Mockito.verify(service).atualizar(lancamento);
        }

        @Test
        @DisplayName("Deve obter um lancamento por id")
        public void deveObterUmLancamentoPorId(){
            //cenario
            Long id = 1L;
            Lancamento lancamento = criarLancamento();
            lancamento.setId(id);
            Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
            //acao
            Optional<Lancamento> resultado = service.obterPorId(id);
            //verificacao
            Assertions.assertThat(resultado.isPresent()).isTrue();
        }

        @Test
        @DisplayName("Deve retornar vazio quando um lancamento não existe na base de dados")
        public void deveRetornarVazioQuandoUmLancamentoNaoExisteNaBaseDeDados(){
            //cenario
            Long id = 1L;
            Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
            //acao
            Optional<Lancamento> resultado = service.obterPorId(id);
            //verificacao
            Assertions.assertThat(resultado.isPresent()).isFalse();
        }

        @Test
        @DisplayName("Deve lançar erros ao validar um lancamento")
        public void deveTestarErrorsDeValidacao(){
         // cenario
            //teste de descrição
            Lancamento lancamento = new Lancamento();
            Throwable erro = Assertions.catchThrowable(() -> service.validar(lancamento));
            Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida.");
            lancamento.setDescricao("");
            erro = Assertions.catchThrowable(() -> service.validar(lancamento));
            Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida.");
            lancamento.setDescricao("Descrição valida");
            //teste de mes
            erro = Assertions.catchThrowable(() -> service.validar(lancamento));
            Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");
            lancamento.setMes(0);
            erro = Assertions.catchThrowable(() -> service.validar(lancamento));
            Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");
            lancamento.setMes(13);
            erro = Assertions.catchThrowable(() -> service.validar(lancamento));
            Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");
            //teste de ano
            lancamento.setMes(1);
            erro = Assertions.catchThrowable(() -> service.validar(lancamento));
            Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");
            lancamento.setAno(202);
            erro = Assertions.catchThrowable(() -> service.validar(lancamento));
            Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");
           //teste de usuario
            lancamento.setAno(2021);
            erro = Assertions.catchThrowable(() -> service.validar(lancamento));
            Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");
            lancamento.setUsuario(new Usuario());
            erro = Assertions.catchThrowable(() -> service.validar(lancamento));
            Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");
            //teste de valor
            lancamento.getUsuario().setId(1L);
            erro = Assertions.catchThrowable(() -> service.validar(lancamento));
            Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido.");
            lancamento.setValor(BigDecimal.ZERO);
            erro = Assertions.catchThrowable(() -> service.validar(lancamento));
            Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido.");
            lancamento.setValor(BigDecimal.valueOf(1));
            //teste de tipo
            erro = Assertions.catchThrowable(() -> service.validar(lancamento));
            Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um tipo de Lançamento.");

        }

        //para que serve o metodo criarLancamento ?




    //criando um lancamento
    private Lancamento criarLancamento() {
        return Lancamento.builder().ano(2021).mes(1).descricao("lancamento qualquer").valor(BigDecimal.valueOf(10,2)).tipo(TipoLancamento.DESPESA).status(StatusLancamento.EFETIVADO).build();
    }

    private Usuario criarUsuario() {
        return Usuario.builder().nome("usuario").email("email@email.com")
                .senha("senha").build();
    }

}
