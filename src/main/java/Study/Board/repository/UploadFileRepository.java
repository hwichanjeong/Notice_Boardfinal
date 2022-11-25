package Study.Board.repository;

import Study.Board.domain.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadFileRepository extends JpaRepository<UploadFile,Long> {
    void deleteALLByContentId(Long contentId);
}
