package com.yoonji.adminproject.user.repository;

import com.yoonji.adminproject.admin.dto.request.AdminUserSearchCondition;
import com.yoonji.adminproject.user.dto.request.SearchType;
import com.yoonji.adminproject.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    public void searchUsersByEmail() {
        // given
        AdminUserSearchCondition condition = new AdminUserSearchCondition();
        condition.setSearchType(SearchType.EMAIL.name());
        condition.setSearchInput("kim");
        condition.setSize(10);

        // when
        List<User> result = userRepository.searchUsersWithCursor(condition);

        // then
        assertThat(result).extracting("email")
                .contains("kim.minsoo@example.com", "kim.soohyun@gmail.com", "kim.goeun@naver.com")
                .doesNotContain("kim.taehee@naver.com", "park.jiyoung@gmail.com", "lee.sungmin@naver.com");
    }

    @Test
    public void searchUsersByNickname() {
        // given
        AdminUserSearchCondition condition = new AdminUserSearchCondition();
        condition.setSearchType(SearchType.NICKNAME.name());
        condition.setSearchInput("민");
        condition.setSize(10);

        // when
        List<User> result = userRepository.searchUsersWithCursor(condition);

        // then
        assertThat(result).extracting("nickname")
                .contains("민수kim", "성민이", "민호정", "민호이")
                .doesNotContain("지영팍", "유나최");
    }

    @Test
    public void searchUsersByEmailOrNickname() {
        // given
        AdminUserSearchCondition condition = new AdminUserSearchCondition();
        condition.setSearchType(SearchType.ALL.name());
        condition.setSearchInput("kim");
        condition.setSize(10);

        // when
        List<User> result = userRepository.searchUsersWithCursor(condition);

        // then
        assertThat(result).extracting("nickname")
                .contains("민수kim")
                .doesNotContain("지영팍", "유나최");

        assertThat(result).extracting("email")
                .contains("kim.minsoo@example.com", "kim.soohyun@gmail.com", "kim.goeun@naver.com")
                .doesNotContain("kim.taehee@naver.com", "park.jiyoung@gmail.com", "lee.sungmin@naver.com");
    }

    @Test
    public void searchUsersByRoles() {
        // given
        AdminUserSearchCondition condition = new AdminUserSearchCondition();
        List<String> roles = List.of("ROLE_ADMIN");
        condition.setRoles(roles);
        condition.setSize(10);

        // when
        List<User> result = userRepository.searchUsersWithCursor(condition);

        // then
        assertThat(result).extracting("email")
                .contains("lee.sungmin@naver.com", "hong.gildong@gmail.com", "jung.minho@example.com")
                .doesNotContain("park.jiyoung@gmail.com", "choi.yuna@example.com");
    }

    @Test
    public void searchUsersByProviders() {
        // given
        AdminUserSearchCondition condition = new AdminUserSearchCondition();
        List<String> providers = List.of("GOOGLE", "NAVER");
        condition.setProviders(providers);
        condition.setSize(10);

        // when
        List<User> result = userRepository.searchUsersWithCursor(condition);

        // then
        assertThat(result).extracting("email")
                .contains("park.jiyoung@gmail.com", "hong.gildong@gmail.com", "kim.soohyun@gmail.com")
                .doesNotContain("lee.jongsuk@gmail.com", "yoo.jaesuk@example.com", "shin.yeonjoo@gmail.com", "lee.jongsuk@gmail.com");
    }

    @Test
    public void searchUsersByDateRange() {
        // given
        AdminUserSearchCondition condition = new AdminUserSearchCondition();
        condition.setStartDate(LocalDateTime.of(2023, 1, 1, 0, 0));
        condition.setEndDate(LocalDateTime.of(2023, 6, 30, 23, 59));
        condition.setSize(10);

        // when
        List<User> result = userRepository.searchUsersWithCursor(condition);

        // then
        assertThat(result).extracting("email")
                .contains("kim.minsoo@example.com", "park.jiyoung@gmail.com", "lee.sungmin@naver.com")
                .doesNotContain("jung.haein@gmail.com", "oh.yeonseo@example.com");
    }

    @Test
    public void searchUsersByStartDateGoe() {
        // given
        AdminUserSearchCondition condition = new AdminUserSearchCondition();
        condition.setStartDate(LocalDateTime.of(2023, 1, 1, 0, 0));
        condition.setSize(10);

        // when
        List<User> result = userRepository.searchUsersWithCursor(condition);

        // then
        assertThat(result).extracting("email")
                .contains("kim.minsoo@example.com", "park.jiyoung@gmail.com", "lee.sungmin@naver.com", "hong.gildong@gmail.com",
                        "kang.sora@naver.com", "jung.minho@example.com", "kwon.jiae@naver.com", "yoo.jaesuk@example.com",
                        "lee.minho@example.com", "park.bogum@gmail.com")
                .doesNotContain("jun.jihyun@naver.com", "choi.yuna@example.com");
    }

    @Test
    public void searchUsersByEndDateLoe() {
        // given
        AdminUserSearchCondition condition = new AdminUserSearchCondition();
        condition.setEndDate(LocalDateTime.of(2023, 3, 8, 0, 0));
        condition.setSize(10);

        // when
        List<User> result = userRepository.searchUsersWithCursor(condition);

        // then
        assertThat(result).extracting("email")
                .contains("kim.minsoo@example.com", "park.jiyoung@gmail.com", "lee.sungmin@naver.com")
                .doesNotContain("hong.gildong@gmail.com", "kang.sora@naver.com");
    }

    @Test
    public void searchUsersWithMultipleConditions() {
        // given
        AdminUserSearchCondition condition = new AdminUserSearchCondition();
        condition.setSearchInput("lee");
        condition.setSearchType(SearchType.EMAIL.name());
        List<String> roles = List.of("ROLE_USER");
        condition.setRoles(roles);
        List<String> providers = List.of("LOCAL");
        condition.setProviders(providers);
        condition.setStartDate(LocalDateTime.of(2023, 1, 1, 0, 0));
        condition.setEndDate(LocalDateTime.of(2024, 12, 31, 23, 59));
        condition.setSize(10);

        // when
        List<User> result = userRepository.searchUsersWithCursor(condition);

        // then
        assertThat(result).extracting("email")
                .contains("lee.minho@example.com")
                .doesNotContain("lee.sungmin@naver.com", "park.jiyoung@gmail.com", "kim.taehee@naver.com");
    }

}