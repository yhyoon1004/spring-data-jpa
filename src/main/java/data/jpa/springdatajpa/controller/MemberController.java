package data.jpa.springdatajpa.controller;

import data.jpa.springdatajpa.dto.MemberDTO;
import data.jpa.springdatajpa.entity.Member;
import data.jpa.springdatajpa.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

    @GetMapping("/member/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    @GetMapping("/member2/{id}")
    public String findMember2(@PathVariable("id") Member member) {  //spring data jpa 가 자동으로 해당 값을 넣어처리해줌
        return member.getUsername();
    }


    //   http://주소/member?page=1&size=3&sort=id,desc  <- 이렇게 파라미터를 추가해서 던지면 DATA JPA 알아서 처리해줌
    @GetMapping("/member")
    public Page<MemberDTO> list(@PageableDefault(size = 5,sort = "username") Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        Page<MemberDTO> map = page.map(MemberDTO::new);//엔티티 노출을 방지하기위해 DTO 매핑
        return map;
    }


//    @PostConstruct
//    public void init() {
////        memberRepository.save(new Member("userA"));
//        for (int i = 0; i < 100; i++) {
//            memberRepository.save(new Member("user" + i, i));
//        }
//    }
}
