package data.jpa.springdatajpa.controller;

import data.jpa.springdatajpa.entity.Member;
import data.jpa.springdatajpa.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
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

    @PostConstruct
    public void init() {
        memberRepository.save(new Member("userA"));
    }
}
