package Study.Board.controller;

import Study.Board.auth.PrincipalDetails;
import Study.Board.domain.*;
import Study.Board.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Hashtable;

@RequiredArgsConstructor
@RequestMapping("/user")
@Controller
@Slf4j
public class UserController {

    //(Key, Value)로 데이터를 저장하는 자료구조
    public static Hashtable sessionList = new Hashtable();
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/signup")
    public String signupForm(@ModelAttribute UserSignupForm form) {
        return "user/signupForm";
    }

    @PostMapping("/signup")
    //BindingResult는 검증 오류가 발생할 경우 오류 내용을 보관하는 스프링 프레임워크에서 제공하는 객체다.
    public String signup(@Valid @ModelAttribute UserSignupForm form, BindingResult bindingResult, Model model) {
        if (userService.findByLoginId(form.getLoginId()).isPresent()) {
            //사용자가 입력한 값(거절된 값)
            bindingResult.rejectValue("loginId", "signupFail", "아이디가 중복됩니다!");
        }
        if (userService.findByNickname(form.getNickname()).isPresent()) {
            bindingResult.rejectValue("nickname", "signupFail", "닉네임이 중복됩니다!");
        }
        if (!form.getPassword().equals(form.getPasswordCheck())) {
            bindingResult.rejectValue("passwordCheck", "signupFail", "비밀번호가 일치하지 않습니다!");
        }
        //에러 체크 메서드
        if (bindingResult.hasErrors()) {
            log.info("회원가입 실패");
            return "user/signupForm";
        }

        User user = new User();
        user.setLoginId(form.getLoginId());
        user.setNickname(form.getNickname());
        user.setPassword( bCryptPasswordEncoder.encode(form.getPassword()) );
        user.setRole("BRONZE");
        user.setSignupDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        user.setLikeCount(0);
        userService.signup(user);
        log.info("회원가입 성공 : {}", user.getNickname());

        model.addAttribute("msg", "회원가입에 성공했습니다! 로그인해주세요!");
        model.addAttribute("url", "/");
        return "message";
    }

    @GetMapping("/login")
    public String loginForm(@ModelAttribute UserLoginForm form) {
        return "user/loginForm";
    }

    @GetMapping("/logout")
    //HttpServletRequest는 클라이언트로부터 서버로 요청이 들어오면 서버에서는 HttpServletRequest를 생성하며
    //요청정보에 있는 패스로 매핑된 서블릿에게 전달합니다.
    public String logout(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            sessionList.remove(session.getId());
            session.invalidate();
        }
        log.info("로그아웃 성공");
        model.addAttribute("msg", "로그아웃 되었습니다!");
        model.addAttribute("url", "/");
        return "message";
    }

    @GetMapping("/edit")
    //@AuthenticationPrincipal 어노테이션을 사용하면 UserDetailsService에서 Return한 객체를 파라미터로 직접 받아 사용할 수 있다.
    public String editForm(@AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        User loginUser = principalDetails.getUser();
        UserEditForm form = new UserEditForm();
        form.setLoginId(loginUser.getLoginId());
        form.setOldNickname(loginUser.getNickname());
        model.addAttribute("userEditForm", form);
        if(loginUser.getProvider() == null) {
            model.addAttribute("oauth", false);
        } else {
            model.addAttribute("oauth", true);
        }
        return "user/editForm";
    }

    @PostMapping("/edit")
    public String edit(@AuthenticationPrincipal PrincipalDetails principalDetails,
                       @Valid @ModelAttribute UserEditForm form, BindingResult bindingResult,
                       HttpServletRequest request, Model model) {
        User loginUser = principalDetails.getUser();
        if(loginUser.getProvider() == null) {
            if (!bCryptPasswordEncoder.matches(form.getOldPassword(), loginUser.getPassword())) {
                bindingResult.rejectValue("oldPassword", "editFail", "현재 비밀번호를 틀렸습니다!");
            }
            if (!form.getPassword().equals(form.getPasswordCheck())) {
                bindingResult.rejectValue("passwordCheck", "editFail", "비밀번호가 일치하지 않습니다!");
            }
            if (userService.findByNickname(form.getNickname()).isPresent() && !loginUser.getNickname().equals(form.getNickname())) {
                bindingResult.rejectValue("nickname", "editFail", "닉네임이 중복됩니다!");
            }

            if (bindingResult.hasErrors()) {
                log.info("정보수정 실패");
                model.addAttribute("oauth", false);
                return "user/editForm";
            }
        } else {
            if (userService.findByNickname(form.getNickname()).isPresent() && !loginUser.getNickname().equals(form.getNickname())) {
                bindingResult.rejectValue("nickname", "editFail", "닉네임이 중복됩니다!");
            }
            if (bindingResult.hasFieldErrors("nickname")) {
                log.info("정보수정 실패");
                model.addAttribute("oauth", true);
                return "user/editForm";
            }
        }

        User user = userService.findById(loginUser.getId());

        user.setNickname(form.getNickname());
        if(loginUser.getProvider() == null) {
            user.setPassword(bCryptPasswordEncoder.encode(form.getPassword()));
        }
        userService.edit(user);

        log.info("정보수정 성공 : {}", user.getNickname());

        HttpSession session = request.getSession(false);
        if (session != null) {
            sessionList.remove(session.getId());
            session.invalidate();
        }

        model.addAttribute("msg", "정보수정 성공! 다시 로그인 해주세요!");
        model.addAttribute("url", "/user/login");
        return "message";
    }

    @GetMapping("/list")
    public String userList(Model model, @RequestParam(required = false, defaultValue = "1") int page) {
        model.addAttribute("users", userService.findAll(page - 1));
        if (page == 1) {
            model.addAttribute("hasPreviousPage", false);
        } else {
            model.addAttribute("hasPreviousPage", true);
        }

        if (userService.findAll(page).isEmpty()) {
            model.addAttribute("hasNextPage", false);
        } else {
            model.addAttribute("hasNextPage", true);
        }
        model.addAttribute("lastPage", userService.findLastPage());
        model.addAttribute("nowPage", page);

        return "user/userList";
    }

    @GetMapping("/delete")
    public String deleteForm(@ModelAttribute UserLoginForm form, Model model,
                             @AuthenticationPrincipal PrincipalDetails principalDetails) {
        User loginUser = principalDetails.getUser();
        if(loginUser.getProvider() == null) {
            model.addAttribute("oauth", false);
        } else {
            model.addAttribute("oauth", true);
        }
        return "user/deleteForm";
    }

    @PostMapping("/delete")
    public String delete(@ModelAttribute UserLoginForm form, Model model, HttpServletRequest request,
                         @AuthenticationPrincipal PrincipalDetails principalDetails) {
        User loginUser = principalDetails.getUser();
        if(loginUser.getProvider() == null) {
            if (!bCryptPasswordEncoder.matches(form.getPassword(), loginUser.getPassword())) {
                model.addAttribute("msg", "비밀번호가 틀렸습니다!");
                model.addAttribute("url", "/user/delete");
                log.info("회원탈퇴 실패");
                return "message";
            }
        }

        userService.delete(loginUser.getId());
        log.info("회원탈퇴 성공");
        HttpSession session = request.getSession(false);
        if (session != null) {
            sessionList.remove(session.getId());
            session.invalidate();
        }
        model.addAttribute("msg", "회원탈퇴 되었습니다!");
        model.addAttribute("url", "/");
        return "message";
    }

}
