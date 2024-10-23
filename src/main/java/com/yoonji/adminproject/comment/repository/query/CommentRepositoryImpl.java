package com.yoonji.adminproject.comment.repository.query;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringExpressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yoonji.adminproject.comment.dto.request.CommentCondition;
import com.yoonji.adminproject.comment.dto.request.SortType;
import com.yoonji.adminproject.comment.entity.Comment;
import com.yoonji.adminproject.comment.repository.CommentRepositoryCustom;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

import static com.yoonji.adminproject.comment.entity.QComment.comment;
import static com.yoonji.adminproject.user.entity.QUser.user;

public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final String databaseType;

    public CommentRepositoryImpl(EntityManager em, @Value("${spring.datasource.url}") String databaseUrl) {
        this.queryFactory = new JPAQueryFactory(em);
        this.databaseType = getDatabaseType(databaseUrl);
    }

    private String getDatabaseType(String url) {
        if (url.contains("h2")) {
            return "h2";
        } else if (url.contains("mysql")) {
            return "mysql";
        }
        // Add more database types as needed
        return "unknown";
    }


    @Override
    public List<Comment> getCommentsWithCursor(CommentCondition condition , Long postId) {
        return queryFactory
                .selectFrom(comment)
                .leftJoin(comment.mentionedUser, user)
                .where(comment.post.id.eq(postId)
                        .and(getCursorCondition(condition.getSortType(), condition.getOrder(), condition.getCursorId())))
                .orderBy(getOrderSpecifier(condition.getSortType(), condition.getOrder()))
                .limit(condition.getSize() + 1)
                .fetch();
    }

    private BooleanBuilder getCursorCondition(String sortType, String order, String cursorId) {
        if (cursorId == null || cursorId.equals("0")) { // 초기값
            return new BooleanBuilder();
        }

        StringExpression stringExpression;

        // sortType이 CREATED_AT일때
        if (SortType.CREATED_AT.name().equalsIgnoreCase(sortType)) {
            StringTemplate stringTemplate = Expressions.stringTemplate(
                    "cast(DATE_FORMAT({0}, {1}) as char)",
                    comment.createdAt,
                    ConstantImpl.create("%y%m%d%H%i%s")
            );
            stringExpression = StringExpressions.lpad(stringTemplate, 20, '0')
                            .concat(StringExpressions.lpad(comment.id.stringValue(), 10, '0'));
            return "asc".equalsIgnoreCase(order) ?
                    new BooleanBuilder(stringExpression.goe(cursorId)) :
                    new BooleanBuilder(stringExpression.loe(cursorId));
        }

        return new BooleanBuilder();
    }

    private OrderSpecifier<?>[] getOrderSpecifier(String sortType, String order) {
        if (sortType == null) {
            return new OrderSpecifier[]{new OrderSpecifier<>(Order.ASC, comment.id)}; // 기본 정렬
        }

        Order orderDirection = "asc".equalsIgnoreCase(order) ? Order.ASC : Order.DESC;

        return switch (SortType.valueOf(sortType)) {
            case CREATED_AT -> new OrderSpecifier[]{new OrderSpecifier<>(orderDirection, comment.createdAt)};
        };
    }

    private BooleanBuilder isNotDeleted() {
        return new BooleanBuilder(comment.deleted.isFalse().and(comment.deletedAt.isNull()));
    }


}
