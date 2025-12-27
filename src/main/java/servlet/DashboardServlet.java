package servlet;

import dto.Launch;
import service.LaunchService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.util.List;

@WebServlet(urlPatterns = "/dashboard")
public class DashboardServlet extends HttpServlet {
    private LaunchService service;

    @Override
    public void init() {
        this.service = (LaunchService) getServletContext().getAttribute("launchService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        try {
            HttpSession session = req.getSession(false);

            boolean loaded = session != null && session.getAttribute("launchesAll") != null;
            req.setAttribute("loaded", loaded);

            if (loaded) {
                List<Launch> all = (List<Launch>) session.getAttribute("launchesAll");

                req.setAttribute("loadedAtMs", session.getAttribute("launchesLoadedAtMs"));
                req.setAttribute("stats", service.stats(all));

                List<Launch> sortedAsc = service.timeline(all);
                int total = sortedAsc.size();
                int from = Math.max(0, total - 200);

                req.setAttribute("timelineTotal", total);
                req.setAttribute("timeline", sortedAsc.subList(from, total));
            }

            req.getRequestDispatcher("/dashboard.ftl").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
