package data.jpa.springdatajpa.repository;

import data.jpa.springdatajpa.dto.MemberDTO;
import data.jpa.springdatajpa.entity.Member;
import data.jpa.springdatajpa.entity.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
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
        assertThat(page.hasNext()).isTrue() ;

        /*_____*/
        Slice <Member> byAge = memberRepository.findSliceByAge(10, pageRequest);
        assertThat(content.size()).isEqualTo(3);
//        assertThat(byAge.getTotalElements()).isEqualTo(5); slice에는 존재하지 않음
        assertThat(byAge.getNumber()).isEqualTo(0);//현재 페이지가 0번 페이지 인지
//        assertThat(byAge.getTotalPages()).isEqualTo(2);  slice에는 존재하지 않음
        assertThat(byAge.isFirst()).isTrue();
        assertThat(byAge.hasNext()).isTrue() ;
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

}