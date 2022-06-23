package com.company.controller;

import com.company.dto.RegionDTO;
import com.company.enums.ProfileRole;
import com.company.service.RegionService;
import com.company.util.HttpHeaderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping("/region")
@RestController
public class RegionController {
    @Autowired
    private RegionService regionService;

    // PUBLIC
    @GetMapping("")
    public ResponseEntity<List<RegionDTO>> getListRegion() {
        List<RegionDTO> list = regionService.getList();
        return ResponseEntity.ok().body(list);
    }

    // SECURE
    @PostMapping("/adm")
    public ResponseEntity<?> create(@RequestBody RegionDTO regionDto,
                                    HttpServletRequest request) {
        HttpHeaderUtil.getId(request, ProfileRole.ADMIN);
        regionService.create(regionDto);
        return ResponseEntity.ok().body("SuccsessFully created");
    }

    @GetMapping("/adm")
    public ResponseEntity<List<RegionDTO>> getlist(HttpServletRequest request) {
        HttpHeaderUtil.getId(request, ProfileRole.ADMIN);
        List<RegionDTO> list = regionService.getListOnlyForAdmin();
        return ResponseEntity.ok().body(list);
    }

    @PutMapping("/adm/{id}")
    private ResponseEntity<?> update(@PathVariable("id") Integer id,
                                     @RequestBody RegionDTO dto,
                                     HttpServletRequest request) {
        HttpHeaderUtil.getId(request, ProfileRole.ADMIN);
        regionService.update(id, dto);
        return ResponseEntity.ok().body("Succsessfully updated");
    }

    @DeleteMapping("/adm/{id}")
    private ResponseEntity<?> delete(@PathVariable("id") Integer id,
                                     HttpServletRequest request) {
        HttpHeaderUtil.getId(request, ProfileRole.ADMIN);
        regionService.delete(id);
        return ResponseEntity.ok().body("Successfully deleted");
    }


}
