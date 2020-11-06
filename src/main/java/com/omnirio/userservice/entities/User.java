package com.omnirio.userservice.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.scripting.support.ResourceScriptSource;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "user")
public class User extends RepresentationModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id", updatable = false, insertable = false)
	private long userId;

	@Column(name = "first_name", nullable = false)
	private String userName;
	@Column(name = "dob")
	private String dateOfBirth;

	@Column(name = "gender", nullable = false, length = 2)
	private String gender;

	@Column(name = "phone", nullable = false)
	private String phoneNumber;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
	@JsonIgnore
	private Role role;

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		role.setUser(this);
		this.role = role;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", userName=" + userName + ", dateOfBirth=" + dateOfBirth + ", gender="
				+ gender + ", phoneNumber=" + phoneNumber + "]";
	}

}
