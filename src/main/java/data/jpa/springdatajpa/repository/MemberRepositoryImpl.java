package data.jpa.springdatajpa.repository;

import data.jpa.springdatajpa.entity.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;


/**
 * 사용자 정의 인터페이스의 메서드를 구현할 때 해당 사용자정의 "인터페이스의 이름" + "Impl" 형식으로 이름을 지정해야한다.
 * 이렇게 이름을 지으면 JPA가 알아서 해당 클래스를 찾아 해당 사용자정의 인터페이스의 메서드들을 구현해 넣는다.
 * */
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList() ;
    }

}
