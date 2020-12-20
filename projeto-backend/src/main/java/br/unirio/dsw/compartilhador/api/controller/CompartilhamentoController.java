package br.unirio.dsw.compartilhador.api.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import br.unirio.dsw.compartilhador.api.model.ItemCompartilhado;
import br.unirio.dsw.compartilhador.api.model.TipoItemCompartilhado;
import br.unirio.dsw.compartilhador.api.utils.spring.PageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.unirio.dsw.compartilhador.api.model.Compartilhamento;
import br.unirio.dsw.compartilhador.api.model.Usuario;
import br.unirio.dsw.compartilhador.api.repository.ItemCompartilhadoRepository;
import br.unirio.dsw.compartilhador.api.repository.CompartilhamentoRepository;
import br.unirio.dsw.compartilhador.api.repository.UsuarioRepository;
import br.unirio.dsw.compartilhador.api.utils.spring.ControllerResponse;
import br.unirio.dsw.compartilhador.api.utils.spring.ResponseData;
import lombok.Data;

/**
 * Controlador com as ações de Compartilhamentos
 *
 * @author User
 */
@RestController
@RequestMapping("/api/compartilhamento")
@CrossOrigin(origins = "*")
public class CompartilhamentoController
{
    private static final Logger log = LoggerFactory.getLogger(CompartilhamentoController.class);

    @Autowired
    private UsuarioRepository usuarioRepositorio;

    @Autowired
    private ItemCompartilhadoRepository itemRepositorio;

    @Autowired
    private CompartilhamentoRepository compartilhamentoRepositorio;

    /**
     * Ação que lista os Compartilhamentos de um item
     */
    @GetMapping(value = "/lista")
    public ResponseEntity<ResponseData> list(@RequestParam Long itemId)
    {
        log.info("Listando compartilhamentos do item: " + itemId);
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (email == null)
            return ControllerResponse.fail("Não há um usuário logado no sistema.");

        Usuario usuario = usuarioRepositorio.findByEmail(email);

        if (usuario == null)
            return ControllerResponse.fail("Não foi possível recuperar os dados do usuário a partir das credenciais.");

        List<Compartilhamento> compartilhamentos = compartilhamentoRepositorio.findByItemId(itemId);

        List<CompartilhamentoDTO> result = new ArrayList<CompartilhamentoDTO>();

        compartilhamentos.forEach(compartilhamento -> {
            CompartilhamentoDTO dto = new CompartilhamentoDTO();
            dto.setId(compartilhamento.getId());
            dto.setData_registro(compartilhamento.getDataRegistro());
            dto.setData_atualizacao(compartilhamento.getDataAtualizacao());
            dto.setUsuario_nome(compartilhamento.getUsuario().getNome());
            dto.setData_inicio(compartilhamento.getDataInicio());
            dto.setData_termino(compartilhamento.getDataTermino());
            if(compartilhamento.isCanceladoDono()){
                dto.setStatus("Cancelado pelo dono");
            }else if(compartilhamento.isCanceladoUsuario()){
                dto.setStatus("Cancelado pelo usuário");
            }else if(compartilhamento.isRejeitado()){
                dto.setStatus("Rejeitado");
            }else if(compartilhamento.isAceito()){
                dto.setStatus("Aceito");
            }else{
                dto.setStatus("Aberto");
            }
            result.add(dto);
        });

        return ControllerResponse.success(result);
    }

    /**
     * Ação que lista os Compartilhamentos recebidos por um usuário
     */
    @GetMapping(value = "/listaUsuario")
    public ResponseEntity<ResponseData> listUsuario(@RequestParam int page, @RequestParam int per_page, @RequestParam String filter)
    {

        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (email == null)
            return ControllerResponse.fail("Não há um usuário logado no sistema.");

        Usuario usuario = usuarioRepositorio.findByEmail(email);

        if (usuario == null)
            return ControllerResponse.fail("Não foi possível recuperar os dados do usuário a partir das credenciais.");

        log.info("Listando compartilhamentos do usuario: " + usuario.getNome());

        Pageable pageable = PageRequest.of(page-1, per_page);
        Page<Compartilhamento> compartilhamentos;
        if(filter.equals("")){
            compartilhamentos = compartilhamentoRepositorio.findByUsuarioId(usuario.getId(), pageable);
        }else{
            compartilhamentos = compartilhamentoRepositorio.findByUsuarioId(usuario.getId(), pageable, filter);
        }

        PageDTO<CompartilhamentoDTO> result = new PageDTO<CompartilhamentoDTO>(compartilhamentos.getTotalElements(), page, per_page);

        compartilhamentos.forEach(compartilhamento -> {
            CompartilhamentoDTO dto = new CompartilhamentoDTO();
            dto.setId(compartilhamento.getId());
            dto.setUsuario_nome(compartilhamento.getItem().getUsuario().getNome());
            dto.setData_inicio(compartilhamento.getDataInicio());
            dto.setItem_nome(compartilhamento.getItem().getNome());
            dto.setData_termino(compartilhamento.getDataTermino());
            if(compartilhamento.isCanceladoDono()){
                dto.setStatus("Cancelado pelo dono");
            }else if(compartilhamento.isCanceladoUsuario()){
                dto.setStatus("Cancelado pelo usuário");
            }else if(compartilhamento.isRejeitado()){
                dto.setStatus("Rejeitado");
            }else if(compartilhamento.isAceito()){
                dto.setStatus("Aceito");
            }else{
                dto.setStatus("Aberto");
            }
            result.add(dto);
        });

        return ControllerResponse.success(result);
    }

    /**
     * Ação que retorna quantos compartilhamentos abertos um usuário tem
     */
    @GetMapping(value = "/quantidadeAberto")
    public ResponseEntity<ResponseData> quantidadeAberto()
    {

        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (email == null)
            return ControllerResponse.fail("Não há um usuário logado no sistema.");

        Usuario usuario = usuarioRepositorio.findByEmail(email);

        if (usuario == null)
            return ControllerResponse.fail("Não foi possível recuperar os dados do usuário a partir das credenciais.");

        log.info("Contando compartilhamentos abertos: " + usuario.getNome());

        long quantidade = compartilhamentoRepositorio.findByUsuarioIdAndAberto(usuario.getId());

        return ControllerResponse.success(quantidade);
    }

    @PutMapping(value = "/novo")
    public ResponseEntity<ResponseData> novo(@RequestBody NovoCompartilhamentoForm form, BindingResult result)
    {
        log.info("Criando um novo compartilhamento: {}", form.toString());
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (email == null)
            return ControllerResponse.fail("message", "Não há um usuário logado no sistema.");

        Usuario usuario = usuarioRepositorio.findByEmail(email);

        if (usuario == null)
            return ControllerResponse.fail("message", "Não foi possível recuperar os dados do usuário a partir das credenciais.");

        if(form.getEmail().length() == 0)
            return ControllerResponse.fail("message", "O email não pode ficar vazio.");

        usuario = usuarioRepositorio.findByEmail(form.getEmail());

        if (usuario == null)
            return ControllerResponse.fail("message", "Usuário não cadastrado.");

        if (form.getDataInicio().compareTo(form.getDataTermino()) > 0)
            return ControllerResponse.fail("message", "A data de término não pode ser anterior a data de início.");

        ItemCompartilhado item = itemRepositorio.findByItemId(form.getItemId());

        if (item == null)
            return ControllerResponse.fail("message", "Item não cadastrado.");

        Compartilhamento compartilhamento = new Compartilhamento();

        compartilhamento.setUsuario(usuario);
        compartilhamento.setDataInicio(form.getDataInicio());
        compartilhamento.setDataTermino(form.getDataTermino());
        compartilhamento.setItem(item);
        compartilhamentoRepositorio.save(compartilhamento);
        return ControllerResponse.success();
    }

    /**
     * Ação que aceita um compartilhamento
     */
    @PostMapping(value = "/aceitar/{id}")
    public ResponseEntity<ResponseData> aceita(@PathVariable("id") long id)
    {
        log.info("Aceitando um compartilhamento: ", id);
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (email == null)
            return ControllerResponse.fail("nome", "Não há um usuário logado no sistema.");

        Usuario usuario = usuarioRepositorio.findByEmail(email);

        if (usuario == null)
            return ControllerResponse.fail("nome", "Não foi possível recuperar os dados do usuário a partir das credenciais.");

        Compartilhamento compartilhamento = compartilhamentoRepositorio.findByCompartilhamentoId(id);

        if (compartilhamento == null)
            return ControllerResponse.fail("message", "O compartilhamento não foi encontrado.");

        if (compartilhamento.isCanceladoDono() || compartilhamento.isCanceladoUsuario() || compartilhamento.isAceito() || compartilhamento.isRejeitado())
            return ControllerResponse.fail("message", "Status inválido.");

        if (compartilhamento.getUsuario().getId() != usuario.getId())
            return ControllerResponse.fail("message", "Você não tem permissão para isso.");

        compartilhamento.setAceito(true);
        compartilhamentoRepositorio.save(compartilhamento);
        return ControllerResponse.success();
    }

    /**
     * Ação que rejeita um compartilhamento
     */
    @PostMapping(value = "/rejeitar/{id}")
    public ResponseEntity<ResponseData> rejeita(@PathVariable("id") long id)
    {
        log.info("Rejeitando um compartilhamento: ", id);
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (email == null)
            return ControllerResponse.fail("nome", "Não há um usuário logado no sistema.");

        Usuario usuario = usuarioRepositorio.findByEmail(email);

        if (usuario == null)
            return ControllerResponse.fail("nome", "Não foi possível recuperar os dados do usuário a partir das credenciais.");

        Compartilhamento compartilhamento = compartilhamentoRepositorio.findByCompartilhamentoId(id);

        if (compartilhamento == null)
            return ControllerResponse.fail("message", "O compartilhamento não foi encontrado.");

        if (compartilhamento.isCanceladoDono() || compartilhamento.isCanceladoUsuario() || compartilhamento.isRejeitado())
            return ControllerResponse.fail("message", "Status inválido.");

        if (compartilhamento.getUsuario().getId() != usuario.getId())
            return ControllerResponse.fail("message", "Você não tem permissão para isso.");

        if (compartilhamento.isAceito()){
            compartilhamento.setCanceladoUsuario(true);
        }else{
            compartilhamento.setRejeitado(true);
        }
        compartilhamentoRepositorio.save(compartilhamento);
        return ControllerResponse.success();
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<ResponseData> remove(@PathVariable("id") long id)
    {
        log.info("Removendo um compartilhamento: {}", id);
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (email == null)
            return ControllerResponse.fail("Não há um usuário logado no sistema.");

        Usuario usuario = usuarioRepositorio.findByEmail(email);

        if (usuario == null)
            return ControllerResponse.fail("Não foi possível recuperar os dados do usuário a partir das credenciais.");

        Compartilhamento compartilhamento = compartilhamentoRepositorio.findByCompartilhamentoId(id);

        if (compartilhamento == null)
            return ControllerResponse.fail("Compartilhamento não existente.");

        if (!compartilhamento.getItem().getUsuario().getEmail().equalsIgnoreCase(email)){
            return ControllerResponse.fail("Você não tem permissão para cancelar esse compartilhamento.");
        }

        if(compartilhamento.isCanceladoDono())
            return ControllerResponse.fail("Você já cancelou este compartilhamento.");

        compartilhamento.setCanceladoDono(true);
        compartilhamentoRepositorio.save(compartilhamento);
        return ControllerResponse.success();
    }

}

/**
 * Classe que representa um Compartilhamento para o lado cliente
 *
 * @author User
 */
@Data class CompartilhamentoDTO
{
    private long id;

    private Date data_registro;

    private Date data_atualizacao;

    private String usuario_nome;

    private LocalDate data_inicio;

    private LocalDate data_termino;

    private String status;

    private String item_nome;
}

/**
 * Formulário para registrar um novo compartilhamento
 *
 * @author User
 */
@Data class NovoCompartilhamentoForm
{
    private String email;

    private LocalDate dataInicio;

    private LocalDate dataTermino;

    private Long itemId;
}