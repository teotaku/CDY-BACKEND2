package com.cdy.cdy.domain.study;


import com.cdy.cdy.common.r2.ImageUrlResolver;
import com.cdy.cdy.domain.study.dto.RequestStudy;
import com.cdy.cdy.domain.study.dto.RequestStudyImage;
import com.cdy.cdy.domain.study.dto.ResponseStudy;
import com.cdy.cdy.domain.study.dto.ResponseStudyListByUser;
import com.cdy.cdy.domain.study.entity.Study;
import com.cdy.cdy.domain.study.entity.StudyImage;
import com.cdy.cdy.domain.study.repository.StudyImageRepository;
import com.cdy.cdy.domain.study.repository.StudyRepository;
import com.cdy.cdy.domain.study.repository.StudyRepositoryJDBC;
import com.cdy.cdy.domain.study.service.StudyService;
import com.cdy.cdy.domain.users.entity.Users;
import com.cdy.cdy.domain.users.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.PATH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class StudyServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    StudyRepository studyRepository;
    @Mock
    StudyImageRepository studyImageRepository;
    @Mock
    StudyRepositoryJDBC studyRepositoryJDBC;

    @Mock
    ImageUrlResolver imageUrlResolver;

    @Mock
    NamedParameterJdbcTemplate template;

    @InjectMocks
    StudyService studyService;

    private Users users;

    @BeforeEach
    void setUp() {
        users = Users.builder()
                .id(1L)
                .nickname("test")
                .username("test")
                .password("test")
                .build();

    }

    /**
     * 스터디 글 작성 시나리오
     */
    @Test
    void 유저가_스터디글_작성시_db정상등록() {

        //given

        RequestStudy requestStudy = new RequestStudy();
        requestStudy.setContent("content");
        requestStudy.setTitle("title");
        given(userRepository.findByUsername(users.getUsername()))
                .willReturn(Optional.of(users));
        ArgumentCaptor<Study> captor = ArgumentCaptor.forClass(Study.class);
        //when
        studyService.createStudy(users.getUsername(), requestStudy);

        //then
        verify(studyRepository).save(captor.capture());
        Study value = captor.getValue();
        assertThat(value.getContent()).isEqualTo(requestStudy.getContent());
        assertThat(value.getTitle()).isEqualTo(requestStudy.getTitle());
        assertThat(value.getUserId()).isEqualTo(users.getId());

    }

    @Test
    void 스터디_이미지포함_db정상등록() {

        RequestStudy requestStudy = new RequestStudy();
        requestStudy.setContent("content");
        requestStudy.setTitle("title");

        RequestStudyImage requestStudyImage = new RequestStudyImage();
        requestStudyImage.setSortOrder(1);
        requestStudyImage.setImageKey("imageKey1");

        RequestStudyImage requestStudyImage2 = new RequestStudyImage();
        requestStudyImage2.setSortOrder(2);
        requestStudyImage2.setImageKey("imageKey2");

        requestStudy.setImageList(List.of(requestStudyImage, requestStudyImage2));

        given(userRepository.findByUsername(users.getUsername()))
                .willReturn(Optional.of(users));
        ArgumentCaptor<Study> studyArgumentCaptor = ArgumentCaptor.forClass(Study.class);
        ArgumentCaptor<List<StudyImage>> studyImageArgumentCaptor = ArgumentCaptor.forClass(List.class);
        //when
        studyService.createStudy(users.getUsername(), requestStudy);

        //then

        verify(studyImageRepository).saveAll(studyImageArgumentCaptor.capture());
        List<StudyImage> studyImageList = studyImageArgumentCaptor.getValue();

        assertThat(studyImageList).hasSize(2);
        assertThat(studyImageList).extracting(StudyImage::getImageKey)
                .containsExactlyInAnyOrder("imageKey1", "imageKey2");


    }

    @Test
    void 스터디_이미지_빈리스트일시에_db호출X() {


        //given

        RequestStudy requestStudy = new RequestStudy();
        requestStudy.setContent("content");
        requestStudy.setTitle("title");
        given(userRepository.findByUsername(users.getUsername()))
                .willReturn(Optional.of(users));
        ArgumentCaptor<Study> captor = ArgumentCaptor.forClass(Study.class);
        //when
        studyService.createStudy(users.getUsername(), requestStudy);

        //then
        verify(studyImageRepository, never()).saveAll(any());

    }

    /**
     * 스터디 글 삭제
     */
    @Test
    void 삭제_작성자가_아니면_에러발생() {

        //given
        Long studyId = 1L;

        Study study = Study.builder()
                .userId(2L)
                .id(studyId)
                .build();

        given(userRepository.findByUsername(users.getUsername()))
                .willReturn(Optional.of(users));
        given(studyRepository.findById(studyId))
                .willReturn(Optional.of(study));

        //when & then

        assertThatThrownBy(() -> studyService.deleteStudy(users.getUsername(), studyId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("작성자만 삭제할 수 있습니다.");

    }

    @Test
    void soft_delete_처리() {

        //given
        Long studyId = 1L;

        Study study = Study.builder()
                .userId(users.getId())
                .id(studyId)
                .build();

        given(userRepository.findByUsername(users.getUsername()))
                .willReturn(Optional.of(users));
        given(studyRepository.findById(studyId))
                .willReturn(Optional.of(study));

        //when

        studyService.deleteStudy(users.getUsername(), studyId);

        //then
        assertThat(study.getIsDeleted()).isTrue();


    }

    /**
     *
     * 스터디 글 수정
     */
    @Test
    void 수정_작성자가_아니면_에러발생() {


        //given
        Long studyId = 1L;
        Users wrongUser = Users.builder()
                .id(2L)
                .username("wrong")
                .build();

        RequestStudy requestStudy = new RequestStudy();
        requestStudy.setContent("content");
        requestStudy.setTitle("title");

        RequestStudyImage requestStudyImage = new RequestStudyImage();
        requestStudyImage.setSortOrder(1);
        requestStudyImage.setImageKey("imageKey1");

        RequestStudyImage requestStudyImage2 = new RequestStudyImage();
        requestStudyImage2.setSortOrder(2);
        requestStudyImage2.setImageKey("imageKey2");

        Study study = Study.builder()
                .id(studyId)
                .title("original")
                .content("original")
                .userId(users.getId())
                .build();

        given(studyRepository.findById(studyId))
                .willReturn(Optional.of(study));
        given(userRepository.findByUsername(wrongUser.getUsername()))
                .willReturn(Optional.of(wrongUser));

        //when & then

        assertThatThrownBy(() -> studyService.updateStudy(wrongUser.getUsername(), requestStudy, studyId))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    void 값_변경_정상확인() {

        //given
        Long studyId = 1L;

        RequestStudy requestStudy = new RequestStudy();
        requestStudy.setContent("content");
        requestStudy.setTitle("title");

        RequestStudyImage requestStudyImage = new RequestStudyImage();
        requestStudyImage.setSortOrder(1);
        requestStudyImage.setImageKey("imageKey1");

        RequestStudyImage requestStudyImage2 = new RequestStudyImage();
        requestStudyImage2.setSortOrder(2);
        requestStudyImage2.setImageKey("imageKey2");

        requestStudy.setImageList(List.of(requestStudyImage,requestStudyImage2));

        Study study = Study.builder()
                .id(studyId)
                .title("original")
                .content("original")
                .userId(users.getId())
                .build();

        given(studyRepository.findById(studyId))
                .willReturn(Optional.of(study));
        given(userRepository.findByUsername(users.getUsername()))
                .willReturn(Optional.of(users));

        ArgumentCaptor<List<StudyImage>> studyImageListCaptor = ArgumentCaptor.forClass(List.class);



        //when

        studyService.updateStudy(users.getUsername(),requestStudy,studyId);


        //then

        verify(studyImageRepository).deleteAllByStudyId(studyId);
        verify(studyImageRepository).saveAll(studyImageListCaptor.capture());
        List<StudyImage> studyImageList = studyImageListCaptor.getValue();
        assertThat(studyImageList).extracting(StudyImage::getImageKey)
                .containsExactlyInAnyOrder("imageKey1", "imageKey2");

        assertThat(study.getContent()).isEqualTo("content");
        assertThat(study.getTitle()).isEqualTo("title");

    }

    /**
     *
     * 스터디 단건 조회
     */

    @Test
    void 스터디_아이디값으로_조회() {


        //given

        Study study = Study.builder()
                .id(1L)
                .title("title")
                .content("content")
                .isDeleted(false)
                .build();

        given(studyRepository.findById(study.getId()))
                .willReturn(Optional.of(study));



        //when

        ResponseStudy responseStudy = studyService.findById(study.getId());

        //then

        assertThat(responseStudy.getContent()).isEqualTo(study.getContent());

    }

    @Test
    void 스터디_제목_내용_사진_작성날짜_정상반환() {


        //given

        Study study = Study.builder()
                .id(1L)
                .title("title")
                .content("content")
                .isDeleted(false)
                .build();

        LocalDateTime createdAt = study.getCreatedAt();
        LocalDateTime updatedAt = study.getUpdatedAt();


        StudyImage studyImage = StudyImage.builder()
                .imageKey("imageKey")
                .studyId(study.getId())
                .sortOrder(1)
                .build();

        StudyImage studyImage2 = StudyImage.builder()
                .imageKey("imageKey2")
                .studyId(study.getId())
                .sortOrder(2)
                .build();


        given(studyRepository.findById(study.getId()))
                .willReturn(Optional.of(study));

        given(studyImageRepository.findByStudyId(study.getId()))
                .willReturn(List.of(studyImage,studyImage2));

        given(imageUrlResolver.toPresignedUrl(anyString()))
                .willReturn("imageUrl");

        //when

        ResponseStudy responseStudy = studyService.findById(study.getId());

        //then

        assertThat(responseStudy.getContent()).isEqualTo(study.getContent());
        assertThat(responseStudy.getTitle()).isEqualTo(study.getTitle());
        assertThat(responseStudy.getId()).isEqualTo(study.getId());
        assertThat(responseStudy.getCreatedAt()).isEqualTo(createdAt);
        assertThat(responseStudy.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(responseStudy.getStudyImageList().get(0).getImageUrl()).isEqualTo("imageUrl");
        assertThat(responseStudy.getStudyImageList()).hasSize(2);

    }

    @Test
    void soft_delete된_스터디는_조회x() {

        //given

        Study study = Study.builder()
                .id(1L)
                .title("title")
                .content("content")
                .isDeleted(true)
                .build();

        given(studyRepository.findById(study.getId()))
                .willReturn(Optional.of(study));


        //when & then

        assertThatThrownBy(() -> studyService.findById(study.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("삭제된 스터디");

    }

    /**
     * 유저의 스터디 전체 목록 조회
     * 페이징 처리
     */
    @Test
    void 스터디_유저프로필_스터디이미지_key값을_url로_반환() {


        //given
        PageRequest pageRequest = PageRequest.of(0, 5);


        ResponseStudyListByUser responseStudyListByUser = ResponseStudyListByUser.builder()
                .firstImageUrl("imageKey")
                .userProfileImageUrl("imageKey")
                .build();

        given(userRepository.findByUsername(users.getUsername()))
                .willReturn(Optional.of(users));
        given(studyRepositoryJDBC.findByUser(users.getId(), pageRequest))
                .willReturn(List.of(responseStudyListByUser));

        given(imageUrlResolver.toPresignedUrl(any()))
                .willReturn("imageUrl");
        given(studyRepository.findTotalCountByUserId(users.getId()))
                .willReturn(1L);

        //when
        Page<ResponseStudyListByUser> result = studyService.findByUser(users.getUsername(), pageRequest);

        //then

        assertThat(responseStudyListByUser.getFirstImageUrl()).isEqualTo("imageUrl");
        assertThat(responseStudyListByUser.getUserProfileImageUrl()).isEqualTo("imageUrl");
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1L);

    }

}


