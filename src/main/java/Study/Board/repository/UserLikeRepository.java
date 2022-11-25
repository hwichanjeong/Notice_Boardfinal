package Study.Board.repository;

import Study.Board.domain.Content;
import Study.Board.domain.UserLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.validation.Valid;
import java.util.Optional;

public interface UserLikeRepository extends JpaRepository<UserLike,Long> {
    @Query(
            value = "select c from Content c join c.userLikes u where u.user.id = :userId"
    )
    Page<Content> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query(
            value = "select c from Content c join c.userLikes u where u.user.id = :userId and"
            +" c.user.nickname = :userNickname"
    )
    Page<Content> findByUserIdAndUserNickname(@Param("userId") Long userId,
                                              @Param("userNickname") String userNickname, Pageable pageable);

    @Query(
            value = "select c from Content c join c.userLikes u where u.user.id = :userId and "
                    +"c.title like %:contentTitle%"
    )
    //Pageable: 페이징 처리를 하기 위한 JPA에서 제공하는 것
    Page<Content> findByUserIdAndContentTitleContains(@Param("userId") Long userId,
                                                      @Param("contentTitle") String contentTitle,Pageable pageable);

    Optional<UserLike> findByUserIdAndContentId(Long userId, Long content_Id);
}
