package com.company.service;

import com.company.dto.CategoryDTO;
import com.company.dto.RegionDTO;
import com.company.dto.article.ArticleCreateDTO;
import com.company.dto.article.ArticleDTO;
import com.company.dto.article.ArticleLikeDTO;
import com.company.entity.*;
import com.company.enums.ArticleStatus;
import com.company.enums.LangEnum;
import com.company.exps.ItemNotFoundException;
import com.company.mapper.ArticleShortInfo;
import com.company.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private RegionService regionService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ArticleTypeService articleTypeService;
    @Autowired
    private ArticleTagService articleTagService;
    @Autowired
    private ArticleLikeService articleLikeService;

    public ArticleDTO create(ArticleCreateDTO dto, Integer profileId) {
        ArticleEntity entity = new ArticleEntity();
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setDescription(dto.getDescription());

        RegionEntity region = regionService.get(dto.getRegionId());
        entity.setRegion(region);

        CategoryEntity category = categoryService.get(dto.getCategoryId());
        entity.setCategory(category);

        ProfileEntity moderator = new ProfileEntity();
        moderator.setId(profileId);
        entity.setModerator(moderator);
        entity.setStatus(ArticleStatus.NOT_PUBLISHED);

        articleRepository.save(entity);
        articleTypeService.create(entity, dto.getTypesList()); // type
        articleTagService.create(entity, dto.getTagList());  // tag

        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setTitle(entity.getTitle());
        articleDTO.setContent(entity.getContent());
        articleDTO.setDescription(entity.getDescription());
        //  articleDTO.setRegionEntity(entity.getRegion());
        articleDTO.setStatus(entity.getStatus());
        // articleDTO.setCategoryEntity(entity.getCategory());
        //  articleDTO.setModerator(entity.getModerator());

        return articleDTO;
    }

    public void updateByStatus(String articleId, Integer pId) {
        Optional<ArticleEntity> optional = articleRepository.findById(articleId);

        if (optional.isEmpty()) {
            throw new ItemNotFoundException("Article not found ");
        }

        ArticleEntity articleEntity = optional.get();
        if (articleEntity.getStatus().equals(ArticleStatus.NOT_PUBLISHED)) {
            /*articleEntity.setStatus(ArticleStatus.PUBLISHED);
            articleEntity.setPublishDate(LocalDateTime.now());
            articleEntity.setPublisher(new ProfileEntity(pId));*/
            articleRepository.changeStatusToPublish(articleId, pId, ArticleStatus.PUBLISHED, LocalDateTime.now());
        } else if (articleEntity.getStatus().equals(ArticleStatus.PUBLISHED)) {
            /*articleEntity.setStatus(ArticleStatus.NOT_PUBLISHED);*/
            articleRepository.changeStatusNotPublish(articleId, ArticleStatus.NOT_PUBLISHED);
        }
        //articleRepository.save(articleEntity);
    }


    //  Get Last 5 Article By Types  ordered_by_created_date
    //        (Berilgan types bo'yicha oxirgi 5ta pubished bo'lgan article ni return qiladi.)
    public List<ArticleDTO> getLast5ArticleByType(String typeKey) {
        Pageable pageable = PageRequest.of(0, 5);
        Page<ArticleEntity> articlePage = articleRepository.findLast5ByType(
                typeKey, pageable);

        List<ArticleDTO> dtoList = new LinkedList<>();
        articlePage.getContent().forEach(article -> {
            dtoList.add(shortDTOInfo(article));
        });
        return dtoList;
    }

    // 14. Get Last 5 Article By Category Key

    public List<ArticleDTO> getLast5ArticleByCategory(String categoryKey) {
        CategoryEntity category = categoryService.get(categoryKey);

        List<ArticleEntity> articleList = articleRepository.findTop5ByCategoryAndStatusAndVisibleTrueOrderByCreatedDateDesc(
                category, ArticleStatus.PUBLISHED);
        List<ArticleDTO> dtoList = new LinkedList<>();
        articleList.forEach(article -> {
            dtoList.add(shortDTOInfo(article));
        });
        return dtoList;
    }

    public List<ArticleDTO> getLast5ArticleByCategory2(String categoryKey) {
        Pageable pageable = PageRequest.of(0, 5);
        Page<ArticleEntity> articlePage = articleRepository.findLast5ByCategory(
                categoryKey, ArticleStatus.PUBLISHED, pageable);
        int n = articlePage.getTotalPages();

        List<ArticleDTO> dtoList = new LinkedList<>();
        articlePage.getContent().forEach(article -> {
            dtoList.add(shortDTOInfo(article));
        });
        return dtoList;
    }

    public List<ArticleDTO> getLast5ArticleByCategory3(String categoryKey) {
        List<ArticleShortInfo> articleList = articleRepository.findTop5ByArticleByCategory2(categoryKey);

        List<ArticleDTO> dtoList = new LinkedList<>();
        articleList.forEach(article -> {
            dtoList.add(shortDTOInfo(article));
        });
        return dtoList;
    }


    public List<ArticleDTO> getLat8ArticleNotIn(List<String> articleIdList) {
        Pageable pageable = PageRequest.of(0, 5);
        Page<ArticleEntity> articlePage = articleRepository.findLast8NotIn(articleIdList, pageable);

        List<ArticleDTO> dtoList = new LinkedList<>();
        articlePage.getContent().forEach(article -> {
            dtoList.add(shortDTOInfo(article));
        });
        return dtoList;
    }


    public ArticleDTO getPublishedArticleById(String id, LangEnum lang) {
        Optional<ArticleEntity> optional = articleRepository.getPublishedById(id);
        if (optional.isEmpty()) {
            throw new ItemNotFoundException("Article Not Found");
        }

        ArticleEntity entity = optional.get();
        ArticleDTO dto = fullDTO(entity);

        dto.setRegion(regionService.get(entity.getRegion(), lang));
        dto.setCategory(categoryService.get(entity.getCategory(), lang));

        ArticleLikeDTO likeDTO = articleLikeService.likeCountAndDislikeCount(entity.getId());
        dto.setLike(likeDTO);

//        dto.setTagList(articleTagService.getTagListNameByArticle(entity));
        return dto;
    }

    private ArticleDTO shortDTOInfo(ArticleEntity entity) {
        ArticleDTO dto = new ArticleDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setPublishDate(entity.getPublishDate());
        // TODO image
        return dto;
    }

    private ArticleDTO shortDTOInfo(ArticleShortInfo entity) {
        ArticleDTO dto = new ArticleDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setPublishDate(entity.getPublishDate());
        // TODO image
        return dto;
    }

    private ArticleDTO fullDTO(ArticleEntity entity) {
        ArticleDTO dto = new ArticleDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());
        dto.setDescription(entity.getDescription());
        dto.setSharedCount(entity.getSharedCount());
        dto.setPublishDate(entity.getPublishDate());
        dto.setViewCount(entity.getViewCount());
        return dto;
    }

}
