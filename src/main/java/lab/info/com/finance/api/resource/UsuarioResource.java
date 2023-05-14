package lab.info.com.finance.api.resource;

import lab.info.com.finance.api.dto.UsuarioDTO;
import lab.info.com.finance.model.entity.Usuario;
import lab.info.com.finance.service.LancamentoService;
import lab.info.com.finance.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("api/usuarios")
public class UsuarioResource {

    private UsuarioService service;

    private LancamentoService lancamentoService;


    @Autowired
   public UsuarioResource(UsuarioService service, LancamentoService lancamentoService) {
        this.service = service;
        this.lancamentoService = lancamentoService;
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

    @GetMapping("{id}/saldo")
    public ResponseEntity obterSaldo(@PathVariable Long id) {

        service.obterPorId(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
        return ResponseEntity.ok(saldo);
    }

}
