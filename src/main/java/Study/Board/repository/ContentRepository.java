package Study.Board.repository;

import Study.Board.domain.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Long> {
    Page<Content> findByCategory(Integer category, Pageable pageable);
    Page<Content> findByCategoryAndUserNickname(Integer category, String title, Pageable pageable);
    Page<Content> findByCategoryAndTitleContains(Integer category, String title, Pageable pageable);
}
