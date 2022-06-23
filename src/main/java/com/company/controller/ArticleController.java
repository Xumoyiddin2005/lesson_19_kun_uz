package com.company.controller;

import com.company.dto.JwtDTO;
import com.company.dto.article.ArticleCreateDTO;
import com.company.dto.article.ArticleDTO;
import com.company.dto.article.ArticleRequestDTO;
import com.company.enums.LangEnum;
import com.company.enums.ProfileRole;
import com.company.service.ArticleService;
import com.company.util.HttpHeaderUtil;
import com.company.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping("/article")
@RestController
public class ArticleController {
    @Autowired
    private ArticleService articleService;


    @PostMapping("/adm")
    public ResponseEntity<ArticleDTO> create(@RequestBody ArticleCreateDTO dto,
                                             HttpServletRequest request) {
        Integer profileId = HttpHeaderUtil.getId(request, ProfileRole.MODERATOR);
        ArticleDTO response = articleService.create(dto, profileId);
        return ResponseEntity.ok().body(response);
    }


    @PutMapping("/adm/status/{id}")
    public ResponseEntity<Void> changeStatus(@PathVariable("id") String articleId,
                                             HttpServletRequest request) {
        Integer pId = HttpHeaderUtil.getId(request, ProfileRole.PUBLISHER);
        articleService.updateByStatus(articleId, pId);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/category/{categoryKey}")
    public ResponseEntity<List<ArticleDTO>> getLast5ArticleByCategory(@PathVariable("categoryKey") String categoryKey) {
//        List<ArticleDTO> response = articleService.getLast5ArticleByCategory(categoryKey);
        List<ArticleDTO> response = articleService.getLast5ArticleByCategory3(categoryKey);
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/type/{typeKey}")
    public ResponseEntity<List<ArticleDTO>> getLast5ArticleByType(@PathVariable("typeKey") String typeKey) {
        List<ArticleDTO> response = articleService.getLast5ArticleByType(typeKey);
        return ResponseEntity.ok().body(response);
    }


    @PostMapping("/last8")
    public ResponseEntity<List<ArticleDTO>> getLast8NoyIn(@RequestBody ArticleRequestDTO dto) {
        List<ArticleDTO> response = articleService.getLat8ArticleNotIn(dto.getIdList());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleDTO> getLast5ArticleByType(@PathVariable("id") String id,
                                                            @RequestHeader(value = "Accept-Language", defaultValue = "uz") LangEnum lang) {
        ArticleDTO response = articleService.getPublishedArticleById(id, lang);
        return ResponseEntity.ok().body(response);
    }

}
