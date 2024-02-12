package data.jpa.springdatajpa.repository;

import data.jpa.springdatajpa.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findTop3HelloBy();

//    @Query(name = "Member.findByUsername") data jpa를 사용한 네임드쿼리 사용법
//    사실 없어도 JpaRepository 가 인터페이스 메서드를 만들기 전에 해당 엔티티의 네임드 쿼리를 찾음
    List<Member> findByUsername(@Param("username")String username); //@Param 은 해당 네임드 쿼리에 파라미터가 있을 때 넣어준다.
}
