package data.jpa.springdatajpa.repository;

import data.jpa.springdatajpa.dto.MemberDTO;
import data.jpa.springdatajpa.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, JpaSpecificationExecutor {
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
    List<Member> collectionParam(@Param("names") Collection<String> names);

    List<Member> findListByUsername(String username);   //컬렉션

    // || Collection 은 절대 null 을 반환하지 않음, 값이 없으면 빈 객체를 반환 ,‼️null 처리 x
    Member findMemberByUsername(String username);   //단건 조회

    // || 객체를 반환하는 메서드의 경우 null을 반렬
    Optional<Member> findOptionalByUsername(String username);   //단건 조회 Optional

    //    join 문이 많아지거나 특수한 경우 전체 갯수를 조회해오는 쿼리를 별도로 넣어줄 수 있음
//    @Query(value = "select m from Member m", countQuery = "select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    Slice<Member> findSliceByAge(int age, Pageable pageable);


    //    @Modifying  //이 어노테이션이 있어야 getResultList 같은 결과로 반환하는 것이 아닌 executeUpdate()와 같은 결과를 리턴
    @Modifying(clearAutomatically = true)   // 해당 옵션이 true 일 경우 해당쿼리가 실행된 후 entityManager.clear() 를 자동으로 해줌
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    // JPQL 이 사용될 경우 자동적으로 엔티티 flush()를 해버리고 실행됨
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})
        // 해당 데이터를 가져올 때 연관관계인
        // team엔티티 정보도 같이 가져오고 싶으면 EntityGraph 어노테이션을 넣어주면 됨
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"}) // jpql도 사용하면서 관련 객체도 받을려면 EntityGraph 어노테이션을 넣어주면 됨
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    @EntityGraph(attributePaths = {"team"})
//    @EntityGraph("Member.all") // -> 네임드 엔티티 그래프 호출
    List<Member> findMemberEntityGraphByUsername(@Param("username") String username); //엔티티그래프를 JPA 네이밍 메서드에 사용할 수 도 있음



    //JPA Hint란 쿼리를 보낼 때 JPA 구현체(Hibernate)에게 설정정보를 알려주는 것
    //엔티티를 조회해오면 JPA 영속성 컨택스트에 원본객채와 변경감지를 위한 객체 2개를 만드는 데 개발자는 그냥 그런 불필요한 과정없이 해당 객체 데이터만 가지고 오고 싶을 때
    //아래 쿼리힌트의 값을 readOnly로 아래와 같이 어노테이션으로 넣어 주면 원본 객체만 생성
    @QueryHints(value = {@QueryHint(name = "org.hibernate.readOnly", value = "true")})
    Member findReadOnlyValueByUsername(String username);

    //데이터베이스에 쿼리에 트렌젝션 처리시 lock을 거는 것을 이야기하는 것 같음..
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);
}
