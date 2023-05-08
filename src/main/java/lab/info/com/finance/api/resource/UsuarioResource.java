package lab.info.com.finance.api.resource;

import lab.info.com.finance.api.dto.UsuarioDTO;
import lab.info.com.finance.model.entity.Usuario;
import lab.info.com.finance.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/usuarios")
public class UsuarioResource {

    private UsuarioService service;

    @Autowired
   public UsuarioResource(UsuarioService service) {
        this.service = service;
    }
    @PostMapping
    public ResponseEntity salvar(@RequestBody UsuarioDTO dto) {

        Usuario usuario = Usuario.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(dto.getSenha())
                .build();

        try {
           Usuario usuarioSalvo = service.salvarUsuario(usuario);
           return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/autenticar")
    public ResponseEntity autenticar(@RequestBody UsuarioDTO dto) {
        try {
            Usuario usuarioAutenticado = service.autenticar(dto.getEmail(), dto.getSenha());
            return ResponseEntity.ok(usuarioAutenticado);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
