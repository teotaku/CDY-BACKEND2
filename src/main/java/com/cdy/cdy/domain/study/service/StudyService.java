package com.cdy.cdy.domain.study.service;

import com.cdy.cdy.common.r2.ImageUrlResolver;
import com.cdy.cdy.domain.study.dto.*;
import com.cdy.cdy.domain.study.entity.Study;
import com.cdy.cdy.domain.study.entity.StudyImage;
import com.cdy.cdy.domain.study.repository.StudyImageRepository;
import com.cdy.cdy.domain.study.repository.StudyRepository;
import com.cdy.cdy.domain.study.repository.StudyRepositoryJDBC;
import com.cdy.cdy.domain.users.entity.Users;
import com.cdy.cdy.domain.users.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final UserRepository userRepository;
    private final StudyRepository studyRepository;
    private final StudyRepositoryJDBC studyRepositoryJDBC;
    private final StudyImageRepository studyImageRepository;
    private final ImageUrlResolver imageUrlResolver;


    //스터디 작성 저장
    @Transactional
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

            List<StudyImage> studyImageList = dto.getImageList().stream().map((imageDto) ->

                    StudyImage.builder()
                            .imageKey(imageDto.getImageKey())
                            .sortOrder(imageDto.getSortOrder())
                            .build()
            ).toList();
            studyImageRepository.saveAll(studyImageList);
        }
    }
    //스터디 글 삭제처리(soft delete 처리)
    @Transactional
    public void deleteStudy(String username,Long studyId) {

        Users users = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자"));


        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 스터디"));

        if (study.getUserId() != users.getId()) {
            throw new IllegalArgumentException("작성자만 삭제할 수 있습니다.");
        }
        //삭제 메서드호출(컬럼 is_deleted 상태만 변경)
        study.setIsDeleted();
    }

    /**
     *
     * RequestStudy dto의 StudyImage dto에 기본값 ArrayList를 주입해서
     * null이 들어오는 상황을 제거,
     * 빈 리스트로 들어오면 전체 삭제 로직.
     */
    @Transactional
    public void updateStudy(String username, RequestStudy dto,Long studyId) {

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 스터디"));

        Users users = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자"));

        if (!study.getUserId().equals(users.getId())) {
            throw new IllegalArgumentException("작성자만 수정할 수 있습니다..");
        }

        //dto값 적용시켜서 엔티티값 변화
        study.update(dto);
        //레포지에 변경된 값 저장
        studyRepository.save(study);

                //스터디 ID에 해당하는 기존 스터디이미지들 전체 삭제
            studyImageRepository.deleteAllByStudyId(studyId);
        //스터디이미지 saveall 저장을 위한 arraylist 초기화
        List<StudyImage> studyImageList = new ArrayList<>();


        //dto의 이미지 리스트에 맞게 for문으로 dto - > studyimage 엔티티 변환과정
            for (int i = 0; i < dto.getImageList().size(); i++) {
                RequestStudyImage requestStudyImage = dto.getImageList().get(i);
                StudyImage studyImage = StudyImage.builder()
                        .studyId(studyId)
                        .sortOrder(requestStudyImage.getSortOrder())
                        .imageKey(requestStudyImage.getImageKey())
                        .build();
                studyImageList.add(studyImage);

            }
            //위에 for문에서 넣은 studyImagelist를 전체 저장하는 로직
        studyImageRepository.saveAll(studyImageList);

        }

    /**
     * 스터디 단건조회
     * 스터디 아이디를 파라미터로 받고 해당 스터디 조회
     */

    public ResponseStudy findById(Long studyId) {


        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 스터디"));

        if (study.getIsDeleted().equals(true)) {
            throw new EntityNotFoundException("삭제된 스터디");
        }

        List<StudyImage> studyImageList = studyImageRepository.findByStudyId(studyId);
        List<ResponseStudyImage> responseStudyImages = studyImageList.stream().map(ent ->

                ResponseStudyImage.builder()
                        .imageUrl(imageUrlResolver.toPresignedUrl(ent.getImageKey()))
                        .sortOrder(ent.getSortOrder())
                        .build()
        ).toList();


        ResponseStudy responseStudy = ResponseStudy.builder()
                .id(studyId)
                .title(study.getTitle())
                .content(study.getContent())
                .studyImageList(responseStudyImages)
                .createdAt(study.getCreatedAt())
                .updatedAt(study.getUpdatedAt())
                .build();


        return responseStudy;
    }

    /**
     * 작성자의 스텅디 조회 (페이징처리)
     * 작성자의 프로필사진(imageUrl) , 작성날짜 , 제목,내용, 첫번째사진 반환
     */

    public Page<ResponseStudyListByUser> findByUser(String username, Pageable pageable) {

        Users users = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자"));

        List<ResponseStudyListByUser> studyListByUsers = studyRepositoryJDBC.findByUser(users.getId(), pageable);


        studyListByUsers.stream().forEach(dto ->
                {
                    dto.setFirstImageUrl(imageUrlResolver.toPresignedUrl(dto.getFirstImageUrl()));
                    dto.setUserProfileImageUrl(imageUrlResolver.toPresignedUrl(dto.getUserProfileImageUrl()));
                }
        );

        Long totalCount = studyRepository.findTotalCountByUserId(users.getId());

        return new PageImpl<>(studyListByUsers, pageable, totalCount);


    }
}

