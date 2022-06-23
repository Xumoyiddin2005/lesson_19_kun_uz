package com.company.controller;

import com.company.dto.article.TypesDTO;
import com.company.dto.RegionDTO;
import com.company.enums.LangEnum;
import com.company.enums.ProfileRole;
import com.company.service.TypesService;
import com.company.util.HttpHeaderUtil;
import com.company.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping("/types")
@RestController
public class ArticleTypeController {
    @Autowired
    private TypesService typesService;

    // PUBLIC
    @GetMapping("/public")
//    public ResponseEntity<List<ArticleTypeDTO>> getArticleList(@RequestParam(value = "lang",defaultValue = "uz") LangEnum lang) {
    public ResponseEntity<List<TypesDTO>> getArticleList(@RequestHeader(value = "Accept-Language", defaultValue = "uz")
                                                         LangEnum lang) {
        List<TypesDTO> list = typesService.getList(lang);
        return ResponseEntity.ok().body(list);
    }

    // SECURED

    @PostMapping("/adm")
    public ResponseEntity<?> create(@RequestBody TypesDTO typesDto, HttpServletRequest request) {
        HttpHeaderUtil.getId(request, ProfileRole.ADMIN);
        typesService.create(typesDto);
        return ResponseEntity.ok().body("SuccsessFully created");
    }


    @GetMapping("/adm")
    public ResponseEntity<List<TypesDTO>> getList(@RequestHeader("Authorization") String jwt) {
        JwtUtil.decode(jwt, ProfileRole.ADMIN);
        List<TypesDTO> list = typesService.getListOnlyForAdmin();
        return ResponseEntity.ok().body(list);
    }


    @PutMapping("/adm/{id}")
    private ResponseEntity<?> update(@PathVariable("id") Integer id,
                                     @RequestBody RegionDTO dto,
                                     @RequestHeader("Authorization") String jwt) {
        JwtUtil.decode(jwt, ProfileRole.ADMIN);
        typesService.update(id, dto);
        return ResponseEntity.ok().body("Succsessfully updated");
    }

    @DeleteMapping("/adm/{id}")
    private ResponseEntity<?> delete(@PathVariable("id") Integer id,
                                     @RequestHeader("Authorization") String jwt) {
        JwtUtil.decode(jwt, ProfileRole.ADMIN);
        typesService.delete(id);
        return ResponseEntity.ok().body("Sucsessfully deleted");
    }


    @GetMapping("/pagination")
    public ResponseEntity<PageImpl> getPagination(@RequestParam(value = "page", defaultValue = "1") int page,
                                                  @RequestParam(value = "size", defaultValue = "5") int size) {
        PageImpl response = typesService.pagination(page, size);
        return ResponseEntity.ok().body(response);
    }


}
