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
* Date created: 2022-Jul-09 2:40:42 pm
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
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author 20032049
 *
 */

@Entity
@Table(name = "points_history")
public class PointsHistory {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int pointsHistoryId;
	
	@ManyToOne
	@JoinColumn(name = "pointsEarnedId")
	private PointsEarned pointsEarned;
	
	private double totalPoints;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime dateTime;
	
	private String status;
    
	private String archive;
	
	public int getPointsHistoryId() {
		return pointsHistoryId;
	}

	public void setPointsHistoryId(int pointsHistoryId) {
		this.pointsHistoryId = pointsHistoryId;
	}

	public PointsEarned getPointsEarned() {
		return pointsEarned;
	}

	public void setPointsEarned(PointsEarned pointsEarned) {
		this.pointsEarned = pointsEarned;
	}

	public double getTotalPoints() {
		return totalPoints;
	}

	public void setTotalPoints(double totalPoints) {
		this.totalPoints = totalPoints;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getArchive() {
		return archive;
	}

	public void setArchive(String archive) {
		this.archive = archive;
	}

}
