package data.jpa.springdatajpa.repository;

import data.jpa.springdatajpa.dto.MemberDTO;
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
    List<Member> findByUsername(@Param("username") String username); //@Param 은 해당 네임드 쿼리에 파라미터가 있을 때 넣어준다.


    //자주쓰이는 기능 || 로딩시점에 해당 쿼리를 파싱하므로 컴파일 타임에 잘못된 문법을 확인할 수 있다.
    @Query("select m from Member m where m.username = :username and m.age >= :age")
    List<Member> findQueryCustom(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> getUsernameList();

    @Query("select new data.jpa.springdatajpa.dto.MemberDTO( m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDTO> findDTOData();

    @Query("select m from Member m where m.username in :names")
    List<Member> collectionParam(@Param("names") List<String> names);
}
