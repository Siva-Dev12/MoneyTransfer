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
		
		String username=req.getParameter("username");
		String useraccnumber=req.getParameter("useraccnumber");
		String useremail=req.getParameter("useremail");
		String password=req.getParameter("userpassword");
		
		try {
			Class.forName(Classname);
			Connection c=DriverManager.getConnection(Db_url);
			PreparedStatement ps=c.prepareStatement("select * from user_table where useremail=? and userpassword=? and useraccnumber=? and username=?");
			
			ps.setString(1,useremail);
			ps.setString(2,password);
			ps.setString(3,useraccnumber);
			ps.setString(4, username);
			
			ResultSet rs=ps.executeQuery();
			
			if(rs.next())
			{
				HttpSession session=req.getSession(true);
				session.setAttribute("username", username);
				session.setAttribute("useremail",useremail);
				session.setAttribute("userpassword",password);
				session.setAttribute("useraccnumber",useraccnumber);
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
