package data.jpa.springdatajpa.repository;


import data.jpa.springdatajpa.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {
    List<Member> findMemberCustom();
}
