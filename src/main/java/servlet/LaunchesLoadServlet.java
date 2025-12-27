package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.Launch;
import service.LaunchService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = "/api/launches/load")
public class LaunchesLoadServlet extends HttpServlet {
    private LaunchService service;
    private ObjectMapper mapper;

    @Override
    public void init() {
        this.service = (LaunchService) getServletContext().getAttribute("launchService");
        this.mapper = (ObjectMapper) getServletContext().getAttribute("objectMapper");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Launch> launches = service.loadAllLaunches();
            Map<String, String> rocketMap = service.loadRocketMap();
            service.enrichRocketNames(launches, rocketMap);

            HttpSession session = req.getSession(true);
            session.setAttribute("launchesAll", launches);
            session.setAttribute("rocketMap", rocketMap);
            session.setAttribute("launchesLoadedAtMs", System.currentTimeMillis());

            resp.setContentType("application/json; charset=utf-8");
            mapper.writeValue(resp.getWriter(), Map.of(
                    "ok", true,
                    "launchCount", launches.size(),
                    "rocketCount", rocketMap.size()
            ));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ServletException(e);
        }
    }
}