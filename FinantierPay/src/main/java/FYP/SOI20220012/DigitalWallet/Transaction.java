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
* Date created: 2022-May-23 6:14:20 pm
*
*/
package FYP.SOI20220012.DigitalWallet;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author 20032049
 *
 */
@Entity
public class Transaction {
	
	@Id
	private String transactionId;
	
	@ManyToOne
	@JoinColumn(name = "walletId", nullable = false)
	private Wallet wallet;
	
	private LocalDateTime dateTime;
	
	private String activity;
	
	private double amount;
	
	private double voucher_value;
	
	private String status;
	
	private String description;
	
	private String archive;
	
	private double points_earned;
	
	private boolean isDeleted = Boolean.FALSE;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate pointsExpiryDate;

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public Wallet getWallet() {
		return wallet;
	}

	public void setWallet(Wallet wallet) {
		this.wallet = wallet;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getVoucher_value() {
		return voucher_value;
	}

	public void setVoucher_value(double voucher_value) {
		this.voucher_value = voucher_value;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getArchive() {
		return archive;
	}

	public void setArchive(String archive) {
		this.archive = archive;
	}

	public double getPoints_earned() {
		return points_earned;
	}

	public void setPoints_earned(double points_earned) {
		this.points_earned = points_earned;
	}

	public LocalDate getPointsExpiryDate() {
		return pointsExpiryDate;
	}

	public void setPointsExpiryDate(LocalDate pointsExpiryDate) {
		this.pointsExpiryDate = pointsExpiryDate;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

}
