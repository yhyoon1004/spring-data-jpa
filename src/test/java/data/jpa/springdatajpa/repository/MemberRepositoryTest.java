package data.jpa.springdatajpa.repository;

import data.jpa.springdatajpa.dto.MemberDTO;
import data.jpa.springdatajpa.entity.Member;
import data.jpa.springdatajpa.entity.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;           //같은 트렌젝션 안이면 같은 엔티티 매니저를 사용

    @Test
    public void testMember() throws Exception {
        Member member = new Member("memberA");
        Member saveMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(saveMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicTest() throws Exception {

        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deleteCount = memberRepository.count();
        assertThat(deleteCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndGreaterThan() {
        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberA", 20);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("memberA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("memberA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testDataJPA() {
        List<Member> top3ByMember = memberRepository.findTop3HelloBy();
    }

    @Test
    public void namedQuery() {
        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberB", 20);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

//        초기 네임드쿼리
//        List<Member> result = memberRepository.findByUsername("memberA");
//        Member findMember = result.get(0);
//        assertThat(findMember).isEqualTo(memberA);

//        jpa 인터페이스에 정의한 네임드쿼리
//        List<Member> result = memberRepository.findQueryCustom("memberA", 15);
//        assertThat(result.get(0)).isEqualTo(memberB);

        List<String> usernameList = memberRepository.getUsernameList();
        for (String value : usernameList) {
            System.out.println("value = " + value);
        }
    }

    @Test
    public void testNamedQueryDTO() {
        Member member = new Member("AAA", 10);
        memberRepository.save(member);

        Team team = new Team("TeamAAA");
        teamRepository.save(team);
        member.setTeam(team);

        List<MemberDTO> dtoData = memberRepository.findDTOData();
        for (MemberDTO value : dtoData) {
            System.out.println("value = " + value);
        }

    }

    @Test
    public void paramTest() {
        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberB", 20);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<Member> result = memberRepository.collectionParam(Arrays.asList("memberA", "memberB"));
        for (Member value : result) {
            System.out.println("value = " + value);
        }
    }

    @Test
    public void returnType() {
        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberB", 20);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<Member> ml1 = memberRepository.findListByUsername("memberA");
        for (Member value : ml1) {
            System.out.println("value = " + value);
        }
        System.out.println("_____");
        Member m1 = memberRepository.findMemberByUsername("memberA");
        System.out.println("m1 = " + m1);
        System.out.println("____");
        Optional<Member> mo1 = memberRepository.findOptionalByUsername("memberA");
        Member getMember = mo1.get();
        System.out.println("Optional Member = " + getMember);

    }


    @Test
    public void pagingTest() {
        memberRepository.save(new Member("m1", 10));
        memberRepository.save(new Member("m2", 10));
        memberRepository.save(new Member("m3", 10));
        memberRepository.save(new Member("m4", 10));
        memberRepository.save(new Member("m5", 10));

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        Page<Member> page = memberRepository.findByAge(10, pageRequest);

        //Page 인터페이스의 map 함수로 엔티티 DTO 변환가능
        Page<MemberDTO> dtos = page.map(member -> new MemberDTO(member.getId(), member.getUsername(), null));

        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();
        int totalPages = page.getTotalPages();

        System.out.println("content = " + content);
        System.out.println("totalElements = " + totalElements);
        System.out.println("totalPages = " + totalPages);

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);//현재 페이지가 0번 페이지 인지
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

        /*_____*/
        Slice<Member> byAge = memberRepository.findSliceByAge(10, pageRequest);
        assertThat(content.size()).isEqualTo(3);
//        assertThat(byAge.getTotalElements()).isEqualTo(5); slice에는 존재하지 않음
        assertThat(byAge.getNumber()).isEqualTo(0);//현재 페이지가 0번 페이지 인지
//        assertThat(byAge.getTotalPages()).isEqualTo(2);  slice에는 존재하지 않음
        assertThat(byAge.isFirst()).isTrue();
        assertThat(byAge.hasNext()).isTrue();
    }

    @Test
    public void bulkTest() {
        memberRepository.save(new Member("m1", 10));
        memberRepository.save(new Member("m2", 19));
        memberRepository.save(new Member("m3", 20));
        memberRepository.save(new Member("m4", 21));
        memberRepository.save(new Member("m5", 40));

        int count = memberRepository.bulkAgePlus(20);
        em.flush();
        em.clear();

        List<Member> findMember = memberRepository.findByUsername("m5");
        Member member = findMember.get(0);
        System.out.println("member = " + member);

        assertThat(count).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() throws Exception {
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);
        //when
        List<Member> members = memberRepository.findMemberFetchJoin();
//        List<Member> members = memberRepository.findMemberEntityGrap hByUsername("member1");

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member = " + member.getTeam().getClass());
            System.out.println("member = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() throws Exception {
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();
        //when
        Member findMember = memberRepository.findReadOnlyValueByUsername("member1");
        findMember.setUsername("member2");
        em.flush();
        //then
//        findLockByUsername
    }

    @Test
    public void lockTest() throws Exception {
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();
        //when
        List<Member> findMembers = memberRepository.findLockByUsername("member1");

    }

    @Test
    public void callCustom() throws Exception {
        List<Member> memberCustom = memberRepository.findMemberCustom();
    }


    @Test
    public void JPAEventBaseEntity () throws Exception{
        //given
        Member member = new Member("member1");
        memberRepository.save(member); //@PrePersist 발생

        Thread.sleep(1000);
        member.setUsername("member2");

        em.flush();//@PreUpdate 발생
        em.clear();
        //when
        Member findMember = memberRepository.findById(member.getId()).get();
        //then
        System.out.println("findMember.getCreatedTime() = " + findMember.getCreatedTime());
        System.out.println("findMember.getUpdateTime() = " + findMember.getUpdateTime());
        System.out.println("findMember.getCreateBy() = " + findMember.getCreateBy());
        System.out.println("findMember.getLastModifiedBy() = " + findMember.getLastModifiedBy());
    }
    
    
    @Test
    public void specBasic () throws Exception{
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();
        //when
        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
        List result = memberRepository.findAll(spec);
        //then
        Assertions.assertThat(result.size()).isEqualTo(1);
    }
    
    
    @Test
    public void queryByExample () throws Exception{
        //given
        Team team = new Team("teamA");
        em.persist(team);

        Member m1 = new Member("m1", 0, team);
        Member m2 = new Member("m2", 0, team);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        //Probe
        Member member = new Member("m1");
        Team teamA = new Team("teamA");

        member.setTeam(teamA);


        ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age");

        Example<Member> example = Example.of(member,matcher);// 인자로 들어간 엔티티 자체가 조건이 됨

        List<Member> result = memberRepository.findAll(example);

        //then
        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("m1");
    }

    @Test
    public void nativeQuery () throws Exception{
        //given
        Team team = new Team("teamA");
        em.persist(team);

        Member m1 = new Member("m1", 0, team);
        Member m2 = new Member("m2", 0, team);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
//        Member result = memberRepository.findByNativeQuery("m1");
        Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(1, 10));
        List<MemberProjection> content = result.getContent();
        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection.getUsername() = " + memberProjection.getUsername());
            System.out.println("memberProjection.getTeamName() = " + memberProjection.getTeamName());
        }
        //then

    }
}