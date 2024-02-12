package data.jpa.springdatajpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)//protected 수준의 생성자를 만들어주는 어노테이션
@ToString(of = {"id", "username", "age"}) //객체를 출력할 때 사용하는 어노테이션

@NamedQuery(
        name = "Member.findByUsername",
        query = "select m from Member m where m.username = :username"
)

public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")//외래키 명 설정
    private Team team;


//    protected Member() { @NoArgsConstructor(access = AccessLevel.PROTECTED)으로 인해 자동생성되어 만들어줄 필요값없음
//    }

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public Member(String name, int age, Team team) {
        this.username = name;
        this.age = age;
        if (team != null) {
            changeTeam(team);
        }
    }

    public void changeTeam(Team team) { //객체이기 때문에 연관관계의 상대방도 변경
        this.team = team;
        team.getMembers().add(this);
    }


}
