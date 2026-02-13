package com.cdy.cdy.domain.study;


import com.cdy.cdy.domain.study.dto.RequestStudy;
import com.cdy.cdy.domain.study.entity.Study;
import com.cdy.cdy.domain.study.repository.StudyImageRepository;
import com.cdy.cdy.domain.study.repository.StudyRepository;
import com.cdy.cdy.domain.study.service.StudyService;
import com.cdy.cdy.domain.users.entity.Users;
import com.cdy.cdy.domain.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class StudyServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    StudyRepository studyRepository;
    @Mock
    StudyImageRepository studyImageRepository;
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


}
