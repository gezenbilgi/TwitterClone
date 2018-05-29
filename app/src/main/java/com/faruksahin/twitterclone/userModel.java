package com.faruksahin.twitterclone;

public class userModel
{
    private String name,mail,username,photo;

    public void setName(String name) {
        this.name = name;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName()
    {
        return name;
    }

    public String getMail()
    {
        return mail;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPhoto()
    {
        return photo;
    }
}
