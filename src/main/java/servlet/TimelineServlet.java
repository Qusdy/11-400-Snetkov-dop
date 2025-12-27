package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.Launch;
import service.LaunchService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = "/api/timeline")
public class TimelineServlet extends HttpServlet {
    private LaunchService service;
    private ObjectMapper mapper;

    @Override
    public void init() {
        this.service = (LaunchService) getServletContext().getAttribute("launchService");
        this.mapper = (ObjectMapper) getServletContext().getAttribute("objectMapper");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("launchesAll") == null) {
            resp.sendError(409, "Not loaded. POST /api/launches/load first");
            return;
        }

        @SuppressWarnings("unchecked")
        List<Launch> all = (List<Launch>) session.getAttribute("launchesAll");

        resp.setContentType("application/json; charset=utf-8");
        mapper.writeValue(resp.getWriter(), service.timeline(all));
    }
}