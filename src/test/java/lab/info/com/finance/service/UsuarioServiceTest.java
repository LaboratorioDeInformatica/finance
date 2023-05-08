package lab.info.com.finance.service;


import lab.info.com.finance.exceptions.ErroAutenticacao;
import lab.info.com.finance.exceptions.RegraNegocioException;
import lab.info.com.finance.model.entity.Usuario;
import lab.info.com.finance.model.repository.UsuarioRepository;

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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UsuarioServiceTest {


    @SpyBean
    UsuarioService service;
    @MockBean
    UsuarioRepository repository;

    @Test()
    @DisplayName("Não deve lançar exceção quando não existir email cadastrado")
    public void validarEmail(){
        //cenario
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

        //acao / execucao
        Throwable exception = Assertions.catchThrowable(() ->  service.validarEmail("email@email.com"));
        //verificacao
        assertThat(exception).isNull();
    }

    @Test()
    @DisplayName("Deve lançar o erro Já existe um usuario cadastrado com esse email")
    public void validarEmailComErro(){
        //cenario
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
        //acao / execucao
        Throwable exception = Assertions.catchThrowable(() ->  service.validarEmail("email@email.com"));
        //verificacao
        assertThat(exception).isInstanceOf(RegraNegocioException.class)
                .hasMessage("Já existe um usuario cadastrado com esse email");
    }

    @Test
    @DisplayName("Deve autenticar um usuario com sucesso")
    public void autenticarUsuario() {
        //cenario
        String email = "email@email.com";
        String senha = "senha";
        Mockito.when(repository.findByEmail("email@email.com")).thenReturn(Optional.of(getUsuario()));
        //acao / execucao
        Usuario result = service.autenticar(email, senha);
        //verificacao
        assertThat(result).isNotNull();

    }


    @Test
    @DisplayName("Deve lançar erro quando não encontrar usuario cadastrado com o email informado")
    public void autenticarUsuarioComErro() {
        String email = "email@email.com";
        String senha = "senha";
        //cenario
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.empty());

        //acao / execucao
        Throwable exception = Assertions.catchThrowable(() -> service.autenticar(email, senha));

        //verificacao
        assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Usuario não encontrado para o email informado");

    }

    @Test
    @DisplayName("Deve lançar erro quando a senha não bater")
    public void autenticarUsuarioComErroSenha() {
        //cenario
        String email = "email@email.com";
        String senha = "123456";
        Usuario usuario = getUsuario();
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
        //acao / execucao
        Throwable exception = Assertions.catchThrowable(() -> service.autenticar(email, senha));
        //verificacao
        assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha inválida");
    }

    @Test
    @DisplayName("Deve salvar um usuario")
    public void salvarUsuario() {
        //cenario
        Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
        Usuario usuario = getUsuario();
        Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
        //acao / execucao
        Usuario result = service.salvarUsuario(new Usuario());
        //verificacao
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1l);
        assertThat(result.getNome()).isEqualTo("usuario");
        assertThat(result.getEmail()).isEqualTo("email@email.com");
        assertThat(result.getSenha()).isEqualTo("senha");
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar salvar um usuario com email já cadastrado")
    public void salvarUsuarioComErro() {
        //cenario
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
        //acao / execucao
        Throwable exception = Assertions.catchThrowable(() -> service.salvarUsuario(getUsuario()));
        //verificacao
        assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Já existe um usuario cadastrado com esse email");
        Mockito.verify(repository, Mockito.never()).save(getUsuario());
    }

    private Usuario getUsuario() {
        Usuario usuario = Usuario.builder().id(1l).nome("usuario").email("email@email.com").senha("senha").build();
        return usuario;
    }

}
