package data.jpa.springdatajpa.repository;

import data.jpa.springdatajpa.entity.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class MemberJPARepository {

    @PersistenceContext
    private EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public  Member find(Long id) {
        return em.find(Member.class, id);
    }
}
