package com.cdy.cdy.domain.users.service;

import com.cdy.cdy.domain.users.entity.Users;
import com.cdy.cdy.domain.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override  //로그필터 이후 dto로 들어온 로그인정보를 DB에서 가져온 정보랑 체크하는 메서드
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Users users = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

        if (users.getIsDeleted().equals(true)) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }

        return User.builder()
                .username(username)
                .password(users.getPassword())
                .roles(users.getRole().name())
                .build();

    }
}