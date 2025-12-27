package servlet;

import service.LaunchService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
            req.setAttribute("nextLaunch", service.nextUpcoming().orElse(null));
            req.getRequestDispatcher("/launches.ftl").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
