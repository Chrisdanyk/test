package org.reservation.login;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import org.reservation.web.service.UserService;

@Named
@SessionScoped
public class Login implements Serializable {

    private static final long serialVersionUID = 1094801825228386363L;

    private String pwd;
    private String msg;
    private String user;
    private boolean visible = false;
    private boolean admin = false;
    
    //@Inject 
    private UserService userService;

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    
    
    
    
//    public boolean isAdmin() {
//        return userConnected.getRole().equals("admin");
//    }
//    
//    public void setAdmin(boolean admin) {
//        this.admin = admin;
//    }

//    private static Users userConnected;
//
//    public static Users getUserConnected() {
//        return userConnected;
//    }
//
//    public void setUserConnected(Users userConnected) {
//        this.userConnected = userConnected;
//    }
    public boolean isVisible() {
        return visible;
    }

    public void setVisible() {
        this.visible = true;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    //validate login
    public String validateUsernamePassword() {
        
        boolean valid = user.equals("admin") && pwd.equals("admin");
        System.out.println("your admin is logged in");
        if (valid) {
            admin = true;
//            System.out.println("userConnected = " + userConnected);
            HttpSession session = SessionBean.getSession();
            session.setAttribute("username", user);
            return "index";
        } else {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Nom d'utilisateur ou mot de passe incorrect",
                            "veillez entrer des données correctes"));
            return "signin";
        }

    }

    //logout event, invalidate session
    public String logout() {
        HttpSession session = SessionBean.getSession();
        session.invalidate();
        return "/login";
    }
}
