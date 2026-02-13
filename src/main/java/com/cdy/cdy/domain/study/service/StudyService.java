package com.cdy.cdy.domain.study.service;

import com.cdy.cdy.domain.study.dto.RequestStudy;
import com.cdy.cdy.domain.study.entity.Study;
import com.cdy.cdy.domain.study.entity.StudyImage;
import com.cdy.cdy.domain.study.repository.StudyImageRepository;
import com.cdy.cdy.domain.study.repository.StudyRepository;
import com.cdy.cdy.domain.users.entity.Users;
import com.cdy.cdy.domain.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final UserRepository userRepository;
    private final StudyRepository studyRepository;
    private final StudyImageRepository studyImageRepository;

    //스터디 작성 저장
    public void createStudy(String username,RequestStudy dto) {

        //로그인 유저 정보 뽑기
        Users users = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

        //스터디 dto->entity 변환 후 저장
        Study study = Study.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .userId(users.getId())
                .build();

        studyRepository.save(study);

        //스터디 이미지 dto->entity 변환 후 저장
        if (dto.getImageList() != null && !dto.getImageList().isEmpty()) {

            List<StudyImage> studyImageList = dto.getImageList().stream().map((ent) ->

                    StudyImage.builder()
                            .imageKey(ent.getImageKey())
                            .sortOrder(ent.getSortOrder())
                            .build()
            ).toList();
            studyImageRepository.saveAll(studyImageList);
        }
    }

}
