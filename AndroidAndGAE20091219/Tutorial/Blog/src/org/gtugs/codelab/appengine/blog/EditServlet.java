package org.gtugs.codelab.appengine.blog;

import java.io.IOException;
import java.net.URLEncoder;

import javax.jdo.PersistenceManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gtugs.codelab.appengine.blog.datastore.Post;

public class EditServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");

		String sid = req.getParameter("id");
		Long id = null;
		try {
			id = Long.parseLong(sid);
		} catch (NumberFormatException e) {
			// TODO
			e.printStackTrace();
		}

		PersistenceManager pm = PMF.get().getPersistenceManager();
		Post post = pm.getObjectById(Post.class, id);

		StringBuilder sb = new StringBuilder();
		sb.append("/admin/view.jsp");
		sb.append("?id=" + post.getId());
		sb.append("&title=" + URLEncoder.encode(post.getTitle(), "UTF-8"));
		sb.append("&content=" + URLEncoder.encode(post.getContent(), "UTF-8"));

		ServletContext sc = getServletContext();
		RequestDispatcher dispatcher = sc.getRequestDispatcher(sb.toString());
		try {
			dispatcher.forward(req, resp);
		} catch (ServletException e) {
			// TODO
			e.printStackTrace();
		}
	}
}
