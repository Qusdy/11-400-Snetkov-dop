package servlet;

import dto.Launch;
import service.LaunchService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.util.List;

@WebServlet(urlPatterns = "/launches")
public class LaunchesPageServlet extends HttpServlet {
    private LaunchService service;

    @Override
    public void init() {
        this.service = (LaunchService) getServletContext().getAttribute("launchService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        try {
            HttpSession session = req.getSession(false);
            Launch next = null;

            if (session != null && session.getAttribute("launchesAll") != null) {
                @SuppressWarnings("unchecked")
                List<Launch> all = (List<Launch>) session.getAttribute("launchesAll");
                next = service.nextUpcoming(all).orElse(null);
            }

            req.setAttribute("nextLaunch", next);
            req.getRequestDispatcher("/launches.ftl").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
