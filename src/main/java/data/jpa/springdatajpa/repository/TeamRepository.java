package data.jpa.springdatajpa.repository;

import data.jpa.springdatajpa.entity.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public class TeamRepository {
    @PersistenceContext
    EntityManager em;

    public Team save(Team team) {
        em.persist(team);
        return team;
    }

    public void delete(Team team) {
        em.remove(team);
    }

    public List<Team> findAll() {
        return em.createQuery("select t from Team t", Team.class).getResultList();
    }

    public Optional<Team> findById(Long id) {
        Team team = em.find(Team.class, id);
        return Optional.ofNullable(team);
    }

    public long count() {
        return em.createQuery("select count(t) from Team t", Long.class).getSingleResult() ;
    }


    //JPA는 업데이트 메서드가 필요없다, why? JPA는 변경감지 기능이 있어서 영속상태의 엔티티가 변경이 되면 자동으로 업데이트 쿼리를 보냄

}
