package com.company.controller;

import com.company.dto.CategoryDTO;
import com.company.dto.JwtDTO;
import com.company.enums.ProfileRole;
import com.company.service.CategoryService;
import com.company.util.HttpHeaderUtil;
import com.company.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping("/category")
@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    // PUBLIC
    @GetMapping("")
    public ResponseEntity<List<CategoryDTO>> getListCategory() {
        List<CategoryDTO> list = categoryService.getList();
        return ResponseEntity.ok().body(list);
    }

    // SECURED
    @PostMapping("/adm")
    public ResponseEntity<?> create(@RequestBody CategoryDTO categoryDto,
                                    HttpServletRequest request) {
        HttpHeaderUtil.getId(request, ProfileRole.ADMIN);
        categoryService.create(categoryDto);
        return ResponseEntity.ok().body("SuccsessFully created");
    }

    @GetMapping("/adm")
    public ResponseEntity<List<CategoryDTO>> getList(HttpServletRequest request) {
        HttpHeaderUtil.getId(request, ProfileRole.ADMIN);
        List<CategoryDTO> list = categoryService.getListOnlyForAdmin();
        return ResponseEntity.ok().body(list);
    }


    @PutMapping("/adm/{id}")
    private ResponseEntity<?> update(@PathVariable("id") Integer id,
                                     @RequestBody CategoryDTO dto,
                                     HttpServletRequest request) {
        HttpHeaderUtil.getId(request, ProfileRole.ADMIN);
        categoryService.update(id, dto);
        return ResponseEntity.ok().body("Succsessfully updated");
    }

    @DeleteMapping("/adm/{id}")
    private ResponseEntity<?> delete(@PathVariable("id") Integer id,
                                     HttpServletRequest request) {
        HttpHeaderUtil.getId(request, ProfileRole.ADMIN);
        categoryService.delete(id);
        return ResponseEntity.ok().body("Sucsessfully deleted");
    }


}
