package money;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/transactionHistory")
public class TransactionHistoryServlet extends HttpServlet {
	private static final String Classname="com.mysql.cj.jdbc.Driver";
	private static final String Db_url="jdbc:mysql://localhost:3306/moneytransfer?user=root&password=root";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session=req.getSession(false);
		if(session==null || session.getAttribute("useraccnumber")==null )
		{
			resp.sendRedirect("signup.html");
			return;
		}
		
		String useraccnumber=(String) session.getAttribute("useraccnumber");
		
		PrintWriter out=resp.getWriter();
		
		try {
			Class.forName(Classname);
			Connection c=DriverManager.getConnection(Db_url);
			PreparedStatement ps=c.prepareStatement("select * from transactions_table where senderaccnumber=?");
			
			ps.setString(1,useraccnumber);
			
            ResultSet rs = ps.executeQuery();
            out.println("<h1>Transaction History of Account Number : " + useraccnumber + "</h1>");
            out.println("<ul>");

            while (rs.next()) {
            	
                out.println("<table border=2px cellspacing=0px cellpadding=4px>");
                out.println("<tr>");
                out.println("<th>SENDER_NAME</th>");
                out.println("<th>SENDER_ACCNUMBER</th>");
                out.println("<th>RECEIVER_NAME</th>");
                out.println("<th>RECEIVER_MOBILE</th>");
                out.println("<th>RECEIVER_BANK</th>");
                out.println("<th>RECEIVER_ACCNUMBER</th>");
                out.println("<th>RECEIVER_IFSC</th>");
                out.println("<th>AMOUNT</th>");
                out.println("<br>");
                out.println("<br>");
                out.println("</tr>");
                out.println("<tr>");
                out.println("<td>"+ rs.getString("sendername") +"</td>");
                out.println("<td>"+ rs.getString("senderaccnumber") +"</td>");
                out.println("<td>"+ rs.getString("receivername") +"</td>");
                out.println("<td>"+ rs.getLong("receivermob") +"</td>");
                out.println("<td>"+ rs.getString("receiverbankname") +"</td>");
                out.println("<td>"+ rs.getLong("receiveraccnumber") + "</td>");
                out.println("<td>"+ rs.getString("receiveraifsc") +"</td>");
                out.println("<td>"+ rs.getDouble("amount")+"</td>");
                out.println("</tr>");
                out.println("</table>");
            }
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
