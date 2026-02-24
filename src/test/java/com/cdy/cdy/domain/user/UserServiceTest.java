package com.cdy.cdy.domain.user;


import com.cdy.cdy.domain.users.dto.ResponseMyProfile;
import com.cdy.cdy.domain.users.dto.UserRequestDto;
import com.cdy.cdy.domain.users.entity.UserCategory;
import com.cdy.cdy.domain.users.entity.UserRole;
import com.cdy.cdy.domain.users.entity.Users;
import com.cdy.cdy.domain.users.repository.UserRepository;
import com.cdy.cdy.domain.users.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {


    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserService userService;

    //테스트 용 유저 (삭제안된 유저)
    Users users;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;
    //테스트 용 유저 (삭제된 유저)
    Users deletedUser;
    @BeforeEach
    void setUp() {

        users = Users.builder()
                .id(1L)
                .nickname("user")
                .role(UserRole.USER)
                .username("username")
                .userCategory(UserCategory.CODING)
                .description("한 줄 소개")
                .isDeleted(false)
                .build();


        deletedUser = Users.builder()
                .id(1L)
                .nickname("deletedUser")
                .role(UserRole.USER)
                .username("deletedUsername")
                .userCategory(UserCategory.CODING)
                .description("한 줄 소개")
                .isDeleted(true)
                .build();


    }

    /**
     * 유저 정보 조회 로직
     * db에서 파라미터에 해당하는 유저네임 결과가 없으면 에러발생
     */
    @Test
    void 존재하지_않는_유저_에러발생() {

        //given
        given(userRepository.findByUsername(any()))
                .willReturn(Optional.empty());


        //when & then

        assertThatThrownBy(() -> userService.getProfileInfo(any()))
                .hasMessageContaining("유저를 찾을 수 없습니다.")
                .isInstanceOf(UsernameNotFoundException.class);

    }
    /**
     * 유저 정보 조회 로직
     *  deleted true 경우 예외 발생
     */
    @Test
    void 삭제된_유저면_에러발생() {

        //given
        given(userRepository.findByUsername(deletedUser.getUsername()))
                .willReturn(Optional.of(deletedUser));

        //when & then

        assertThatThrownBy(() -> userService.getProfileInfo(deletedUser.getUsername()))
                .isInstanceOf(UsernameNotFoundException.class);
    }
    /**
     * 유저 정보 조회 로직
     *  dto 값 정상반환
     */

    @Test
    void 유저정보_유저네임_유저닉네임_한줄소개_유저카테고리_정상조회() {


        //given
        given(userRepository.findByUsername(users.getUsername()))
                .willReturn(Optional.of(users));


        //when

        ResponseMyProfile result = userService.getProfileInfo(users.getUsername());


        //then
        assertThat(result.getUserCategory()).isEqualTo(users.getUserCategory().toString());
        assertThat(result.getNickname()).isEqualTo(users.getNickname());
        assertThat(result.getDescription()).isEqualTo(users.getDescription());
        assertThat(result.getUsername()).isEqualTo(users.getUsername());
    }


    @DisplayName("UserService - updateProfile")
    @Nested
    class UpdateProfileTest {

        @Test
        @DisplayName("변경_존재하지_않는_유저_에러발생")
        void 변경_존재하지_않는_유저_에러발생() {

            //given
            given(userRepository.findByUsername(any()))
                    .willReturn(Optional.empty());

            //when & then

            assertThatThrownBy(() -> userService.getProfileInfo(any()))
                    .isInstanceOf(UsernameNotFoundException.class);

        }


        @Test
        @DisplayName("변경_삭제된_유저면_에러발생")
        void 변경_삭제된_유저면_에러발생() {

            //given

            given(userRepository.findByUsername(deletedUser.getUsername()))
                    .willReturn(Optional.of(deletedUser));

            //when & then

            assertThatThrownBy(() -> userService.getProfileInfo(deletedUser.getUsername()))
                    .isInstanceOf(UsernameNotFoundException.class);

        }

        @Test
        @DisplayName("유저_본인이_아니면_에러발생")
        void 유저_본인이_아니면_에러발생() {

            //given
            given(userRepository.findByUsername(users.getUsername()))
                    .willReturn(Optional.of(users));

            UserRequestDto dto = new UserRequestDto();
            dto.setUsername("wrong");

            //when & then

            assertThatThrownBy(() -> userService.changeMyPage(users.getUsername(), dto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("본인만 정보를 변경 할 수 있습니다.");

        }

        @Test
        @DisplayName("dto변경값으로 엔티티에 정상 반영 완료")
        void updateSuccess() {

            //given
            given(userRepository.findByUsername(users.getUsername()))
                    .willReturn(Optional.of(users));

            UserRequestDto dto = new UserRequestDto();
            dto.setUsername("username"); //유저네임 동일
            dto.setNickname("change"); // 닉네임 user -> chagne 확인
            dto.setPassword("changepassword"); // 비밀번호
            dto.setUserCategory(UserCategory.valueOf("DESIGN"));
            dto.setDescription("한줄소개바꾸기"); //한 줄 소개 - > 한줄소개바꾸기 확인

            given(bCryptPasswordEncoder.encode(any()))
                    .willReturn("changepassword");

            //when
            userService.changeMyPage("username", dto);


            //then
            assertThat(users.getUserCategory()).isEqualTo(UserCategory.DESIGN);
            assertThat(users.getNickname()).isEqualTo("change");
            assertThat(users.getPassword()).isEqualTo("changepassword");
            assertThat(users.getDescription()).isEqualTo("한줄소개바꾸기");

        }
    }

}
