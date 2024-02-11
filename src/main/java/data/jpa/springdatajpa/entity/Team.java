package data.jpa.springdatajpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED )
@ToString(of = {"id", "name"})
public class Team {
    @Id
    @GeneratedValue
    @Column(name = "team_id")
    private Long id;
    private String name;
    @OneToMany(mappedBy = "team")//외래키가 없는 쪽에 mappedBy를 넣어주는 게 좋음
    private List<Member> members = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }


}
