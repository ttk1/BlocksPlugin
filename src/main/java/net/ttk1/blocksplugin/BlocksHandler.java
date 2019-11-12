package net.ttk1.blocksplugin;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class BlocksHandler extends AbstractHandler {
    private final BlocksPlugin plugin;

    BlocksHandler(BlocksPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // log
        plugin.getLogger().info(target);

        // process
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter writer = response.getWriter();
        writer.println("hello, world!");
        baseRequest.setHandled(true);
    }
}
