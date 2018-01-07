package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "form", urlPatterns = {"/form"})
public class form extends HttpServlet {

    private String getLastLogin(HttpServletRequest request) {
        if (request.getParameter("Login") != null) {
            return request.getParameter("Login");
        } else {
            return "";
        }
    }

    private Cookie getCookie(Cookie[] cookies, String name) {
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals(name)) {
                    return c;
                }

            }
        }
        return null;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"theme.css\">");
            out.println("</head>");
            out.println("<body>");
            out.println("<div>");

            Cookie cBlocage = getCookie(request.getCookies(), "BLOCAGE");

            if (cBlocage != null) { // s'il a trouvé un cookie
                out.println("<p style='color:red'>Trop de tenatatives</p><br>");
                out.println("<p style='color:red'>Dommage pour vous</p>");
            } else {
                Cookie cSession = getCookie(request.getCookies(), "SESSION");

                if (cSession != null) { // s'il a trouvé un cookie
                    out.println("Bienvenue Admin");
                    out.println("<a href = 'logout'>Se déconnecter</a>");

                } else {
                    out.println("<form name='form' action='form' method='POST'>");
                    out.println("Login : <input type='text' name='Login' value=" + getLastLogin(request) + "><br>");
                    out.println("Mot de passe : <input type='password' name='Password' value='' /><br>");
                    out.println("<input type='submit' value='Ok' name='doIt' />");
                    out.println("</form>");

                    if (request.getParameter("Login").equals("admin")
                            && request.getParameter("Password").equals("root")) {
                        out.println("<br>");
                        out.println("Bienvenue admin !");

                        Cookie cc = new Cookie("SESSION", "001");
                        response.addCookie(cc);

                    } else {

                        Cookie cDelai = getCookie(request.getCookies(), "DELAI");

                        if (cDelai != null) {

                            Cookie cTentatives = getCookie(request.getCookies(), "TENTATIVE");

                            if (cTentatives != null) {
                                int tentatives = Integer.valueOf(cTentatives.getValue());

                                if (tentatives >= 2) {
                                    Cookie cb = new Cookie("BLOCAGE", "000");
                                    cb.setMaxAge(30);
                                    response.addCookie(cb);
                                    out.println("<p style='color:red'>Trop de tenatatives</p><br>");
                                    out.println("<p style='color:red'>Dommage pour vous</p>");

                                } else {
                                    tentatives++;
                                    cTentatives.setValue(String.valueOf(tentatives));
                                    response.addCookie(cTentatives);
                                    out.println("<p style='color:red'>Utilisateur / Mot de passe invalide !!</p>");
                                    out.println("<p style='color:red'>Il vous reste " + (3 - tentatives) + " tentatives</p>");
                                }

                            } else {
                                cTentatives = new Cookie("TENTATIVE", "1");
                                response.addCookie(cTentatives);
                                out.println("<p style='color:red'>Utilisateur / Mot de passe invalide !!</p>");
                                out.println("<p style='color:red'>Il vous reste 2 tentatives</p>");
                            }

                        } else {
                            Cookie cd = new Cookie("DELAI", "000");
                            cd.setMaxAge(30);
                            response.addCookie(cd);

                            Cookie ct = new Cookie("TENTATIVE", "1");
                            response.addCookie(ct);
                            out.println("<p style='color:red'>Utilisateur / Mot de passe invalide !!</p>");
                            out.println("<p style='color:red'>Il vous reste 2 tentatives</p>");
                        }

                    }
                }

            }

            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
