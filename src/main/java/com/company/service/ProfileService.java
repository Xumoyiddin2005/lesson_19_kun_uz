package com.company.service;

import com.company.dto.ProfileDTO;
import com.company.entity.ProfileEntity;
import com.company.enums.ProfileStatus;
import com.company.exps.AlreadyExist;
import com.company.exps.AlreadyExistPhone;
import com.company.exps.BadRequestException;
import com.company.exps.ItemNotFoundException;
import com.company.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {
    @Autowired
    private ProfileRepository profileRepository;

    public ProfileDTO create(ProfileDTO profileDto) {

        Optional<ProfileEntity> entity = profileRepository.findByEmail(profileDto.getEmail());
        if (entity.isPresent()) {
            throw new AlreadyExistPhone("Already exist phone");
        }

        isValid(profileDto);

        ProfileEntity profile = new ProfileEntity();
        profile.setName(profileDto.getName());
        profile.setSurname(profileDto.getSurName());
        profile.setEmail(profileDto.getEmail());
        profile.setRole(profileDto.getRole());
        profile.setPassword(profileDto.getPassword());
        profile.setStatus(ProfileStatus.ACTIVE);
        profileRepository.save(profile);

        profileDto.setId(profile.getId());
        profile.setPassword(null);

        return profileDto;
    }

    public List<ProfileDTO> getList() {
        Iterable<ProfileEntity> all = profileRepository.findAllByVisible(true);
        List<ProfileDTO> dtoList = new LinkedList<>();
        all.forEach(profileEntity -> {
            ProfileDTO dto = new ProfileDTO();
            dto.setId(profileEntity.getId());
            dto.setName(profileEntity.getName());
            dto.setSurName(profileEntity.getSurname());
            dto.setEmail(profileEntity.getEmail());
            dtoList.add(dto);
        });
        return dtoList;
    }

    public List<ProfileDTO> listByVisibility(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProfileEntity> all = profileRepository.findAllByVisible(Boolean.TRUE, pageable);

        List<ProfileDTO> dtoList = new LinkedList<>();
        all.forEach(profileEntity -> {
            ProfileDTO dto = new ProfileDTO();
            dto.setId(profileEntity.getId());
            dto.setName(profileEntity.getName());
            dto.setSurName(profileEntity.getSurname());
            dto.setEmail(profileEntity.getEmail());
            dtoList.add(dto);
        });
        return dtoList;
    }

    public void update(Integer pId, ProfileDTO dto) {

        Optional<ProfileEntity> profile = profileRepository.findById(pId);

        if (profile.isEmpty()) {
            throw new ItemNotFoundException("not found profile");
        }

        isValidUpdate(dto);

        ProfileEntity entity = profile.get();

        entity.setName(dto.getName());
        entity.setSurname(dto.getSurName());
        entity.setEmail(dto.getEmail());
        profileRepository.save(entity);

    }


    public void delete(Integer id) {
        Optional<ProfileEntity> profile = profileRepository.findById(id);
        if (profile.isEmpty()) {
            throw new ItemNotFoundException("not found profile");
        }
        if (!profile.get().getVisible()) {
            throw new AlreadyExist("IsVisible False edi");
        }

        profile.get().setVisible(Boolean.FALSE);
        profileRepository.save(profile.get());
    }

    private void isValidUpdate(ProfileDTO dto) {

        if (dto.getName() == null || dto.getName().length() < 3) {
            throw new BadRequestException("wrong name");
        }

        if (dto.getSurName() == null || dto.getSurName().length() < 4) {
            throw new BadRequestException("surname required.");
        }

        if (dto.getEmail() == null || dto.getEmail().length() < 3) {
            throw new BadRequestException("email required.");
        }


    }

    private void isValid(ProfileDTO dto) {

        if (dto.getName() == null || dto.getName().length() < 3) {
            throw new BadRequestException("wrong name");
        }

        if (dto.getSurName() == null || dto.getSurName().length() < 4) {
            throw new BadRequestException("surname required.");
        }

        if (dto.getEmail() == null || dto.getEmail().length() < 3) {
            throw new BadRequestException("email required.");
        }

    }
}

