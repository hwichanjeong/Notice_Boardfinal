package Study.Board;

import Study.Board.auth.PrincipalDetails;
import Study.Board.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.validation.constraints.Null;
import java.security.Principal;

@Controller
public class HomeController {

    @GetMapping("/")
    //@AuthenticationPrincipal 어노테이션을 사용하면 UserDetailsService에서 Return한 객체를 파라미터로 직접 받아 사용할 수 있다.
    public String home(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if(principalDetails == null) {
            return "home";
        } else {
            User loginUser = principalDetails.getUser();
            model.addAttribute("loginUser", loginUser);
            return "loginHome";
        }
    }

    @GetMapping("/message")
    //@RequestParam: 요청 매개변수에 들어있는 기본 타입 데이터를 메서드 인자로 받아올 수 있따.
    public String message( @RequestParam String msg, String url, Model model) {
        model.addAttribute("msg", msg);
        model.addAttribute("url", url);
        return "message";
    }
}
