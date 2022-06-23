package com.company.controller;

import com.company.dto.ProfileDTO;
import com.company.enums.ProfileRole;
import com.company.service.ProfileService;
import com.company.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/profile")
@RestController
public class ProfileController {
    @Autowired
    private ProfileService profileService;

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody ProfileDTO profileDto,
                                    @RequestHeader("Authorization") String jwt) {
        JwtUtil.decode(jwt, ProfileRole.ADMIN);
        ProfileDTO dto = profileService.create(profileDto);
        return ResponseEntity.ok().body(dto);
    }

    @GetMapping("")
    public ResponseEntity<List<ProfileDTO>> getProfileList(@RequestHeader("Authorization") String jwt) {
        JwtUtil.decode(jwt, ProfileRole.ADMIN);
        List<ProfileDTO> list = profileService.getList();
        return ResponseEntity.ok().body(list);
    }

    @PutMapping("/detail")
    public ResponseEntity<?> update(@RequestBody ProfileDTO dto,
                                    @RequestHeader("Authorization") String jwt) {
        Integer profileId = JwtUtil.decode(jwt);
        profileService.update(profileId, dto);
        return ResponseEntity.ok().body("Sucsessfully updated");
    }

    @PutMapping("/{id}")
    private ResponseEntity<?> update(@PathVariable("id") Integer id, @RequestBody ProfileDTO dto,
                                     @RequestHeader("Authorization") String jwt) {
        JwtUtil.decode(jwt, ProfileRole.ADMIN);
        profileService.update(id, dto);
        return ResponseEntity.ok().body("Succsessfully updated");
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<?> delete(@PathVariable("id") Integer id,
                                     @RequestHeader("Authorization") String jwt) {
        JwtUtil.decode(jwt, ProfileRole.ADMIN);
        profileService.delete(id);
        return ResponseEntity.ok().body("Sucsessfully deleted");
    }


}
