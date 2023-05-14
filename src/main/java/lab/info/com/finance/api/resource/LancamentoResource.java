package lab.info.com.finance.api.resource;

import lab.info.com.finance.api.dto.LancamentoDTO;
import lab.info.com.finance.exceptions.RegraNegocioException;
import lab.info.com.finance.model.entity.Lancamento;
import lab.info.com.finance.model.entity.Usuario;
import lab.info.com.finance.model.enums.StatusLancamento;
import lab.info.com.finance.model.enums.TipoLancamento;
import lab.info.com.finance.service.LancamentoService;
import lab.info.com.finance.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoResource {

    private LancamentoService service;

    private  UsuarioService usuarioService;

    public LancamentoResource(LancamentoService service, UsuarioService usuarioService){
        this.service = service;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity salvar(@RequestBody LancamentoDTO dto){
        try {
            Lancamento lancamentoSalvo = this.service.salvar(converter(dto));
            return new ResponseEntity(lancamentoSalvo, HttpStatus.CREATED);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable Long id, @RequestBody LancamentoDTO dto){
        try {
            Lancamento lancamento = service.obterPorId(id)
                    .map( lanc -> {
                        try {
                            dto.setId(id);
                            return service.atualizar(converter(dto));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }).orElseThrow(() -> new RegraNegocioException("Lancamento não encontrado na base de dados"));
            return ResponseEntity.ok(lancamento);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity deletar(@PathVariable Long id){
        try {
            Lancamento lancamento = service.obterPorId(id)
                    .orElseThrow(() -> new RegraNegocioException("Lancamento não encontrado na base de dados"));
            service.deletar(lancamento);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity buscar(
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam(value = "tipo", required = false) TipoLancamento tipo,
            @RequestParam(value = "usuario") Long idUsuario
    ){
        Lancamento lancamentoFiltro = new Lancamento();
        lancamentoFiltro.setDescricao(descricao);
        lancamentoFiltro.setMes(mes);
        lancamentoFiltro.setAno(ano);
        lancamentoFiltro.setTipo(tipo);

        Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
        if(!usuario.isPresent()){
            return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Usuário não encontrado para o Id informado");
        }else{
            lancamentoFiltro.setUsuario(usuario.get());
        }

        try {
            java.util.List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
            return ResponseEntity.ok(lancamentos);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}/atualiza-status")
    public ResponseEntity atualizarStatus(@PathVariable Long id, @RequestBody LancamentoDTO dto){
        try {
            Lancamento lancamento = service.obterPorId(id)
                    .map( lanc -> {
                        try {
                            StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus().toString());
                            if(statusSelecionado == null){
                                throw new RegraNegocioException("Status inválido");
                            }
                            lanc.setStatus(statusSelecionado);
                            return service.atualizarStatus(lanc, statusSelecionado);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }).orElseThrow(() -> new RegraNegocioException("Lancamento não encontrado na base de dados"));
            return ResponseEntity.ok(lancamento);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    private Lancamento converter(LancamentoDTO dto) {

       Usuario usuario = usuarioService
                .obterPorId(dto.getUsuario())
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o Id informado"));

        return Lancamento.builder()
                .id(dto.getId())
                .descricao(dto.getDescricao())
                .mes(dto.getMes())
                .ano(dto.getAno())
                .valor(dto.getValor())
                .usuario(usuario)
                .status(dto.getStatus())
                .tipo(dto.getTipo())
                .build();
    }


}
