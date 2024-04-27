package money;

import java.io.IOException;
import java.net.ResponseCache;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet{

	private static final String Classname="com.mysql.cj.jdbc.Driver";
	private static final String Db_url="jdbc:mysql://localhost:3306/moneytransfer?user=root&password=root";
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String useremail=req.getParameter("useremail");
		String userpassword=req.getParameter("userpass");
		String username=req.getParameter("username");
		long usermob=Long.parseLong(req.getParameter("usermob"));
		String userbank=req.getParameter("userbank");
		String useraccnumber=req.getParameter("useraccnumber");
		
		try {
			Class.forName(Classname);
			Connection c=DriverManager.getConnection(Db_url);
			
			//check if the useremail already exists
			PreparedStatement ps=c.prepareStatement("select count(*) from user_table where useremail=?");
			ps.setString(1,useremail);
			ResultSet rs= ps.executeQuery();
			
			if(rs.next() && rs.getInt(1)>0)
			{
				resp.getWriter().println("useremil already exists");
				return;
			}
			//check if the user account number is already exists
			
			//insert the new user
			PreparedStatement ps1=c.prepareStatement("insert into user_table (username,useremail,userpassword,userbank,useraccnumber,usermob,balance) values(?,?,?,?,?,?,50000)");
			ps1.setString(1, username);
			ps1.setString(2, useremail);
			ps1.setString(3, userpassword);
			ps1.setString(4, userbank);
			ps1.setString(5, useraccnumber);
			ps1.setLong(6, usermob);
			
			ps1.executeUpdate();
			resp.getWriter().println("Signup successfull");
		} catch (Exception e) {
			resp.getWriter().println("Error :"+e.getMessage());
		}
	}
}
