package servlet;

import dto.Launch;
import service.LaunchService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = "/dashboard/refresh")
public class DashboardRefreshServlet extends HttpServlet {
    private LaunchService service;

    @Override
    public void init() {
        this.service = (LaunchService) getServletContext().getAttribute("launchService");
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

            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ServletException(e);
        }
    }
}

