package data.jpa.springdatajpa.repository;

import data.jpa.springdatajpa.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberJPARepositoryTest {

    @Autowired
    MemberJPARepository memberJPARepository;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member save = memberJPARepository.save(member);

        Member findMember = memberJPARepository.find(save.getId());

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);//true -> why? 같은 transactional 안에 있는 엔티티는 서로 같음을 보장한다.
    }
    
    @Test
    public void basicTest () throws Exception{

        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberJPARepository.save(member1);
        memberJPARepository.save(member2);

        //단건 조회 검증
        Member  findMember1 = memberJPARepository.findById(member1.getId()).get();
        Member findMember2 = memberJPARepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberJPARepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberJPARepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberJPARepository.delete(member1);
        memberJPARepository.delete(member2);

        long deleteCount  = memberJPARepository.count();
        assertThat(deleteCount  ).isEqualTo(0 );
    }

    @Test
    public void findByUsernameAndGreaterThan() {
        Member memberA = new Member("memberA",10);
        Member memberB = new Member("memberA",20);
        memberJPARepository.save(memberA);
        memberJPARepository.save(memberB);

        List<Member> result = memberJPARepository.findByUsernameAndAgeGreaterThan("memberA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("memberA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery() {
        Member memberA = new Member("memberA",10);
        Member memberB = new Member("memberB",20);
        memberJPARepository.save(memberA);
        memberJPARepository.save(memberB);

        List<Member> result = memberJPARepository.findByUsername("memberA");
        Member findMember = result.get(0);


        assertThat(findMember).isEqualTo(memberA);
    }


    @Test
    public void paging () throws Exception{
        //given
        memberJPARepository.save(new Member("m1", 10));
        memberJPARepository.save(new Member("m2", 10));
        memberJPARepository.save(new Member("m3", 10));
        memberJPARepository.save(new Member("m4", 10));
        memberJPARepository.save(new Member("m5", 10));

        int age = 10;
        int offset =0;
        int limit = 3;

        //when
        List<Member> byPage = memberJPARepository.findByPage(age, offset, limit);
        long totalCount = memberJPARepository.totalCount(age);

        //페이지 계산공식 적용...
        //totalPage = totalCount / size ...
        //마지막 페이지 ...
        //최초 페이지 ...

        //then
        assertThat(byPage.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(5);
    }

    @Test
    public void bulkTest() {
        memberJPARepository.save(new Member("m1", 10));
        memberJPARepository.save(new Member("m2", 15));
        memberJPARepository.save(new Member("m3", 20));
        memberJPARepository.save(new Member("m4", 45));
        memberJPARepository.save(new Member("m5", 50));
        memberJPARepository.save(new Member("m6", 88));

        int count = memberJPARepository.bulkAgePlus(11);
        assertThat(count).isEqualTo(6);

    }

}