/**
*
* I declare that this code was written by me, 20008303.
* I will not copy or allow others to copy my code.
* I understand that copying code is considered as plagiarism.
*
* Student Name: Cheng Xin Lin (20032049), Koh Siew Gek (20008303), Chen Wan Ting (20009334)
* Team ID: SOI-2022-0012
* Team Project ID: SOI-2022-2210-0049
* Project: [IP] Digital Wallet
* Date created: 2022-Jul-10 12:21:44 am
*
*/
package FYP.SOI20220012.DigitalWallet;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.sun.istack.NotNull;

/**
 * @author 20008303
 *
 */

@Entity
public class ServiceRequest {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int serviceRequestId;
	
	@ManyToOne
	@JoinColumn(name = "accountId")
	private Account account;
	
	private LocalDateTime dateTime;
	
	@NotNull
	@NotEmpty(message = "Title cannot be empty")
	@Size(min = 8, max = 50, message = "Title length must be between 8 and 50 characters!")
	private String title;
	
	@NotNull
	@NotEmpty(message = "Description cannot be empty")
	@Size(min = 3, max = 200, message = "Description length must be between 3 and 200 characters!")
	private String description;
	
	private String status;
	
	
	public int getServiceRequestId() {
		return serviceRequestId;
	}

	public Account getAccount() {
		return account;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}
	
	public String getStatus() {
		return status;
	}

	public void setServiceRequestId(int serviceRequestId) {
		this.serviceRequestId = serviceRequestId;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
