package com.mnesa.backend.modules.opportunity.service;

import com.mnesa.backend.common.exception.ResourceNotFoundException;
import com.mnesa.backend.modules.opportunity.domain.Tag;
import com.mnesa.backend.modules.opportunity.dto.TagResponse;
import com.mnesa.backend.modules.opportunity.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public List<TagResponse> getTags(UUID userId) {
        return tagRepository.findAllByUserIdOrderByNameAsc(userId).stream()
                .map(t -> new TagResponse(t.getId(), t.getName(), t.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional
    public TagResponse createTag(UUID userId, String name) {
        String normalized = name.trim().toLowerCase();
        Tag tag = tagRepository.findByUserIdAndName(userId, normalized)
                .orElseGet(() -> tagRepository.save(Tag.builder().userId(userId).name(normalized).build()));
        return new TagResponse(tag.getId(), tag.getName(), tag.getCreatedAt());
    }

    @Transactional
    public void deleteTag(UUID userId, UUID id) {
        Tag tag = tagRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found: " + id));
        tagRepository.delete(tag);
        log.info("Tag deleted: id={} userId={}", id, userId);
    }
}
