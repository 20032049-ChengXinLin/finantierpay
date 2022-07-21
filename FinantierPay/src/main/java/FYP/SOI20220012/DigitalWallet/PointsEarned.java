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
* Date created: 2022-Jul-08 8:41:47 pm
*
*/
package FYP.SOI20220012.DigitalWallet;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.format.annotation.DateTimeFormat;


/**
 * @author 20032049
 *
 */
@Entity
public class PointsEarned {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int pointsEarnedId;
	
	@ManyToOne
	@JoinColumn(name = "accountId")
	private Account account;
	
	private double pointsEarned;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate pointsExpiryDate;
	
    
	public int getPointsEarnedId() {
		return pointsEarnedId;
	}

	public void setPointsEarnedId(int pointsEarnedId) {
		this.pointsEarnedId = pointsEarnedId;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public double getPointsEarned() {
		return pointsEarned;
	}

	public void setPointsEarned(double pointsEarned) {
		this.pointsEarned = pointsEarned;
	}

	public LocalDate getPointsExpiryDate() {
		return pointsExpiryDate;
	}

	public void setPointsExpiryDate(LocalDate pointsExpiryDate) {
		this.pointsExpiryDate = pointsExpiryDate;
	}
}
