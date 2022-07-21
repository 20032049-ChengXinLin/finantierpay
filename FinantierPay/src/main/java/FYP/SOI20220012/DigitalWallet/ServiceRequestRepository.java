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
* Date created: 2022-Jul-10 12:27:44 am
*
*/
package FYP.SOI20220012.DigitalWallet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author 20008303
 *
 */
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Integer> {
	
	public ServiceRequest findTopByOrderByDateTimeAsc();
	
	public List<ServiceRequest> findByAccount_AccountId(int accountid);

	public Page<ServiceRequest> findAllByDateTimeBetweenOrderByDateTimeDesc(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	
	public Page<ServiceRequest> findAllByStatusAndDateTimeBetweenOrderByDateTimeDesc(String status, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	
	@Query("select s from ServiceRequest s WHERE CONCAT(s.title, ' ', s.description, ' ') LIKE %?1% And s.dateTime BETWEEN ?2 AND ?3")
	public Page<ServiceRequest> findAllBetweenOrderByDateTimeDesc(String keyword, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	
	@Query("select s from ServiceRequest s WHERE CONCAT(s.title, ' ', s.description, ' ') LIKE %?1% And s.status =?2 And s.dateTime BETWEEN ?3 AND ?4")
	public Page<ServiceRequest> findAllByStatusAndKeywordAndDateTimeBetweenOrderByDateTimeDesc(String keyword, String status, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
