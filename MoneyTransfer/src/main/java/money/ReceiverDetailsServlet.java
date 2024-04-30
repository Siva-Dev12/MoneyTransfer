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
import javax.servlet.http.HttpSession;

@WebServlet("/receiverdetails")
public class ReceiverDetailsServlet extends HttpServlet{

	private static final String Classname="com.mysql.cj.jdbc.Driver";
	private static final String Db_url="jdbc:mysql://localhost:3306/moneytransfer?user=root&password=root";
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String receivername=req.getParameter("receivername");
		long receivermob=Long.parseLong( req.getParameter("receivermob"));
		String receiverbankname=req.getParameter("receiverbankname");
		long receiveraccnumber=Long.parseLong(req.getParameter("receiveraccnumber"));
		String receiverifsc=req.getParameter("receiverifsc");
		
		try {
			Class.forName(Classname);
			Connection c=DriverManager.getConnection(Db_url);
			
			//check if the receiver account already exists
			PreparedStatement ps=c.prepareStatement("select count(*) from receiver_table where receiveraccnumber=?");
			ps.setLong(1,receiveraccnumber);
			ResultSet rs= ps.executeQuery();
			
			if(rs.next() && rs.getInt(1)>0)
			{
				resp.getWriter().println("user Account is already exists");
				return;
			}		
			
			//insert the new user
			PreparedStatement ps1=c.prepareStatement("insert into receiver_table (receivername,receivermob,receiverbankname,receiveraccnumber,receiverifsc) values(?,?,?,?,?)");
			ps1.setString(1, receivername);
			ps1.setLong(2, receivermob);
			ps1.setString(3, receiverbankname);
			ps1.setLong(4, receiveraccnumber);
			ps1.setString(5, receiverifsc);
			
			ps1.executeUpdate();
			
			HttpSession session=req.getSession(true);
			session.setAttribute("receivername",receivername);
			session.setAttribute("receiverbankname",receiverbankname);
			session.setAttribute("receiveraccnumber",receiveraccnumber);
			session.setAttribute("receiverifsc",receiverifsc);
			
			resp.getWriter().println("receiver details saved successfully..");
			resp.sendRedirect("sendmoney.html");
		} catch (Exception e) {
			resp.getWriter().println("Error :"+e.getMessage());
		}
	}
}
