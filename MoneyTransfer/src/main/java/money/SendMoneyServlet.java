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

@WebServlet("/sendmoney")
public class SendMoneyServlet extends HttpServlet{
	private static final String Classname="com.mysql.cj.jdbc.Driver";
	private static final String Db_url="jdbc:mysql://localhost:3306/moneytransfer?user=root&password=root";
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session=req.getSession(false);
		if(session==null || session.getAttribute("useremail")==null )
		{
			resp.sendRedirect("login.html");
			return;
		}
		
		String sender=(String) session.getAttribute("useremail");
		String receiver=req.getParameter("receiver");
		double amount=Double.parseDouble(req.getParameter("amount"));
		
		PrintWriter out=resp.getWriter();
		
		try {
			Class.forName(Classname);
			Connection c=DriverManager.getConnection(Db_url);
			c.setAutoCommit(false);
			
			//check if sender has enough money
			PreparedStatement checkbalance=c.prepareStatement("select balance from user_table where useremail=?");
			checkbalance.setString(1,sender);
			ResultSet rs=checkbalance.executeQuery();
			if(rs.next() && rs.getDouble("balance") >=amount)
			{
				double senderbalance=rs.getDouble("balance");
				
				//deduct from sender table
				PreparedStatement deduct=c.prepareStatement("update user_table set balance=? where useremail=?");
				deduct.setDouble(1, senderbalance-amount);
				deduct.setString(2,sender);
				deduct.executeUpdate();
				
				//add to receiver table
				PreparedStatement set=c.prepareStatement("insert into receiver_table (receivername,balance) values (?,0)");
				set.setString(1, receiver);
				set.executeUpdate();
				
				PreparedStatement add=c.prepareStatement("update receiver_table set receivername=? ,balance=balance +? where receivername=?");
				add.setString(1, receiver);
				add.setDouble(2,amount);
				add.setString(3, receiver);
				add.executeUpdate();
				
				//add into the transaction table
                PreparedStatement logTransactionStmt = c.prepareStatement(
                        "insert into transactions_table (sender, receiver, amount) values (?, ?, ?)"
                    );
                    logTransactionStmt.setString(1, sender);
                    logTransactionStmt.setString(2, receiver);
                    logTransactionStmt.setDouble(3, amount);
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
