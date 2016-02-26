package com.qbit;

import com.google.gson.Gson;
import com.qbit.Objects.ActivationRequest;
import com.qbit.Objects.ActivationResponse;
import com.qbit.Objects.Customer;
import com.qbit.Utils.DatabaseConnection;
import com.qbit.Utils.EmailManager;
import com.qbit.Utils.Encryptor;

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
    private Encryptor encryptor;

    @Override
    public void init() throws ServletException {
        super.init();
        encryptor = Encryptor.getInstance(24, 11);

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

        try {
            EmailManager.generateAndSendEmail(activationRequest.getEmail(), encryptor.encrypt(activationRequest.getEmail()), activationRequest.getName());
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
