package com.github.hackerwin7.jlib.utils.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/11/30
 * Time: 3:42 PM
 * Desc:
 * Tips:
 */
public class HelloWorldTest extends HttpServlet {
    private String msg;

    @Override
    public void init() throws ServletException {
        msg = "hello world!";
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.println("<h1>" + msg + "</h1>");
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
