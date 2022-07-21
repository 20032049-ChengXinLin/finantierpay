/**
*
* I declare that this code was written by me, 20032049.
* I will not copy or allow others to copy my code.
* I understand that copying code is considered as plagiarism.
*
* Student Name: Cheng Xin Lin (20032049), Koh Siew Gek (20008303), Chen Wan Ting (20009334)
* Team ID: SOI-2022-0012
* Team Project ID: SOI-2022-2210-0049
* Project: [IP] Digital Wallet
* Date created: 2022-May-04 4:19:02 pm
*
*/
package FYP.SOI20220012.DigitalWallet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;
/**
 * @author 20032049
 *
 */
@Entity
//Soft Delete Part
//@Table(name = "account")
//@SQLDelete(sql = "update account set isActive = 0 where id =?")
//@Where(clause = "isActive = 1")
//
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int accountId;
	
	@ManyToOne
	@JoinColumn(name = "countryId", nullable = false)
	@NotNull(message = "country name cannot be empty")
	private Country country;
	
	@OneToMany(mappedBy="account")
	private Set<Wallet> wallet;
	
	@OneToMany(mappedBy="account")
	private Set<Notifications> notification;
	
	@OneToMany(mappedBy="account")
	private Set<PointsEarned> pointsEarned;
	
	@OneToMany(mappedBy="account")
	private Set<Voucher> voucher;
	
	@OneToMany(mappedBy="account")
	private Set<ServiceRequest> help;
	
	@NotNull
	@NotEmpty(message = "First Name cannot be empty!")
	@Size(min = 3, max = 20, message = "First Name length must be between 3 and 20 characters!")
	private String first_name;

	@NotNull
	@NotEmpty(message = "Last Name cannot be empty!")
	@Size(min = 1, max = 20, message = "Last Name length must be between 1 and 20 characters!")
	private String last_name;

	@NotNull
	@NotEmpty(message = "Username cannot be empty!")
	@Size(min = 3, max = 15, message = "Username length must be between 3 and 15 characters!")
	private String username;

	@Email
	@NotNull
	@NotEmpty(message = "Email cannot be empty!")
	private String email;

	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date_of_birth;

	@NotNull
	@NotEmpty(message = "Phone cannot be empty!")
	private String phone;

	@NotNull
	@NotEmpty(message = "Password cannot be empty!")
	@Size(min=8, message = "Password must contains minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character")
	private String password;

	@NotNull
	@NotEmpty(message = "Address cannot be empty")
	@Size(min = 10, max = 250, message = "Address length must be between 10 and 250 characters!")
	private String address;

	@NotNull
	@NotEmpty(message = "Postal Code cannot be empty")
	private String postal_code;

	@NotNull
	@NotEmpty(message = "Account Type cannot be empty")
	private String role;
	
	@NotNull
	@NotEmpty(message = "Store Name cannot be empty")
	private String storeName;
	
	private String membership_levels;
	
	private double balance_points;
	
	private double total_points;
	
	private double cashback_voucher;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime last_login_date_time;

	@Column(name = "reset_password_token")
	private String resetPasswordToken;
	private boolean isEnabled;
	private String verificationCode;
	
	private boolean isLocked = Boolean.TRUE;
	
	private LocalDate date_deleted;

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public LocalDate getDate_of_birth() {
		return date_of_birth;
	}

	public void setDate_of_birth(LocalDate date_of_birth) {
		this.date_of_birth = date_of_birth;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostal_code() {
		return postal_code;
	}

	public void setPostal_code(String postal_code) {
		this.postal_code = postal_code;
	}
	
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getMembership_levels() {
		return membership_levels;
	}

	public void setMembership_levels(String membership_levels) {
		this.membership_levels = membership_levels;
	}

	public double getBalance_points() {
		return balance_points;
	}
	
	public void setBalance_points(double balance_points) {
		this.balance_points = balance_points;
	}
	
	public double getTotal_points() {
		return total_points;
	}

	public void setTotal_points(double total_points) {
		this.total_points = total_points;
	}

	public double getCashback_voucher() {
		return cashback_voucher;
	}

	public void setCashback_voucher(double cashback_voucher) {
		this.cashback_voucher = cashback_voucher;
	}

	public LocalDateTime getLast_login_date_time() {
		return last_login_date_time;
	}

	public void setLast_login_date_time(LocalDateTime last_login_date_time) {
		this.last_login_date_time = last_login_date_time;
	}

	public String getResetPasswordToken() {
		return resetPasswordToken;
	}

	public void setResetPasswordToken(String resetPasswordToken) {
		this.resetPasswordToken = resetPasswordToken;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

	public boolean isLocked() {
		return isLocked;
	}

	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}

	public LocalDate getDate_deleted() {
		return date_deleted;
	}

	public void setDate_deleted(LocalDate date_deleted) {
		this.date_deleted = date_deleted;
	}

	
//	public boolean isActive() {
//		return isActive;
//	}
//
//	public void setActive(boolean isActive) {
//		this.isActive = isActive;
//	}
//
//	@Override
//	public String toString() {
//		return "Account [accountId=" + accountId + ", country=" + country + ", first_name=" + first_name
//				+ ", last_name=" + last_name + ", username=" + username + ", email=" + email + ", date_of_birth="
//				+ date_of_birth + ", phone=" + phone + ", password=" + password + ", address=" + address
//				+ ", postal_code=" + postal_code + ", role=" + role + ", storeName=" + storeName
//				+ ", membership_levels=" + membership_levels + ", balance_points=" + balance_points + ", total_points="
//				+ total_points + ", cashback_voucher=" + cashback_voucher + ", last_login_date_time="
//				+ last_login_date_time + ", resetPasswordToken=" + resetPasswordToken + ", isEnabled=" + isEnabled
//				+ ", verificationCode=" + verificationCode + ", isActive=" + isActive + "]";
//	}
}
