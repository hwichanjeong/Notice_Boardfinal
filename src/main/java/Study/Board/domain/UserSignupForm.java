package Study.Board.domain;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data //@Data는 위에서 설명드린 @Getter, @Setter, @RequiredArgsConstructor, @ToString, @EqualsAndHashCode을 한꺼번에 설정해주는 매우 유용한 어노테이션입니다.
public class UserSignupForm {
    @NotEmpty
    private String loginId;
    @NotEmpty
    private String password;
    @NotEmpty
    private String passwordCheck;
    @NotEmpty
    private String nickname;
}
