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
* Date created: 2022-May-22 12:06:08 am
*
*/
package FYP.SOI20220012.DigitalWallet;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.sun.istack.NotNull;

/**
 * @author 20008303
 *
 */

@Entity
public class Wallet {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int walletId;
	
	@ManyToOne
	@JoinColumn(name = "accountId", nullable = false)
	private Account account;
	
	private String pin;

	private double totalAmount;
	
	@Column(name = "reset_wallet_pin_token")
	private String resetWalletPinToken;

	private double paymentLimit;
	
    private boolean isDeleted = Boolean.FALSE;
    
	private LocalDateTime dateTimeofCreation;
	
	private LocalDateTime dateTimeofDeletion;
	
	public int getWalletId() {
		return walletId;
	}

	public String getPin() {
		return pin;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setWalletId(int walletId) {
		this.walletId = walletId;
	}
	
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getResetWalletPinToken() {
		return resetWalletPinToken;
	}

	public void setResetWalletPinToken(String resetWalletPinToken) {
		this.resetWalletPinToken = resetWalletPinToken;
	}

	public double getPaymentLimit() {
		return paymentLimit;
	}

	public void setPaymentLimit(double paymentLimit) {
		this.paymentLimit = paymentLimit;
	}


	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public LocalDateTime getDateTimeofCreation() {
		return dateTimeofCreation;
	}

	public void setDateTimeofCreation(LocalDateTime dateTimeofCreation) {
		this.dateTimeofCreation = dateTimeofCreation;
	}

	public LocalDateTime getDateTimeofDeletion() {
		return dateTimeofDeletion;
	}

	public void setDateTimeofDeletion(LocalDateTime dateTimeofDeletion) {
		this.dateTimeofDeletion = dateTimeofDeletion;
	}
	
	
}
