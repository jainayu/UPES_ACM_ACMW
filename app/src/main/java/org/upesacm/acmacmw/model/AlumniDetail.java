package org.upesacm.acmacmw.model;

public class AlumniDetail {

    private String Name;
    private String Position;
    private String Session;
    private String Image;
    private String Contact;
    private String Linkedin;

    public AlumniDetail() {
    }

    public AlumniDetail(String name, String position, String session, String image, String contact, String linkedin) {
        Name = name;
        Position = position;
        Session = session;
        Image = image;
        Contact = contact;
        Linkedin = linkedin;
    }

    public String getName() {
        return Name;
    }

    public String getPosition() {
        return Position;
    }

    public String getSession() {
        return Session;
    }

    public String getImage() {
        return Image;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setPosition(String position) {
        Position = position;
    }

    public void setSession(String session) {
        Session = session;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getLinkedin() {
        return Linkedin;
    }

    public void setLinkedin(String linkedin) {
        Linkedin = linkedin;
    }
}
