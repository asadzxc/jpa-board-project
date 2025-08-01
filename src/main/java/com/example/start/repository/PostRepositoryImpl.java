package com.example.start.repository;

import com.example.start.entity.Post;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.util.StringUtils;

import java.util.List;

public class PostRepositoryImpl implements PostRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Post> searchByConditions(String title, String content, String username) {
        StringBuilder jpql = new StringBuilder("SELECT p FROM Post p WHERE 1=1");

        if (StringUtils.hasText(title)) {
            jpql.append(" AND p.title LIKE :title");
        }
        if (StringUtils.hasText(content)) {
            jpql.append(" AND p.content LIKE :content");
        }
        if (StringUtils.hasText(username)) {
            jpql.append(" AND p.author.name LIKE :username");
        }

        TypedQuery<Post> query = em.createQuery(jpql.toString(), Post.class);

        if (StringUtils.hasText(title)) {
            query.setParameter("title", "%" + title + "%");
        }
        if (StringUtils.hasText(content)) {
            query.setParameter("content", "%" + content + "%");
        }
        if (StringUtils.hasText(username)) {
            query.setParameter("username", "%" + username + "%");
        }

        return query.getResultList();
    }
}