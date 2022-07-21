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
* Date created: 2022-Jun-23 1:05:37 pm
*
*/
package FYP.SOI20220012.DigitalWallet;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author 20032049
 *
 */
@Configuration
public class RewardsConfig implements WebMvcConfigurer {
	// add resource handler helps to configure the upload path as a resource path
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		String dirName = "uploads/";

		Path uploadDir = Paths.get(dirName);
		String uploadPath = uploadDir.toFile().getAbsolutePath();

		//registry.addResourceHandler("/" + dirName + "/**").addResourceLocations("file:/" + uploadPath + "/");

		// If the above does not work, comment line 38 and uncomment the line below.
		registry.addResourceHandler("/uploads/**").addResourceLocations("file:uploads/");
	}
}
