package Study.Board.auth;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    //요청을 받을 때 전달 받은 정보를 HttpServletRequest객체를 생성하여 저장
    //웹브라우져에게 응답을 돌려줄 HttpServletResponse객체를 생성(빈 객체)
    //생성된 HttpServletRequest(정보가 저장된)와 HttpServletResponse(비어 있는)를 Servlet에게 전달
    //HttpServletRequest를 사용하면, 값을 받아올 수가 있는데
    //만약 회원 정보를 컨트롤러로 보냈을 때 HttpServletRequest 객체 안에 모든 데이터들이 들어가게 된다.
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        if(request.getRequestURI().equals("/content/list/1")) {
            response.sendRedirect("/message?url=/&msg=" + URLEncoder.encode("공지사항은 관리자만 작성 가능합니다!", "UTF-8"));
        } else {
            response.sendRedirect("/message?url=/&msg=" + URLEncoder.encode("골드이상만 접근가능합니다.", "UTF-8"));
        }
    }
}
