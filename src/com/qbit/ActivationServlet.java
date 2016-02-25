package com.qbit;

import com.google.gson.Gson;
import com.qbit.Objects.ActivationRequest;
import com.qbit.Objects.ActivationResponse;
import com.qbit.Objects.Customer;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * User: cbates
 */
public class ActivationServlet extends HttpServlet{
    private static final long serialVersionID = 1L;

    private DatabaseConnection dbConnection;

    @Override
    public void init() throws ServletException {
        super.init();

        dbConnection = new DatabaseConnection();
        try {
            if (!dbConnection.isDatabaseCreated()) {
                dbConnection.createDatabase();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        response.setContentType("text/html");
//
//        PrintWriter out = response.getWriter();
//        out.println("Activation Successful");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();

        ActivationRequest activationRequest = new ActivationRequest();
        String json = request.getReader().readLine();
        if (json != null) {
            activationRequest = gson.fromJson(json, ActivationRequest.class);
        }

        // TODO: Take the email from the request and generate activation code

        try {
            EmailManager.generateAndSendEmail(activationRequest.getEmail(), "98165198652132");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        Customer customer = new Customer(activationRequest.getName(), activationRequest.getEmail(), true);
        try {
            dbConnection.insert(activationRequest.getName(), activationRequest.getEmail(), true);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        ActivationResponse activationResponse = new ActivationResponse();
        activationResponse.setSuccess(customer.isActivated());

        response.getOutputStream().print(gson.toJson(activationResponse));
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
