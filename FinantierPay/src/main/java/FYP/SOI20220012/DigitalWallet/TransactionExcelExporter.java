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
* Date created: 2022-Jul-17 11:20:53 am
*
*/
package FYP.SOI20220012.DigitalWallet;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author 20032049
 *
 */
public class TransactionExcelExporter {
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;
	private List<Transaction> listTransaction;

	public TransactionExcelExporter(List<Transaction> listTransaction) {
		this.listTransaction = listTransaction;
		workbook = new XSSFWorkbook();
	}

	private void writeHeaderLine() {
		sheet = workbook.createSheet("Transaction");

		Row row = sheet.createRow(0);

		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		font.setFontHeight(10);
		style.setFont(font);

		createCell(row, 0, "Transaction ID", style);
		createCell(row, 1, "Date/Time", style);
		createCell(row, 2, "Account ID", style);
		createCell(row, 3, "Wallet ID", style);
		createCell(row, 4, "Amount", style);
		createCell(row, 5, "Activity", style);
		createCell(row, 6, "Status", style);
		createCell(row, 7, "Voucher Value", style);
		createCell(row, 8, "Points Earned", style);
		createCell(row, 9, "Points Expiry Date", style);
		createCell(row, 10, "Description", style);
		createCell(row, 11, "Archive", style);

	}

	private void createCell(Row row, int columnCount, Object value, CellStyle style) {
		sheet.autoSizeColumn(columnCount);
		Cell cell = row.createCell(columnCount);
		if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
		} else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		} else if (value instanceof Double) {
			cell.setCellValue((Double) value);
		} else {
			cell.setCellValue((String) value);
		}
		cell.setCellStyle(style);
	}

	private void writeDataLines() {
		int rowCount = 1;

		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setFontHeight(10);
		style.setFont(font);

		for (int i = 0; i < listTransaction.size(); i++) {
			Row row = sheet.createRow(rowCount++);
			int columnCount = 0;

			createCell(row, columnCount++, listTransaction.get(i).getTransactionId().toString(), style);
			createCell(row, columnCount++, listTransaction.get(i).getDateTime().toString(), style);
			createCell(row, columnCount++, listTransaction.get(i).getWallet().getAccount().getAccountId(), style);
			createCell(row, columnCount++, listTransaction.get(i).getWallet().getWalletId(), style);
			createCell(row, columnCount++, listTransaction.get(i).getAmount(), style);
			createCell(row, columnCount++, listTransaction.get(i).getActivity(), style);
			createCell(row, columnCount++, listTransaction.get(i).getStatus(), style);
			createCell(row, columnCount++, listTransaction.get(i).getVoucher_value(), style);
			createCell(row, columnCount++, listTransaction.get(i).getPoints_earned(), style);
			if (listTransaction.get(i).getPointsExpiryDate() == null) {
				createCell(row, columnCount++, "null", style);
			} else {
				createCell(row, columnCount++, listTransaction.get(i).getDateTime().toString(), style);
			}
			
			createCell(row, columnCount++, listTransaction.get(i).getDescription(), style);
			createCell(row, columnCount++, listTransaction.get(i).getArchive(), style);
		}
	}

	public void export(HttpServletResponse response) throws IOException {
		writeHeaderLine();
		writeDataLines();

		ServletOutputStream outputStream = response.getOutputStream();
		workbook.write(outputStream);
		workbook.close();

		outputStream.close();

	}
}
