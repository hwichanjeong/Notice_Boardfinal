package Study.Board.repository;

import Study.Board.domain.User;
import Study.Board.domain.UserLike;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional
public class UserRepository {
    // 영속성 컨텍스트: Entity를 영구히 저장하는 환경
    private final EntityManager em;

    public User save(User user) {
        // user를 EntityManager에 저장 영속성 컨텍스트를 통해 Entity를 영속화 하는 것
        em.persist(user);
        return findById((long) user.getId());
    }
    public User findById(Long id) {
        return em.find(User.class, id);
    }

    //Optinal을 쓰는 이유는 NullPointException 오류가 나지 않게 하기 위함이다.
    public Optional<User> findByLoginId(String loginId) {
        return em.createQuery("select u from User u where u.loginId=:loginId")
                .setParameter("loginId", loginId)//매개변수 값을 설정하는 메서드
                .getResultStream()//쿼리결과를 stream으로 검색할 수 있다.
                .findAny();//조건에 일치하는 요소 1개를 리턴한다.
    }

    public Optional<User> findByNickname(String nickname) {
        return em.createQuery("select u from User u where u.nickname=:nickname")
                .setParameter("nickname", nickname)
                .getResultStream()
                .findAny();
    }

    public void delete(Long userId) {
        em.remove(findById(userId));
        return;
    }

    public List<User> findAll(int page) {
        return em.createQuery("select u from User u")
                .setFirstResult(page * 10)
                .setMaxResults(10)
                .getResultList();
    }

    public Long findLastPage() {
        return (em.createQuery("select u from User u")
                .getResultStream().count() - 1) / 10 + 1;
    }

}
