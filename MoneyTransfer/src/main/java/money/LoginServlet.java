package money;

import java.io.IOException;
import java.net.ResponseCache;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	private static final String Classname="com.mysql.cj.jdbc.Driver";
	private static final String Db_url="jdbc:mysql://localhost:3306/moneytransfer?user=root&password=root";
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String useremail=req.getParameter("useremail");
		String password=req.getParameter("userpassword");
		
		try {
			Class.forName(Classname);
			Connection c=DriverManager.getConnection(Db_url);
			PreparedStatement ps=c.prepareStatement("select * from user_table where useremail=? and userpassword=?");
			
			ps.setString(1,useremail);
			ps.setString(2,password);
			
			ResultSet rs=ps.executeQuery();
			
			Cookie c1=new Cookie("email",useremail);
			Cookie c2=new Cookie("pass",password);
			
			resp.addCookie(c1);
			c1.setMaxAge(60*60*24);
			resp.addCookie(c2);
			
			if(rs.next())
			{
				HttpSession session=req.getSession(true);
				session.setAttribute("useremail",useremail);
				session.setAttribute("userpassword",password);
				resp.sendRedirect("welcome.html");
			}else {
				resp.sendRedirect("login.html?error=invalid");
			}
		} catch (Exception e) {
			e.printStackTrace();
			resp.getWriter().println("Error: "+e.getMessage());
		}
		
	}
}
