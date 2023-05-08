package lab.info.com.finance.api.resource;

import lab.info.com.finance.api.dto.LancamentoDTO;
import lab.info.com.finance.model.entity.Lancamento;
import lab.info.com.finance.model.entity.Usuario;
import lab.info.com.finance.service.LancamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpResponse;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoResource {

    private LancamentoService service;

    public LancamentoResource(LancamentoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity salvar(@RequestBody LancamentoDTO dto){
        try {
            Lancamento lancamentoSalvo = this.service.salvar(Lancamento.builder()
                    .descricao(dto.getDescricao())
                    .mes(dto.getMes())
                    .ano(dto.getAno())
                    .valor(dto.getValor())
                    .build());
            return new ResponseEntity(lancamentoSalvo, HttpStatus.CREATED);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
