package money;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/sendmoney")
public class SendMoneyServlet extends HttpServlet{
	private static final String Classname="com.mysql.cj.jdbc.Driver";
	private static final String Db_url="jdbc:mysql://localhost:3306/moneytransfer?user=root&password=root";
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session=req.getSession(false);
				
		if(session==null || session.getAttribute("receiveraccnumber")==null )
		{
			resp.sendRedirect("sendmoney.html");
			return;
		}
		
		String sender=(String) session.getAttribute("username");
		String useraccnumber=(String) session.getAttribute("useraccnumber");
		String receivername=(String)session.getAttribute("receivername");
		long receivermob=Long.parseLong(req.getParameter("receivermob"));
		String receiverbankname=(String)session.getAttribute("receiverbankname");
		long receiveraccnumber =Long.parseLong(req.getParameter("receiveraccnumber"));
		String receiverifsc=(String)session.getAttribute("receiverifsc");
		double amount=Double.parseDouble(req.getParameter("amount"));
		
		PrintWriter out=resp.getWriter();
		
		try {
			Class.forName(Classname);
			Connection c=DriverManager.getConnection(Db_url);
			c.setAutoCommit(false);
			
			//check if sender has enough money
			PreparedStatement checkbalance=c.prepareStatement("select balance from user_table where useraccnumber=?");
			checkbalance.setString(1,useraccnumber);
			ResultSet rs=checkbalance.executeQuery();
			if(rs.next() && rs.getDouble("balance") >=amount)
			{
				double senderbalance=rs.getDouble("balance");
				
				//deduct from sender table
				PreparedStatement deduct=c.prepareStatement("update user_table set balance=? where useraccnumber=?");
				deduct.setDouble(1, senderbalance-amount);
				deduct.setString(2,useraccnumber);
				deduct.executeUpdate();
				
                PreparedStatement logTransactionStmt = c.prepareStatement(
                        "insert into transactions_table () values (?,?,?,?,?,?,?,?)");
                    logTransactionStmt.setString(1,sender);
                    logTransactionStmt.setString(2,useraccnumber);
                    logTransactionStmt.setString(3,receivername);
                    logTransactionStmt.setLong(4,receivermob);
                    logTransactionStmt.setString(5,receiverbankname);
                    logTransactionStmt.setLong(6,receiveraccnumber);
                    logTransactionStmt.setString(7,receiverifsc);
                    logTransactionStmt.setDouble(8,amount);
                    
//                  logTransactionStmt.setDate(4, currentdate);
                    
                    logTransactionStmt.executeUpdate();

                    c.commit();

                    out.println("Money transferred successfully.");
                } else {
                    c.rollback();
                    out.println("Insufficient funds.");
                }

                c.close();
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
