package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.Launch;
import service.LaunchService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = "/api/launches")
public class LaunchesApiServlet extends HttpServlet {
    private LaunchService service;
    private ObjectMapper mapper;

    @Override
    public void init() {
        this.service = (LaunchService) getServletContext().getAttribute("launchService");
        this.mapper = (ObjectMapper) getServletContext().getAttribute("objectMapper");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("launchesAll") == null) {
                resp.sendError(409, "Not loaded. POST /api/launches/load first");
                return;
            }

            @SuppressWarnings("unchecked")
            List<Launch> all = (List<Launch>) session.getAttribute("launchesAll");

            Integer year = parseIntOrNull(req.getParameter("year"));
            String status = req.getParameter("status");     // success|failure|all
            String rocket = req.getParameter("rocket");     // Falcon 1|Falcon 9|Falcon Heavy
            String q = req.getParameter("q");
            Integer limit = parseIntOrNull(req.getParameter("limit"));
            if (limit == null) limit = 20;

            List<Launch> filtered = service.filter(all, year, status, rocket, q);
            List<Launch> last = service.lastN(filtered, limit);

            resp.setContentType("application/json; charset=utf-8");
            mapper.writeValue(resp.getWriter(), last);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private Integer parseIntOrNull(String s) {
        if (s == null || s.isBlank()) return null;
        return Integer.parseInt(s);
    }
}