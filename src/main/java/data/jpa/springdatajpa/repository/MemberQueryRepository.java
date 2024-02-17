package data.jpa.springdatajpa.repository;

import data.jpa.springdatajpa.entity.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor        //사용자정의 인터페이스를 만들고 구현 클래스까지 만들기까지는 과한 경우 이렇게 클래스를 만들어서 사용하는 것 추천!
public class MemberQueryRepository {
    private final EntityManager em;

    List<Member> userCustomRepositoryMethod() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
