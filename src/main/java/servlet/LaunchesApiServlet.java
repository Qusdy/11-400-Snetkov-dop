package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.Launch;
import dto.QueryResponse;
import service.LaunchService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        try {
            Integer year = parseIntOrNull(req.getParameter("year"));
            String status = req.getParameter("status");     // success|failure|all
            String rocket = req.getParameter("rocket");     // Falcon 9 ...
            String search = req.getParameter("q");

            QueryResponse<Launch> data = service.lastLaunches(20, year, status, rocket, search);

            resp.setContentType("application/json; charset=utf-8");
            mapper.writeValue(resp.getWriter(), data);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private Integer parseIntOrNull(String s) {
        if (s == null || s.isBlank()) return null;
        return Integer.parseInt(s);
    }
}
