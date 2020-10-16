package de.java2enterprise.onlineshop;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import de.java2enterprise.onlineshop.model.Customer_;

@WebServlet("/buy")
public class BuyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Resource
    private DataSource ds;

    public void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        Long itemId = Long
                .parseLong(request.getParameter("item_id"));
        HttpSession session = request.getSession();
        Customer_ customer = (Customer_) session
                .getAttribute("customer");

        try {
            update(itemId, customer.getId());
        } catch (Exception e) {
            throw new ServletException(e.getMessage());
        }
        RequestDispatcher dispatcher = request
                .getRequestDispatcher("search");
        dispatcher.forward(request, response);
    }

    public void update(Long itemId, Long buyerId)
            throws Exception {
        try (final Connection con = ds.getConnection();
                final PreparedStatement stmt = con
                        .prepareStatement(
                                "UPDATE mapshop.item " +
                                        "SET buyer_id = ?, "
                                        +
                                        "sold = SYSTIMESTAMP "
                                        +
                                        "WHERE id = ?")) {
            stmt.setLong(1, buyerId);
            stmt.setLong(2, itemId);
            stmt.executeUpdate();
        }
    }
}
