package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.Launch;
import service.LaunchService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/api/launch")
public class LaunchesDetailServlet extends HttpServlet {
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
            String id = req.getParameter("id");
            if (id == null || id.isBlank()) {
                resp.sendError(400, "Missing id");
                return;
            }
            Launch data = service.launchDetail(id);
            resp.setContentType("application/json; charset=utf-8");
            mapper.writeValue(resp.getWriter(), data);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
