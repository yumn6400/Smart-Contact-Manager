package com.smart.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name="USER")
public class User {


@Id
@GeneratedValue(strategy=GenerationType.AUTO)
private int id;

@NotBlank(message="Invalid username")
@Size(min=3,max=20,message="Username should be in between 3-20 characters")
private String name;

@Column(unique=true)
@Email(regexp="^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")
private String email;

@NotBlank(message="Invalid password")
private String password;

private String role;


@AssertTrue(message="Must agree terms and conditions")
private boolean agreed;

public boolean isAgreed() {
	return agreed;
}
public void setAgreed(boolean agreed) {
	this.agreed = agreed;
}

private String imageUrl;

@Column(length=500)
private String about;

@OneToMany(cascade=CascadeType.ALL ,fetch=FetchType.LAZY , mappedBy="user")
private List<Contact> contacts=new ArrayList<Contact>();



public List<Contact> getContacts() {
	return contacts;
}
public void setContacts(List<Contact> contacts) {
	this.contacts = contacts;
}
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getEmail() {
	return email;
}
public void setEmail(String email) {
	this.email = email;
}
public String getPassword() {
	return password;
}
public void setPassword(String password) {
	this.password = password;
}
public String getRole() {
	return role;
}
public void setRole(String role) {
	this.role = role;
}
public String getImageUrl() {
	return imageUrl;
}
public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
}
public String getAbout() {
	return about;
}
public void setAbout(String about) {
	this.about = about;
}
public User(int id, String name, String email, String password, String role, boolean agreed, String imageUrl,
		String about) {
	super();
	this.id = id;
	this.name = name;
	this.email = email;
	this.password = password;
	this.role = role;
	this.agreed=agreed;
	this.imageUrl = imageUrl;
	this.about = about;
}
public User() {
	super();
	// TODO Auto-generated constructor stub
}

@Override
public String toString() {
	return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", role=" + role
			+ ", agreed=" + agreed + ", imageUrl=" + imageUrl + ", about=" + about + ", contacts=" + contacts
			+ "]";
}
}
