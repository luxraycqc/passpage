package org.chuxian.passpage.utils;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

public final class ServletUtils {
	public static void web(final HttpServletResponse response, final String content)
			throws ServletException, IOException {
		response.setContentType("text/json; charset=UTF-8");
		response.setHeader("Pragma", "no-cache");
		response.addHeader("Cache-Control", "must-revalidate");
		response.addHeader("Cache-Control", "no-cache");
		response.addHeader("Cache-Control", "no-store");
		response.setDateHeader("Expires", 0);
		response.getWriter().write(content);
	}

	public static void saveFile(final HttpServletResponse response, final String content, final String filename)
			throws ServletException, IOException {
		response.setContentType("text/json; charset=UTF-8");
		response.setHeader("Location", filename);
		response.setHeader("Content-Disposition", "attachment; filename=" + filename);
		response.getWriter().write(content);
	}
}
