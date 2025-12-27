package filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Instant;

@WebFilter("/*")
public class RequestLoggingFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        String path = req.getRequestURI();
        String ts = Instant.now().toString();

        String year = req.getParameter("year");
        String status = req.getParameter("status");
        String rocket = req.getParameter("rocket");
        String q = req.getParameter("q");
        String launchId = req.getParameter("id");

        try {
            log.info("view ts={} path={} launchId={} filters: year={} status={} rocket={} q={}",
                    ts, path, launchId, year, status, rocket, q);

            chain.doFilter(request, response);

        } catch (Throwable e) {
            log.error("ERROR ts={} path={} launchId={} filters: year={} status={} rocket={} q={}",
                    ts, path, launchId, year, status, rocket, q, e);

            if (e instanceof ServletException) throw (ServletException) e;
            if (e instanceof IOException) throw (IOException) e;
            throw new ServletException(e);
        }
    }
}