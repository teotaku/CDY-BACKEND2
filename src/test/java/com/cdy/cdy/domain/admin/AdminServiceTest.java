package com.cdy.cdy.domain.admin;


import com.cdy.cdy.admin.service.AdminService;
import com.cdy.cdy.domain.users.dto.UserRequestDto;
import com.cdy.cdy.domain.users.entity.UserCategory;
import com.cdy.cdy.domain.users.entity.UserRole;
import com.cdy.cdy.domain.users.entity.Users;
import com.cdy.cdy.domain.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @InjectMocks
    AdminService adminService;

    Users admin;
    Users user;


    @BeforeEach
    void setUp() {

        admin = Users.builder()
                .username("admin")
                .role(UserRole.ADMIN)
                .build();

        user = Users.builder()
                .username("user")
                .role(UserRole.USER)
                .build();
    }

    @Nested
    @DisplayName("관리자가 신규 회원 등록")
    class adminServiceTest {


        @Test
        @DisplayName("관리자가 아닐시에 에러발생")
        void no_admin_error() {

            //given

            given(userRepository.findByUsername(user.getUsername()))
                    .willReturn(Optional.of(user));

            UserRequestDto dto = new UserRequestDto();
            dto.setUsername("test");

            //when & then

            assertThatThrownBy(() -> adminService.createUser(user.getUsername(), dto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("관리자만 가능.");


        }

        @Test
        @DisplayName("이미 존재하는 아이디면 에러발생")
        void already_exists_error() {

            //given

            UserRequestDto dto = new UserRequestDto();
            dto.setUsername("user");
            given(userRepository.findByUsername(admin.getUsername()))
                    .willReturn(Optional.of(admin));
            given(userRepository.findByUsername(dto.getUsername()))
                    .willReturn(Optional.of(user));

            //when & then
            assertThatThrownBy(() -> adminService.createUser(admin.getUsername(), dto))
                    .hasMessageContaining("이미 존재하는 아이디입니다.");

        }

        @Test
        @DisplayName("존재하지 않는 관리자아이디 에러발생")
        void no_exist_admin_error() {


            //given
            given(userRepository.findByUsername(any()))
                    .willReturn(Optional.empty());
            UserRequestDto dto = new UserRequestDto();
            dto.setUsername("user");

            //when & then
            assertThatThrownBy(() -> adminService.createUser(user.getUsername(), dto))
                    .hasMessageContaining("존재하지 않는 유저");


        }

        @Test
        @DisplayName("기본 유저 role은 USER")
        void role_of_user_is_USER() {

            //given

            UserRequestDto dto = new UserRequestDto();
            dto.setUsername("test");
            dto.setPassword("password");

            given(userRepository.findByUsername(admin.getUsername()))
                    .willReturn(Optional.of(admin));
            given(userRepository.findByUsername(dto.getUsername()))
                    .willReturn(Optional.empty());
            given(bCryptPasswordEncoder.encode(any()))
                    .willReturn("encodedPassword");
            ArgumentCaptor<Users> captor = ArgumentCaptor.forClass(Users.class);
            //when
            adminService.createUser(admin.getUsername(), dto);
            //then
            verify(userRepository).save(captor.capture());
            Users value = captor.getValue();
            assertThat(value.getRole().equals(UserRole.USER));
        }

        @Test
        @DisplayName("dto값 엔티티로 전부 정상적용")
        void from_dto_to_entity_success() {


            //given
            UserRequestDto dto = new UserRequestDto();
            dto.setUsername("test");
            dto.setPassword("password");
            dto.setName("테스트");
            dto.setPhoneNumber("01012345678");
            dto.setUserCategory(UserCategory.DESIGN);

            given(userRepository.findByUsername(admin.getUsername()))
                    .willReturn(Optional.of(admin));
            given(userRepository.findByUsername(dto.getUsername()))
                    .willReturn(Optional.empty());
            given(bCryptPasswordEncoder.encode(any()))
                    .willReturn("encodedPassword");
            ArgumentCaptor<Users> captor = ArgumentCaptor.forClass(Users.class);

            //when
            adminService.createUser(admin.getUsername(), dto);

            //then
            verify(userRepository).save(captor.capture());
            Users value = captor.getValue();
            assertThat(value.getRole().equals(UserRole.USER));
            assertThat(value.getUserCategory().equals(UserCategory.DESIGN));
            assertThat(value.getPassword().equals("enocdedPassword"));
            assertThat(value.getUsername().equals(dto.getUsername()));
            assertThat(value.getPhoneNumber().equals(dto.getPhoneNumber()));
            assertThat(value.getName().equals(dto.getName()));
        }
    }
}
