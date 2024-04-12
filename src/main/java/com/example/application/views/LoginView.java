package com.example.application.views.movie;

import com.example.application.controller.UserController;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@PageTitle("Login View")
@Route(value = "login", layout = MainLayout.class)
@RouteAlias(value = "" , layout = MainLayout.class)
@Uses(Icon.class)

public class LoginView extends Composite<VerticalLayout> {

    private final UserController userController;

//    public addLoginView(MovieController movieController, MovieController movieController1) {
//        this.movieController = movieController1;
//    }
    public LoginView(UserController userController) {
        this.userController = userController;

        LoginForm loginForm = new LoginForm();

        loginForm.addLoginListener(e->{
            String username = e.getUsername();
            String password = e.getPassword();

            String User = userController.checkUser(username, password);
            if (User==null)
            {
                Notification.show("User not found");
            }
            else
            {
                if(User.equals("user"))
                    UI.getCurrent().navigate("user");
                else if(User.equals("admin"))
                    UI.getCurrent().navigate("admin");
            }
        });

        VerticalLayout layout = getContent();
        layout.setWidth("100%");
        layout.getStyle().set("flex-grow", "1");
        layout.setSizeFull();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        //loginForm.setWidth("100%");
        getContent().add(loginForm);
    }
}