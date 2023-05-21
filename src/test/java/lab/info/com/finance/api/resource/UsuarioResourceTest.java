package lab.info.com.finance.api.resource;


import com.fasterxml.jackson.databind.ObjectMapper;
import lab.info.com.finance.api.dto.UsuarioDTO;
import lab.info.com.finance.exceptions.RegraNegocioException;
import lab.info.com.finance.model.entity.Usuario;
import lab.info.com.finance.service.LancamentoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import lab.info.com.finance.service.UsuarioService;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest( controllers = UsuarioResource.class )
@AutoConfigureMockMvc
public class UsuarioResourceTest {

    static String USUARIO_API = "/api/usuarios";

    @Autowired
    MockMvc mvc ;

    @MockBean
    UsuarioService service;

    @MockBean
    LancamentoService lancamentoService;

    @Test
    @DisplayName("Deve criar um usuario com sucesso.")
    public void deveAutenticarUmUsuario() throws Exception {

        String email = "email.@email.com";
        String senha = "senha";

        UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
        Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).nome("Fulano").build();
        Mockito.when(service.autenticar(dto.getEmail(), dto.getSenha())).thenReturn(usuario);

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(USUARIO_API.concat("/autenticar"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("email").value(usuario.getEmail()))
                .andExpect( jsonPath("nome").value(usuario.getNome()))
        ;

    }

    @Test
    @DisplayName("Deve retornar bad request ao obter um usuario inexistente.")
    public void deveRetornarBadRequestAoObterUmUsuarioInexistente() throws Exception {

        String email = "email.@email";
        String senha = "senha";

        UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
        Mockito.when(service.autenticar(dto.getEmail(), dto.getSenha())).thenThrow( new RuntimeException("Usuario não encontrado"));

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(USUARIO_API.concat("/autenticar"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isBadRequest())
        ;

    }

    @Test
    @DisplayName("Deve criar um usuario com sucesso.")
    public void deveCriarUmUsuario() throws Exception {

        String email = "email.@email";
        String senha = "senha";

        UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
        Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).nome("Fulano").build();
        Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenReturn(usuario);

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(USUARIO_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("email").value(usuario.getEmail()))
                .andExpect( jsonPath("nome").value(usuario.getNome()))
        ;

    }

    @Test
    @DisplayName("Deve retornar bad request ao tentar criar um usuario invalido.")
    public void deveRetornarBadRequestAoTentarCriarUmUsuarioInvalido() throws Exception {

        String email = "email.@email";
        String senha = "senha";

        UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
        Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenThrow(RegraNegocioException.class);

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(USUARIO_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isBadRequest())
        ;

    }
}
