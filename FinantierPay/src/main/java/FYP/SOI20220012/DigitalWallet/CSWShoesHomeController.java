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
 * Date created: 2022-May-03 1:03:59 pm 
 * 
 */
package FYP.SOI20220012.DigitalWallet;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author 20032049
 *
 */
@Controller
public class CSWShoesHomeController {
	@Autowired
	private ProductsRepository ProductsRepository;
	
	@GetMapping("/cswshoes")
	public String home(Model model) {
		List<Products> listProducts = ProductsRepository.findAll();
		model.addAttribute("listProducts", listProducts);
		return "CSWShoes";
	}
	
}
