package lab.info.com.finance.model.repository;


import lab.info.com.finance.model.entity.Usuario;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UsuarioRepositoryTest {

    @Autowired
    UsuarioRepository repository;

    @Autowired
    TestEntityManager entityManager;

     @Test
     public void deveVerificarAExistenciaDeUmEmail(){
         //cenario
         Usuario usuario =criarUsuario();
         entityManager.persist(usuario);

         //acao / execucao
         boolean result = repository.existsByEmail("usuario@gmail.com");
        //verificacao
         Assertions.assertThat(result).isTrue();
     }
     @Test
     public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComOEmail(){
         //cenario

         //acao / execucao
         boolean result = repository.existsByEmail("usuario@gmail,com");
         //verificacao
         Assertions.assertThat(result).isFalse();
     }

        @Test
        public void devePersistirUmUsuarioNaBaseDeDados(){
            //cenario
            Usuario usuario = criarUsuario();
            //acao
            Usuario usuarioSalvo = repository.save(usuario);
            //verificacao
            Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
        }

    private Usuario criarUsuario() {
        return Usuario.builder().nome("usuario").email("usuario@gmail.com").senha("123456").build();
    }

    @Test
    public void deveBuscarUmUsuarioPorEmail() {

        //cenario
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario);
        //verificacao
        Assertions.assertThat(repository.findByEmail(("usuario@gmail.com"))).isPresent();
    }

    @Test
    public void deveRetornarNuloAoBuscarUmUsuarioPorEmailQuandoNaoExisteNaBase() {

        //verificacao
        Assertions.assertThat(repository.findByEmail(("usuario@gmail.com"))).isNotPresent();
    }
}
