package controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;

import DAO.UserDao;
import DAO.VideoDao;
import model.User;
import model.Video;

@WebServlet(urlPatterns = { "/", "/404", "/myFavourites" }, loadOnStartup = 10)
public class HomeController extends HttpServlet {
	private static Map<Integer, List<Video>> videos = new HashMap<Integer, List<Video>>();
	private static int total = VideoDao.findAllPage(30);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("here");
		String path = req.getServletPath();
		System.out.println(path);
		switch (path) {
		case "/":
			String page = req.getParameter("page");
			Integer pageNumber = 0;

			if (page == null) {
				pageNumber = 0;
			} else {
				try {
					pageNumber = Integer.valueOf(page);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			
			List<Video> list = videos.get(pageNumber);
			if (list == null) {
				list = VideoDao.findAllByPage(pageNumber, 30);
//				videos.put(pageNumber, list);
			}
			req.setAttribute("page", pageNumber);
			req.setAttribute("total", total);
			req.setAttribute("pageTitle", "Online videos");
			req.setAttribute("videoList", list);
			req.getRequestDispatcher("/views/index.jsp").forward(req, resp);
			break;
		case "/myFavourites":
			myFavourites(req, resp);
			break;
		case "/404":
			req.setAttribute("pageTitle", "404 Not Found");
			req.getRequestDispatcher("/views/404.jsp").forward(req, resp);
			break;
		default:
			resp.sendRedirect(req.getContextPath() + "/404");
			break;
		}
	}

	public void myFavourites(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		User u = (User) session.getAttribute("user_Loged");
		if (u != null) {
			req.setAttribute("pageTitle", "My favourite videos");
			req.setAttribute("videoList", u.getMyVideos());
			req.getRequestDispatcher("/views/myFavourite.jsp").forward(req, resp);
		} else {
			resp.getWriter().print("You don't have permission to visit this site!");
		}
	}
}
