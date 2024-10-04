package com.yoonji.adminproject.user.repository.query;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yoonji.adminproject.user.dto.request.SearchType;
import com.yoonji.adminproject.admin.dto.request.AdminUserSearchCondition;
import com.yoonji.adminproject.user.dto.request.SortType;
import com.yoonji.adminproject.user.entity.ProviderType;
import com.yoonji.adminproject.user.entity.User;
import com.yoonji.adminproject.user.repository.UserRepositoryCustom;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.yoonji.adminproject.user.entity.QRole.role;
import static com.yoonji.adminproject.user.entity.QUser.user;
import static com.yoonji.adminproject.user.entity.QUserRole.userRole;
import static org.springframework.util.StringUtils.hasText;

public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public UserRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<User> searchUsersWithCursor(AdminUserSearchCondition condition) {
        return queryFactory
                .selectFrom(user).distinct()
                .leftJoin(user.userRoles, userRole)
                .leftJoin(userRole.role, role)
                .where(applySearchType(condition.getSearchInput(), condition.getSearchType())
                        .and(hasRoles(condition.getRoles()))
                        .and(hasProviders(condition.getProviders()))
                        .and(goeStartDate(condition.getStartDate()))
                        .and(loeEndDate(condition.getEndDate()))
                        .and(isNotDeleted())
                        .and(getCursorCondition(condition.getSortType(), condition.getOrder(), condition.getCursorId()))
                )
                .orderBy(getOrderSpecifier(condition.getSortType(), condition.getOrder()))
                .limit(condition.getSize() + 1)
                .fetch();
    }

    private BooleanBuilder getCursorCondition(String sortType, String order, String cursorId) {
        if (cursorId == null || cursorId.equals("0")) { // 초기값
            return new BooleanBuilder();
        }

        if (sortType == null) { // sortType이 null일 경우 기본 정렬 (userId)
            return "asc".equalsIgnoreCase(order) ?
                    new BooleanBuilder(user.id.goe(Long.parseLong(cursorId))) :
                    new BooleanBuilder(user.id.loe(Long.parseLong(cursorId)));
        }

        StringExpression stringExpression;

        // sortType이 CREATED_AT일때
        if (SortType.CREATED_AT.name().equalsIgnoreCase(sortType)) {
            StringExpression stringTemplate = Expressions.stringTemplate(
                    "cast(DATE_FORMAT({0}, {1}) as char)",
                    user.createdAt,
                    ConstantImpl.create("%y%m%d%H%i%s")
            );
            stringExpression = StringExpressions.lpad(stringTemplate, 20, '0')
                            .concat(StringExpressions.lpad(user.id.stringValue(), 10, '0'));
            return "asc".equalsIgnoreCase(order) ?
                    new BooleanBuilder(stringExpression.goe(cursorId)) :
                    new BooleanBuilder(stringExpression.loe(cursorId));
        }

        // sortType이 EMAIL일때
        if (SortType.EMAIL.name().equalsIgnoreCase(sortType)) {
            return "asc".equalsIgnoreCase(order) ?
                    new BooleanBuilder(user.email.goe(cursorId)) :
                    new BooleanBuilder(user.email.loe(cursorId));
        }

        return new BooleanBuilder();
    }

    private OrderSpecifier<?>[] getOrderSpecifier(String sortType, String order) {
        if (sortType == null) {
            return new OrderSpecifier[]{new OrderSpecifier<>(Order.ASC, user.id)}; // 기본 정렬
        }

        Order orderDirection = "asc".equalsIgnoreCase(order) ? Order.ASC : Order.DESC;

        return switch (SortType.valueOf(sortType)) {
            case CREATED_AT -> new OrderSpecifier[]{new OrderSpecifier<>(orderDirection, user.createdAt)};
            case EMAIL -> new OrderSpecifier[]{new OrderSpecifier<>(orderDirection, user.email)};
        };
    }

    private BooleanBuilder applySearchType(String searchInput, String searchType) {
        if (searchType == null || !hasText(searchInput)) {
            return new BooleanBuilder();
        }

        return switch (SearchType.fromString(searchType)) {
            case EMAIL -> containsEmail(searchInput);
            case NICKNAME -> containsNickname(searchInput);
            default -> containsEmail(searchInput).or(containsNickname(searchInput));
        };
    }

    private BooleanBuilder containsEmail(String searchInput) {
        if (!hasText(searchInput)) {
            return new BooleanBuilder();
        }
        return new BooleanBuilder(user.email.containsIgnoreCase(searchInput));
    }

    private BooleanBuilder containsNickname(String searchInput) {
        if (!hasText(searchInput)) {
            return new BooleanBuilder();
        }
        return new BooleanBuilder(user.nickname.containsIgnoreCase(searchInput));
    }

    private BooleanBuilder hasRoles(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return new BooleanBuilder();
        }

        return new BooleanBuilder(user.userRoles.any().role.name.in(roles));
    }

    private BooleanBuilder hasProviders(List<String> providers) {
        if (providers == null || providers.isEmpty()) {
            return new BooleanBuilder();
        }

        Set<ProviderType> providerTypes = providers.stream()
                .map(ProviderType::getProviderType)
                .collect(Collectors.toSet());

        return new BooleanBuilder(user.provider.in(providerTypes));
    }

    private BooleanBuilder goeStartDate(LocalDateTime startDate) {
        if (startDate == null) {
            return new BooleanBuilder();
        }
        return new BooleanBuilder(user.createdAt.goe(startDate));
    }

    private BooleanBuilder loeEndDate(LocalDateTime endDate) {
        if (endDate == null) {
            return new BooleanBuilder();
        }
        return new BooleanBuilder(user.createdAt.loe(endDate));
    }

    private BooleanBuilder isNotDeleted() {
        return new BooleanBuilder(user.deleted.isFalse().and(user.deletedAt.isNull()));
    }


}